package org.ascent.excel;
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
public class ExcelDeploymentConfigException extends RuntimeException{

	private String sheet_;
	private int row_;
	private int column_;
	
	
	
	public ExcelDeploymentConfigException(String errormsg, String sheet, int row, int column) {
		super(errormsg);
		sheet_ = sheet;
		row_ = row;
		column_ = column;
	}
	
	public ExcelDeploymentConfigException(String errormsg, Exception e, String sheet, int row, int column) {
		super(errormsg,e);
		sheet_ = sheet;
		row_ = row;
		column_ = column;
	}

	public ExcelDeploymentConfigException() {
		super();
	}

	public ExcelDeploymentConfigException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ExcelDeploymentConfigException(String arg0) {
		super(arg0);
	}

	public ExcelDeploymentConfigException(Throwable arg0) {
		super(arg0);
	}

	public String getSheet() {
		return sheet_;
	}

	public void setSheet(String sheet) {
		sheet_ = sheet;
	}

	public int getRow() {
		return row_;
	}

	public void setRow(int row) {
		row_ = row;
	}

	public int getColumn() {
		return column_;
	}

	public void setColumn(int column) {
		column_ = column;
	}

	@Override
	public String getMessage() {
		String row = (row_ > -1)? ""+row_ : "unknown";
		String col = (column_ > -1)? ""+column_ : "unknown";
		return super.getMessage() + " [sheet:"+sheet_+", column:"+col+", row:"+row+"]";
	}

}
