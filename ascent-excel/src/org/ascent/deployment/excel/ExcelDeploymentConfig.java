package org.ascent.deployment.excel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

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
import org.ascent.hamy.ExcelComponent;

/*******************************************************************************
 * Copyright (c) 2007 Jules White. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jules White - initial API and implementation
 ******************************************************************************/
public class ExcelDeploymentConfig extends WorksheetManipulator {
	private static final Logger log = Logger
			.getLogger(ExcelDeploymentConfig.class.getName());

	/**
	 * Components with this prefix in their primary label will be marked as
	 * optional
	 */
	public static final String OPTIONAL_COMPONENT_PREFIX = "Opt_";

	private List<WorksheetHandler> handlers_ = new ArrayList<WorksheetHandler>();

	public ExcelDeploymentConfig() {
		log.finer("");
		handlers_.add(new NodeHandler());
		handlers_.add(new ComponentHandler());
		handlers_.add(new ComponentSchedulingHandler());
		handlers_.add(new ComponentColocationHandler());
		handlers_.add(new NetworkHandler());
		handlers_.add(new InteractionHandler());
		handlers_.add(new ValidHostsHandler());
	}

	public void load(File f, DeploymentConfig problem) throws Exception {
		log.finer("");
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

	/**
	 * Given a required configuration and a number of optional configuration
	 * elements, stored as {@link ExcelComponent}s, this creates one excel file
	 * that contains both required and all passed optional components
	 * 
	 * @param current
	 * @param included
	 * @param requiredConfig
	 * @param result
	 *            Where the resulting excel file should be stored
	 * @throws Exception
	 */
	public void loadOptionalComponents(ExcelComponent current,
			List<ExcelComponent> included, File requiredConfig, File result)
			throws Exception {
		log.finer("");
		Workbook main = Workbook.getWorkbook(requiredConfig);
		WritableWorkbook workbook = Workbook.createWorkbook(result, main);

		WritableSheet sheet = workbook
				.getSheet(ComponentHandler.COMPONENTS_RESOURCES_SHEET);
		addOptionalComponentsToSheet(sheet,
				ComponentHandler.COMPONENTS_RESOURCES_SHEET, current, included);
		sheet = workbook
				.getSheet(ComponentSchedulingHandler.COMPONENTS_SCHEDULING_SHEET);
		addOptionalComponentsToSheet(sheet,
				ComponentSchedulingHandler.COMPONENTS_SCHEDULING_SHEET,
				current, included);

		workbook.write();
		workbook.close();
		main.close();
	}

	private void addOptionalComponentsToSheet(WritableSheet sheet,
			String sheetId, ExcelComponent current,
			List<ExcelComponent> included) throws RowsExceededException,
			WriteException {
		int rows = getRowCount(sheet);

		// Add the current component
		sheet.addCell(new Label(0, rows, current.getComponentName()));
		String[] row = current.getRowForSheet(sheetId);
		for (int j = 1; j < row.length; j++)
			sheet.addCell(new Label(j, rows, row[j]));

		for (int r = rows + 1; r < rows + included.size() + 1; r++) {

			ExcelComponent component = included.get(r - rows - 1);

			sheet.addCell(new Label(0, r, component.getComponentName()));
			row = component.getRowForSheet(sheetId);
			for (int j = 1; j < row.length; j++)
				sheet.addCell(new Label(j, r, row[j]));
		}

	}

	/**
	 * Given an excel workbook as input, this creates data/temp.xls, which is
	 * the exact same {@link DeploymentConfig} but with all optional components
	 * removed. Optional components are identified by component names that start
	 * with 'Opt'
	 */
	public HashMap<String, ExcelComponent> getAndTrimOptionalComponentIDs(
			File input, File output) throws Exception {
		log.finer("");
		Workbook main = Workbook.getWorkbook(input);
		WritableWorkbook workbook = Workbook.createWorkbook(output, main);

		HashMap<String, ExcelComponent> components = new HashMap<String, ExcelComponent>();
		WritableSheet sheet = workbook
				.getSheet(ComponentHandler.COMPONENTS_RESOURCES_SHEET);
		log.finest("Trimming " + ComponentHandler.COMPONENTS_RESOURCES_SHEET);
		trimOptionalComponentsFromSheet(components, sheet,
				ComponentHandler.COMPONENTS_RESOURCES_SHEET);
		sheet = workbook
				.getSheet(ComponentSchedulingHandler.COMPONENTS_SCHEDULING_SHEET);
		log.finest("Trimming "
				+ ComponentSchedulingHandler.COMPONENTS_SCHEDULING_SHEET);
		trimOptionalComponentsFromSheet(components, sheet,
				ComponentSchedulingHandler.COMPONENTS_SCHEDULING_SHEET);

		workbook.write();
		workbook.close();
		main.close();

		log.finest("Removed " + components.size() + " optional components");
		return components;
	}

	private void trimOptionalComponentsFromSheet(
			HashMap<String, ExcelComponent> storage, WritableSheet sheet,
			String sheetId) throws RowsExceededException, WriteException {
		log.finer("");
		int rows = getRowCount(sheet);
		int cols = getColumnCount(sheet);

		for (int r = 1; r < rows; r++) {
			String id = getPrimaryKey(sheet, r);
			if (id.startsWith("Opt")) {

				sheet.addCell(new Label(0, r, ""));
				String[] row = new String[cols];
				for (int c = 1; c < cols; c++) {
					String contents = sheet.getCell(c, r).getContents();
					row[c] = contents;
					sheet.addCell(new Label(c, r, ""));
				}

				ExcelComponent c = storage.get(id);
				if (c == null)
					c = new ExcelComponent(id);
				c.addRowForSheet(sheetId, row);
				storage.put(id, c);
			}
		}
	}
}
