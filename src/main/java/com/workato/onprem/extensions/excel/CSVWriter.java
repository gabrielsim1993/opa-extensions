package com.workato.onprem.extensions.excel;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVWriter {
	
	private final static Logger LOG = LoggerFactory.getLogger(CSVWriter.class);
	
	// Delimiter used in CSV file
	private static final String NEW_LINE_SEPARATOR = "\n";

	public static String writeCsvFile(ArrayList<ArrayList<String>> records) throws IOException {

		CSVPrinter csvFilePrinter = null;

		// Create the CSVFormat object with "\n" as a record delimiter
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

		StringBuilder sb = new StringBuilder();
		try {
			// initialize CSVPrinter object
			csvFilePrinter = new CSVPrinter(sb, csvFileFormat);

			for (ArrayList<String> row : records) {
				csvFilePrinter.printRecord(row);
			}
			

		} catch (Exception e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
			throw e;
			
		} finally {
			csvFilePrinter.close();
		}
			return sb.toString();
			
	}
}