package org.ascent.deployment.excel.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Sheet;

import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.NetworkLink;
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
public class NetworkHandler extends AbstractWorksheetHandler {

	public static final String NETWORK_RESOURCES_SHEET = "Networks";

	public String getWorksheetName() {
		return NETWORK_RESOURCES_SHEET;
	}

	@Override
	public void handleSheet(DeploymentConfig problem, Sheet networks,
			Map<String, Component> comps, Map<String, Node> nodelookup) {
		String[] headers = getHeaders(networks);
		int rows = getRowCount(networks);

		NetworkLink[] links = new NetworkLink[rows - 1];

		for (int i = 0; i < links.length; i++) {
			List<Node> nodes = new ArrayList<Node>();

			int[] bandwidth = null;
			try {
				bandwidth = new int[] { getInt(networks, 1, i + 1) };
			} catch (Exception e) {
				throw new ExcelDeploymentConfigException(
						"Invalid network resource specification", e,
						NETWORK_RESOURCES_SHEET, i + 2, 2);
			}
			for (int j = 1; j < headers.length; j++) {
				String val = networks.getCell(j + 1, i + 1).getContents()
						.trim();
				if (val.length() > 0) {
					Node node = nodelookup.get(headers[j]);
					if (node == null) {
						throw new ExcelDeploymentConfigException(
								"Undeclared node ID:" + headers[j],
								NETWORK_RESOURCES_SHEET, i + 1, j + 2);
					}
					nodes.add(node);
				}
			}

			String key = getPrimaryKey(networks, i + 1);
			if (key == null || key.length() < 1) {
				throw new ExcelDeploymentConfigException(
						"Invalid network identifier (null or missing):" + key,
						NETWORK_RESOURCES_SHEET, i + 2, 1);
			}
			links[i] = problem.addNetwork(key, nodes.toArray(new Node[0]),
					bandwidth);
		}
	}

}
