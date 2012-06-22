package org.ascent.excel;

import java.util.HashMap;
import java.util.Map;

import jxl.Cell;
import jxl.CellType;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;

/******************************************************************************
 * Copyright (c) 2007 Jules White.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jules White - initial API and implementation 
 ****************************************************************************/
public class WorksheetManipulator {

	protected Sheet getSheet(Workbook workbook, String sheetname){
		return getSheet(workbook, sheetname, false);
	}

	protected Sheet getSheet(Workbook workbook, String sheetname, boolean opt) {
		Sheet sheet = workbook.getSheet(sheetname);
		if (sheet == null && !opt) {
			throw new ExcelDeploymentConfigException(
					"The workbook is missing the required worksheet: \""
							+ sheetname + "\".");
		}
		return sheet;
	}

	
	public class Row {
		private String key_;
		private Map<String, Object> data_ = new HashMap<String, Object>();

		public String getKey() {
			return key_;
		}

		public void setKey(String key) {
			key_ = key;
		}

		public Map<String, Object> getData() {
			return data_;
		}

		public void setData(Map<String, Object> data) {
			data_ = data;
		}
	}
	
	public String getPrimaryKey(Sheet sheet, int row) {
		return sheet.getCell(0, row).getContents();
	}

	public int[] getIntResources(Sheet sheet, int row) {
		int tc = getColumnCount(sheet);
		int[] nres = new int[tc - 1];
		for (int j = 1; j < tc; j++) {
			nres[j - 1] = Integer.parseInt(sheet.getCell(j, row).getContents());
		}
		return nres;
	}

	public float[] getFloatResources(Sheet sheet, int row) {
		int tc = getColumnCount(sheet);
		float[] nres = new float[tc - 1];
		for (int j = 1; j < tc; j++) {
			nres[j - 1] = Float.parseFloat(sheet.getCell(j, row).getContents());
		}
		return nres;
	}

	public Row getRow(Sheet sheet, String[] cols, int row) {
		String pk = getPrimaryKey(sheet, row);
		Row robj = new Row();
		robj.setKey(pk);
		for (int i = 1; i < cols.length; i++) {
			Cell cell = sheet.getCell(i, row);
			if (cell.getType() == CellType.NUMBER)
				robj.getData().put(cols[i - 1], ((NumberCell) cell).getValue());
			else {
				String val = cell.getContents().trim();

				if (val.length() > 0)
					robj.getData().put(cols[i - 1], val);
			}
		}
		return robj;
	}

	public String[] getHeaders(Sheet sheet) {
		return getHeaders(sheet, 1);
	}

	public String[] getHeaders(Sheet sheet, int off) {
		// Find the names of the headers
		int tc = getColumnCount(sheet);
		int totalres = tc - 1;
		String[] head = new String[totalres];
		for (int i = off; i < tc; i++) {
			head[i - 1] = sheet.getCell(i, 0).getContents().trim();
		}
		return head;
	}

	public int getColumnCount(Sheet sheet) {
		int max = sheet.getColumns();
		int real = 0;			// Changed 1 to 0 here to catch empty sheets
		for (int i = 0; i < max; i++) {		// Changed 1 to 0 here also
			if (sheet.getCell(i, 0).getContents().trim().length() > 0) {
				real++;
			} else {
				break;
			}
		}
		return real;
	}

	public int getInt(Sheet sheet, int col, int row) {
		String val = sheet.getCell(col, row).getContents().trim();
		if (val.length() < 1)
			return 0;
		return Integer.parseInt(val);
	}

	public int getRowCount(Sheet sheet) {
		int max = sheet.getRows();
		int real = 0;			// Changed 1 to 0 here to catch empty sheets
		for (int i = 0; i < max; i++) {		// Changed 1 to 0 here also
			if (sheet.getCell(0, i).getContents().trim().length() > 0) {
				real++;
			} else {
				break;
			}
		}
		return real;
	}
}
