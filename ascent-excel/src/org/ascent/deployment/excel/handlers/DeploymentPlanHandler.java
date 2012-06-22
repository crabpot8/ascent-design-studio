package org.ascent.deployment.excel.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Sheet;

import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.Node;
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
public class DeploymentPlanHandler extends AbstractWorksheetHandler {

	public static final String DEPLOYMENT_PLAN_SHEET = "Deployment Plan";

	public String getWorksheetName() {
		return DEPLOYMENT_PLAN_SHEET;
	}

	public boolean isOptionalWorksheet() {
		return true;
	}
	
	public void handleSheet(DeploymentConfig problem, Sheet dplan,
			Map<String, Component> comps, Map<String, Node> nodes) {
		handleSheetImpl(problem, dplan, comps, nodes);
	}

	public void handleSheetImpl(Object problem, Sheet dplan,
			Map<String, Component> comps, Map<String, Node> nodes) {

		String[] headers = getHeaders(dplan);
		int rows = getRowCount(dplan);
		for (int i = 1; i < rows; i++) {
			String pk = getPrimaryKey(dplan, i);
			Component comp = comps.get(pk);
			if (comp == null) {
				throw new ExcelDeploymentConfigException(
						"Undeclared component ID:" + pk,
						DEPLOYMENT_PLAN_SHEET, i + 1, 1);

			}
			
			List<Node> valid = new ArrayList<Node>();
			for (int j = 0; j < headers.length; j++) {
				String val = dplan.getCell(j+1, i).getContents().trim();
				
				if (val.trim().length() > 0) {
					Node other = nodes.get(headers[j]);
					if (other == null) {
						throw new ExcelDeploymentConfigException(
								"Undeclared node ID:" + headers[j - 1],
								DEPLOYMENT_PLAN_SHEET, i + 1, j + 1);

					}
					place(comp,other,problem);
					break;
				}
			}
			
		}
	}
	
	public void place(Component c, Node n, Object plan){
		if(plan instanceof DeploymentConfig){
			((DeploymentConfig)plan).getPrePlacedComponents().put(c,n);
		}
		else if(plan instanceof DeploymentPlan){
			((DeploymentPlan)plan).moveTo(c, n);
		}
	}
}
