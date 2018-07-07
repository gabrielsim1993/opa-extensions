package com.workato.onprem.extensions.excel;

import java.util.ArrayList;

public class WorkbookMetadata {
	ArrayList<SheetMetadata> sheets;
	
	WorkbookMetadata(ArrayList<SheetMetadata> sheets) {
		this.sheets = sheets;
	}
}
