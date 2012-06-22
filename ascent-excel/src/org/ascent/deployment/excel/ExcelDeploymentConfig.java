package org.ascent.deployment.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.Node;
import org.ascent.deployment.excel.handlers.ComponentColocationHandler;
import org.ascent.deployment.excel.handlers.ComponentHandler;
import org.ascent.deployment.excel.handlers.ComponentSchedulingHandler;
import org.ascent.deployment.excel.handlers.InteractionHandler;
import org.ascent.deployment.excel.handlers.NetworkHandler;
import org.ascent.deployment.excel.handlers.NodeHandler;
import org.ascent.deployment.excel.handlers.ValidHostsHandler;
import org.ascent.deployment.excel.handlers.WorksheetHandler;
import org.ascent.hamy.OptionalComponentCallback;

/*******************************************************************************
 * Copyright (c) 2007 Jules White. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jules White - initial API and implementation
 ******************************************************************************/
public class ExcelDeploymentConfig extends WorksheetManipulator {

	private List<WorksheetHandler> handlers_ = new ArrayList<WorksheetHandler>();

	public ExcelDeploymentConfig() {
		handlers_.add(new NodeHandler());
		handlers_.add(new ComponentHandler(null));
		handlers_.add(new ComponentSchedulingHandler());
		handlers_.add(new ComponentColocationHandler());
		handlers_.add(new NetworkHandler());
		handlers_.add(new InteractionHandler());
		handlers_.add(new ValidHostsHandler());
	}

	public ExcelDeploymentConfig(OptionalComponentCallback callback) {
		handlers_.add(new NodeHandler());
		handlers_.add(new ComponentHandler(callback));
		handlers_.add(new ComponentSchedulingHandler(callback));
		handlers_.add(new ComponentColocationHandler());
		handlers_.add(new NetworkHandler());
		handlers_.add(new InteractionHandler());
		handlers_.add(new ValidHostsHandler());
	}

	public void load(File f, DeploymentConfig problem) throws Exception {
		Workbook workbook = Workbook.getWorkbook(f);

		try {
			HashMap<String, Component> complookup = new HashMap<String, Component>();
			HashMap<String, Node> nodelookup = new HashMap<String, Node>();

			for (WorksheetHandler handler : handlers_) {
				Sheet sheet = getSheet(workbook, handler.getWorksheetName(),
						handler.isOptionalWorksheet());
				if (sheet != null)
					handler.handleSheet(problem, sheet, complookup, nodelookup);
			}

		} finally {
			workbook.close();
		}
	}

	public void loadOptionalComponents(ExcelComponent current,
			List<ExcelComponent> included) throws Exception {
		Workbook main = Workbook.getWorkbook(new File("data/temp.xls"));
		WritableWorkbook workbook = Workbook.createWorkbook(new File(
				"data/temp2.xls"), main);

		WritableSheet sheet = workbook
				.getSheet(ComponentHandler.COMPONENTS_RESOURCES_SHEET);

		int rows = getRowCount(sheet);

		for (int i = rows; i < rows + included.size() + 1; i++) {
			if (i == rows) {
				sheet.addCell(new Label(0, i, current.id));

				double[] row = current.rows
						.get(ComponentHandler.COMPONENTS_RESOURCES_SHEET);
				for (int j = 1; j < row.length; j++)
					sheet.addCell(new jxl.write.Number(j, i, row[j]));
			} else {
				ExcelComponent componentForRow = included.get(i - rows - 1);

				sheet.addCell(new Label(0, i, componentForRow.id));
				double[] row = componentForRow.rows
						.get(ComponentHandler.COMPONENTS_RESOURCES_SHEET);
				for (int j = 1; j < row.length; j++)
					sheet.addCell(new jxl.write.Number(j, i, row[j]));
			}
		}

		sheet = workbook
				.getSheet(ComponentSchedulingHandler.COMPONENTS_SCHEDULING_SHEET);
		rows = getRowCount(sheet);

		for (int i = rows; i < rows + included.size() + 1; i++) {
			if (i == rows) {
				sheet.addCell(new Label(0, i, current.id));
				double[] row = current.rows
						.get(ComponentSchedulingHandler.COMPONENTS_SCHEDULING_SHEET);
				for (int j = 1; j < row.length; j++)
					sheet.addCell(new jxl.write.Number(j, i, row[j]));
			} else {
				ExcelComponent componentForRow = included.get(i - rows - 1);

				sheet.addCell(new Label(0, i, componentForRow.id));
				double[] row = componentForRow.rows
						.get(ComponentSchedulingHandler.COMPONENTS_SCHEDULING_SHEET);
				for (int j = 1; j < row.length; j++)
					sheet.addCell(new jxl.write.Number(j, i, row[j]));
			}
		}

		workbook.write();
		workbook.close();
	}

