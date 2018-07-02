package com.workato.onprem.extensions.excel;

import java.util.ArrayList;

public class RowItem {
	int rowIndex;
	ArrayList<CellItem> columns;
	
	RowItem(int rowIndex, ArrayList<CellItem> columns) {
	this.rowIndex = rowIndex;
	this.columns = columns;
	}
}
