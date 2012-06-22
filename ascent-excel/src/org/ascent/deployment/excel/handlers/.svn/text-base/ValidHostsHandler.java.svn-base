package org.ascent.deployment.excel.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Sheet;

import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.Node;
import org.ascent.deployment.PlacementConstraint;
import org.ascent.deployment.excel.ExcelDeploymentConfigException;

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
public class ValidHostsHandler extends AbstractWorksheetHandler {

	private static final String VALID_HOST = "x";
	private static final String VALID_HOSTS_SHEET = "Valid Hosts";
	
	public String getWorksheetName() {
		return VALID_HOSTS_SHEET;
	}

	@Override
	public boolean isOptionalWorksheet() {
		return true;
	}

	@Override
	public void handleSheet(DeploymentConfig problem, Sheet validhosts,
			Map<String, Component> comps, Map<String, Node> nodes) {

		String[] headers = getHeaders(validhosts);
		int rows = getRowCount(validhosts);
		for (int i = 1; i < rows; i++) {
			String pk = getPrimaryKey(validhosts, i);
			Component comp = comps.get(pk);
			if (comp == null) {
				throw new ExcelDeploymentConfigException(
						"Undeclared component ID:" + pk,
						VALID_HOSTS_SHEET, i + 1, 1);

			}
			
			List<Node> valid = new ArrayList<Node>();
			for (int j = 0; j < headers.length; j++) {
				String val = validhosts.getCell(j+1, i).getContents().trim();
				
				if (val.equalsIgnoreCase(VALID_HOST)) {
					Node other = nodes.get(headers[j]);
					if (other == null) {
						throw new ExcelDeploymentConfigException(
								"Undeclared node ID:" + headers[j - 1],
								VALID_HOSTS_SHEET, i + 1, j + 1);

					}
					valid.add(other);					
				}
			}
			problem.getConstraints().add(new PlacementConstraint(comp,valid));
		}
		
	}

	
}
