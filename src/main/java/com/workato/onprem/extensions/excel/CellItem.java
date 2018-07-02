package com.workato.onprem.extensions.excel;

public class CellItem {
	int colIndex;
	String value;
	
	public CellItem(int colIndex, String value) {
		this.colIndex = colIndex;
		this.value = value;
	}
}
