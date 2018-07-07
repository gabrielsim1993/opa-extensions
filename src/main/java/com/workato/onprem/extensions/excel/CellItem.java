package com.workato.onprem.extensions.excel;

public class CellItem {
	/*
	 * c - columnIndex
	 * v - value
	 * Singular character keys minimize response payload
	 */
	int c;
	String v;
	
	public CellItem(int colIndex, String value) {
		this.c = colIndex;
		this.v = value;
	}
}
