package com.workato.onprem.extensions.excel;

import java.util.ArrayList;

public class SheetItem {
	String sheetName;
	ArrayList<RowItem> rows;
	
	SheetItem(String sheetName, ArrayList<RowItem> rows) {
		this.sheetName = sheetName;
		this.rows =  rows;
	}
}
