package org.ascent.hamy;

import java.util.HashMap;

public class ExcelComponent {
	private String componentName;

	// key is sheet name. Rows is all values that come after the name
	private HashMap<String, String[]> rows = new HashMap<String, String[]>();

	public ExcelComponent(String componentName) {
		this.componentName = componentName;
	}

	public String toString() {
		return componentName;
	}

	public String[] getRowForSheet(String sheetName) {
		return rows.get(sheetName);
	}

	public void addRowForSheet(String sheet, String[] rowValues) {
		rows.put(sheet, rowValues);
	}

	public String getComponentName() {
		return componentName;
	}

	// The next two methods are needed to collections to locate half-completed
	// ExcelComponents and append information to them

	@Override
	public boolean equals(Object o) {
		return ((ExcelComponent) o).componentName.equals(componentName);
	}

	@Override
	public int hashCode() {
		return componentName.hashCode();
	}

}
