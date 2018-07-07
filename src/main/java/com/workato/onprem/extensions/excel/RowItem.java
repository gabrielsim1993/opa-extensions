package com.workato.onprem.extensions.excel;

import java.util.ArrayList;

public class RowItem {
	/*
	 * r - rowIndex
	 * Singular character keys minimize response payload
	 */
	int r;
	ArrayList<CellItem> columns;
	
	RowItem(int rowIndex, ArrayList<CellItem> columns) {
	this.r = rowIndex;
	this.columns = columns;
	}
}
