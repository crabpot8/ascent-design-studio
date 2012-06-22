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
public class ComponentHandler extends AbstractWorksheetHandler {

	public static final String COMPONENTS_RESOURCES_SHEET = "Component Resources";

	public String getWorksheetName() {
		return COMPONENTS_RESOURCES_SHEET;
	}

	@Override
	public void handleSheet(DeploymentConfig problem, Sheet comps,
			Map<String, Component> complookup, Map<String, Node> nodes) {

		int rows = getRowCount(comps);
		
		for (int i = 1; i < rows; i++) {
			try {
				int[] cres = getIntResources(comps, i);
				Component c = problem.addComponent(getPrimaryKey(comps, i),
						cres);

				complookup.put(c.getLabel(), c);
			} catch (Exception e) {
				throw new ExcelDeploymentConfigException(
						"Invalid resource specification (a non-number is in the row)",
						COMPONENTS_RESOURCES_SHEET, i + 1, -1);
			}
		}

	}

}
