package org.ascent.deployment.excel.handlers;

import java.util.Map;

import jxl.Sheet;

import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.Node;

/*******************************************************************************
 * Copyright (c) 2007 Jules White. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jules White - initial API and implementation
 ******************************************************************************/

/**
 * This interface is for providing a workbook extension for the 
 * Excel problem loader. Implementations of this interface are used
 * to process specific Excel worksheets and add constraints/data to
 * a DeploymentConfig. 
 */
public interface WorksheetHandler {

	/**
	 * Returns true if the worksheet is not required to
	 * be processed.
	 * @return
	 */
	public boolean isOptionalWorksheet();
	
	/**
	 * Returns the name of the worksheet
	 * that this handler operates on.
	 * @return
	 */
	public String getWorksheetName();
	
	/**
	 * This method reads a worksheet and populates a DeploymentConfig
	 * with data from the worksheet.
	 * 
	 * @param problem
	 * @param interacts
	 * @param comps
	 * @param nodes
	 */
	public void handleSheet(DeploymentConfig problem, Sheet interacts,
			Map<String, Component> comps, Map<String, Node> nodes);
}
