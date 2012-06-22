package org.ascent.deployment.excel.handlers;

import java.util.Map;

import jxl.Sheet;

import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentConfig;
import org.ascent.deployment.Node;
import org.ascent.deployment.excel.WorksheetManipulator;

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
public abstract class AbstractWorksheetHandler extends WorksheetManipulator implements WorksheetHandler {

	public boolean isOptionalWorksheet() {
		return false;
	}

	public void handleSheet(DeploymentConfig problem, Sheet interacts,
			Map<String, Component> comps, Map<String, Node> nodes) {
	}

}
