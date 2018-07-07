/*
 * Copyright (c) 2018 Workato, Inc. All rights reserved.
 */
package com.workato.onprem.extensions.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.monitorjbl.xlsx.StreamingReader;


public class ExcelParser {

	private final static Logger LOG = LoggerFactory.getLogger(ExcelParser.class);

	public static String parseExcelByPath(String filepath, int sheetIndex, int offset, int batchSize) throws FileNotFoundException{
		Workbook workbook = readExcelByPath(filepath);
		
		Sheet s = workbook.getSheetAt(sheetIndex);
		ArrayList<RowItem> sheetData = parseSheet(s, offset, batchSize);
		Gson gson = new Gson();
		SheetItem sheetItem = new SheetItem(s.getSheetName(), sheetData);
		String json = gson.toJson(sheetItem);
		
		return json;
	}
	
	
	public static String parseExcelMetadataByPath(String filepath) throws FileNotFoundException {
		Workbook workbook = readExcelByPath(filepath);
		
		ArrayList<SheetMetadata> sheetsMetadata = new ArrayList<SheetMetadata>();
		
		int sheetIdx = 0;
		for (Sheet sheet : workbook) {
			int numRows = 0, numCols = 0;
			
			for (Row row : sheet) {
				if (numRows == 0) {
					for (@SuppressWarnings("unused") Cell cell : row) {
						numCols++;
					}
				}
				numRows++;
			}
			sheetsMetadata.add(new SheetMetadata(sheet.getSheetName(), sheetIdx, numRows, numCols));
			sheetIdx++;
		};
		WorkbookMetadata workbookMetadata = new WorkbookMetadata(sheetsMetadata);
		
		Gson gson = new Gson();
		String json = gson.toJson(workbookMetadata);
		
		return json;
	}

	private static Workbook readExcelByPath(String filepath) throws FileNotFoundException {
		InputStream is = new FileInputStream(new File(filepath));
		Workbook workbook = StreamingReader.builder()
		        .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
		        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
		        .open(is);            // InputStream or File for XLSX file (required)
		return workbook;
	}
	
	private static ArrayList<RowItem> parseSheet(Sheet sheet, int offset, int batchSize) {
		String cellValue;
		ArrayList<CellItem> columns;
		ArrayList<RowItem> rows = new ArrayList<RowItem>();
		
		for (Row r : sheet) {
			if (r.getRowNum() < offset) {
				continue;
			}
			
			if ((r.getRowNum() - offset) > batchSize) {
				break;
			}
			
			columns = new ArrayList<CellItem>();
			
			for (Cell c : r) {
				switch (c.getCellTypeEnum()) {
					case _NONE:
						cellValue = "";
						break;
					case BLANK:
						cellValue = "";
						break;
					case BOOLEAN:
						cellValue = Boolean.toString(c.getBooleanCellValue());
						break;
					case ERROR:
						cellValue = "";
						break;
					case FORMULA:
						switch (c.getCachedFormulaResultTypeEnum()) {
							case NUMERIC:
								cellValue = Double.toString(c.getNumericCellValue());
								break;
							case STRING:
								cellValue = c.getRichStringCellValue().getString();
								break;
							default:
								try {
									cellValue = c.getStringCellValue();
								}
								catch (Exception e) {
									LOG.error("Error deciphering formula at Cell " + c.getAddress());
									cellValue = "";
								}
							}
						break;
					case STRING:
						cellValue = c.getStringCellValue();
						break;
					case NUMERIC:
						cellValue = Double.toString(c.getNumericCellValue());
						break;
					default:
						cellValue = "";
						LOG.error("Error deciphering type at Cell " + c.getAddress());
						break;
				}
				columns.add(new CellItem(c.getColumnIndex(), cellValue));
			}
			rows.add(new RowItem(r.getRowNum(), columns));
		}
		return rows;
	}


}
