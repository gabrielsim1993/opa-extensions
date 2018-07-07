package com.workato.onprem.extensions.excel;

public class SheetMetadata {
	String name;
	int index, rows, cols;
	
	SheetMetadata(String name, int index, int rows, int cols) {
		this.name = name;
		this.index = index;
		this.rows =  rows;
		this.cols = cols;
	}
}
