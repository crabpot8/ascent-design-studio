package org.ascent.deployment.excel.handlers;

import java.util.Map;

import jxl.Sheet;

import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.Node;
import org.ascent.deployment.excel.ExcelDeploymentConfigException;

/*******************************************************************************
 * Copyright (c) 2007 Jules White. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jules White - initial API and implementation
 ******************************************************************************/
public class ComponentColocationHandler extends AbstractWorksheetHandler {

	public static final String EXCLUDES_COLOCATION = "x";
	public static final String REQUIRES_COLOCATION = "r";
	public static final String COMPONENTS_COLOCATION_SHEET = "Component Co-location";

	public String getWorksheetName() {
		return COMPONENTS_COLOCATION_SHEET;
	}

	
	public void handleSheet(DeploymentConfig problem, Sheet colocation,
			Map<String, Component> comps, Map<String, Node> nodes) {
		String[] headers = getHeaders(colocation);
		int rows = getRowCount(colocation);
		for (int i = 1; i < rows; i++) {
			String pk = getPrimaryKey(colocation, i);
			Component comp = comps.get(pk);
			if (comp == null) {
				throw new ExcelDeploymentConfigException(
						"Undeclared component ID:" + pk,
						COMPONENTS_COLOCATION_SHEET, i + 1, 1);

			}
			for (int j = 1; j < headers.length; j++) {
				String val = colocation.getCell(j, i).getContents().trim();
				if (val.equalsIgnoreCase(REQUIRES_COLOCATION)) {
					Component other = comps.get(headers[j - 1]);
					if (other == null) {
						throw new ExcelDeploymentConfigException(
								"Undeclared component ID:" + headers[j - 1],
								COMPONENTS_COLOCATION_SHEET, i + 1, j + 1);

					}
					problem.requireColocated(comp, other);
				} else if (val.equalsIgnoreCase(EXCLUDES_COLOCATION)) {
					Component other = comps.get(headers[j - 1]);
					if (other == null) {
						throw new ExcelDeploymentConfigException(
								"Undeclared component ID:" + headers[j - 1],
								COMPONENTS_COLOCATION_SHEET, i + 1, j + 1);

					}
					problem.requireNotColocated(comp, other);
				}
			}
		}
	}

}
