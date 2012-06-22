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
public class NodeHandler extends AbstractWorksheetHandler {

	public static final String NODES_SHEET = "Nodes";

	public String getWorksheetName() {
		return NODES_SHEET;
	}

	@Override
	public void handleSheet(DeploymentConfig problem, Sheet nodes,
			Map<String, Component> comps, Map<String, Node> nodemap) {

		int rows = getRowCount(nodes);

		// Load the resources available on each node
		for (int i = 1; i < rows; i++) {
			try {
				int[] nres = getIntResources(nodes, i);
				// Create the node
				Node n = problem.addNode(getPrimaryKey(nodes, i), nres);

				nodemap.put(n.getLabel(), n);
			} catch (Exception e) {
				throw new ExcelDeploymentConfigException(
						"Invalid resource specification (a non-number is in the row)",
						NODES_SHEET, i + 1, -1);
			}
		}
	}

}
