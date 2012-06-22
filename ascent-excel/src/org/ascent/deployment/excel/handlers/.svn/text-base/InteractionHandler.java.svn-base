package org.ascent.deployment.excel.handlers;

import java.util.Map;

import jxl.Sheet;

import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.Node;
import org.ascent.deployment.excel.ExcelDeploymentConfigException;
import org.ascent.deployment.excel.WorksheetManipulator;
import org.ascent.deployment.excel.WorksheetManipulator.Row;

/*******************************************************************************
 * Copyright (c) 2007 Jules White. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jules White - initial API and implementation
 ******************************************************************************/
public class InteractionHandler extends AbstractWorksheetHandler {

	public static final String INTERACTIONS_SHEET = "Component Interactions";
	public static final String ISOURCE = "Sender";
	public static final String ITARGET = "Receiver";
	public static final String IRATE = "Transmit Rate";
	public static final String ISIZE = "Length";

	public String getWorksheetName() {
		return INTERACTIONS_SHEET;
	}

	@Override
	public void handleSheet(DeploymentConfig problem, Sheet interacts,
			Map<String, Component> comps, Map<String, Node> nodes) {
		int rows = getRowCount(interacts);
		String[] headers = getHeaders(interacts);
		for (int i = 1; i < rows; i++) {
			Row row = getRow(interacts, headers, i);
			String src = "" + row.getData().get(ISOURCE);
			if (src == null) {
				throw new ExcelDeploymentConfigException(
						"Missing required column:" + ISOURCE
								+ " from worksheet:" + INTERACTIONS_SHEET,
						INTERACTIONS_SHEET, i + 1, -1);
			}
			Component sender = comps.get(src);
			if (sender == null) {
				throw new ExcelDeploymentConfigException(
						"Undeclared component ID:" + src + " from worksheet:"
								+ INTERACTIONS_SHEET + " in " + ISOURCE
								+ " column", INTERACTIONS_SHEET, i + 1, -1);
			}

			String trg = "" + row.getData().get(ITARGET);
			if (trg == null) {
				throw new ExcelDeploymentConfigException(
						"Missing required column:" + ITARGET
								+ " from worksheet:" + INTERACTIONS_SHEET,
						INTERACTIONS_SHEET, i + 1, -1);
			}
			Component receiver = comps.get(trg);
			if (receiver == null) {
				throw new ExcelDeploymentConfigException(
						"Undeclared component ID:" + trg + " from worksheet:"
								+ INTERACTIONS_SHEET + " in " + ITARGET
								+ " column", INTERACTIONS_SHEET, i + 1, -1);
			}

			double rate = 0;
			try {
				rate = (Double) row.getData().get(IRATE);
			} catch (Exception e) {
				throw new ExcelDeploymentConfigException(
						"Invalid rate specification (not a number):"
								+ row.getData().get(IRATE) + " from worksheet:"
								+ INTERACTIONS_SHEET + " in " + IRATE
								+ " column", INTERACTIONS_SHEET, i + 1, -1);
			}

			int size = 0;
			try {
				size = (int) Math.rint((Double) (row.getData().get(ISIZE)));
			} catch (Exception e) {
				throw new ExcelDeploymentConfigException(
						"Invalid size specification (not a number):"
								+ row.getData().get(ISIZE) + " from worksheet:"
								+ INTERACTIONS_SHEET + " in " + ISIZE
								+ " column", INTERACTIONS_SHEET, i + 1, -1);
			}

			problem.addInteraction(row.getKey(), new int[] { size }, rate,
					new Component[] { sender, receiver });
		}
	}

}