	/*public void trimOptionalComponents(File f) throws Exception {
		Workbook main = Workbook.getWorkbook(f);
		WritableWorkbook workbook = Workbook.createWorkbook(new File(
				"data/temp.xls"), main);

		WritableSheet sheet = workbook
				.getSheet(ComponentHandler.COMPONENTS_RESOURCES_SHEET);

		int rows = getRowCount(sheet);
		int cols = getColumnCount(sheet);

		for (int i = 1; i < rows; i++) {
			String id = getPrimaryKey(sheet, i);
			if (id.startsWith("Opt")) {
				for (int j = 0; j < cols; j++)
					sheet.addCell(new Label(j, i, ""));
			}

		}

		sheet = workbook
				.getSheet(ComponentSchedulingHandler.COMPONENTS_SCHEDULING_SHEET);
		rows = getRowCount(sheet);
		cols = getColumnCount(sheet);

		for (int i = 1; i < rows; i++) {
			String id = getPrimaryKey(sheet, i);
			if (id.startsWith("Opt")) {
				for (int j = 0; j < cols; j++)
					sheet.addCell(new Label(j, i, ""));
			}

		}

		workbook.write();
		workbook.close();
	}*/

	// Given an input, saves data/temp.xls without the optional components
	public HashMap<String, ExcelComponent> getAndTrimOptionalComponentIDs(File f)
			throws Exception {
		Workbook main = Workbook.getWorkbook(f);
		WritableWorkbook workbook = Workbook.createWorkbook(new File(
				"data/temp.xls"), main);

		WritableSheet sheet = workbook
				.getSheet(ComponentHandler.COMPONENTS_RESOURCES_SHEET);

		int rows = getRowCount(sheet);
		int cols = getColumnCount(sheet);
		HashMap<String, ExcelComponent> components = new HashMap<String, ExcelComponent>();

		for (int i = 1; i < rows; i++) {
			String id = getPrimaryKey(sheet, i);
			if (id.startsWith("Opt")) {
				double[] row = new double[cols];
				for (int j = 0; j < cols; j++) {
					if (j != 0)
						row[j] = Double.parseDouble(sheet.getCell(j, i)
								.getContents());
					sheet.addCell(new Label(j, i, ""));
				}

				ExcelComponent c = new ExcelComponent();
				c.id = id;
				c.rows.put(ComponentHandler.COMPONENTS_RESOURCES_SHEET, row);
				components.put(id, c);
			}
		}

		sheet = workbook
				.getSheet(ComponentSchedulingHandler.COMPONENTS_SCHEDULING_SHEET);
		rows = getRowCount(sheet);
		cols = getColumnCount(sheet);

		for (int i = 1; i < rows; i++) {
			String id = getPrimaryKey(sheet, i);
			if (id.startsWith("Opt")) {
				double[] row = new double[cols];
				for (int j = 0; j < cols; j++) {
					if (j != 0)
						if (sheet.getCell(j, i).getContents().equals(""))
							row[j] = 0;
						else
							row[j] = Double.parseDouble(sheet.getCell(j, i)
									.getContents().replace("%", ""));
					sheet.addCell(new Label(j, i, ""));
				}

				components.get(id).rows.put(
						ComponentSchedulingHandler.COMPONENTS_SCHEDULING_SHEET,
						row);

			}

		}

		workbook.write();
		workbook.close();
		return components;
	}

	public static class ExcelComponent {
		String id;

		// key is sheet name. Rows is all values that come after the name
		HashMap<String, double[]> rows = new HashMap<String, double[]>();

		@Override
		public boolean equals(Object o) {
			return ((ExcelComponent) o).id.equals(id);
		}

		@Override
		public int hashCode() {
			return id.hashCode();
		}

		public String toString() {
			return id;
		}

	}

}
