package org.ascent.deployment.excel.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.ascent.deployment.Component;
import org.ascent.deployment.DeploymentPlan;
import org.ascent.deployment.Node;
import org.ascent.deployment.excel.handlers.DeploymentPlanHandler;

/*******************************************************************************
 * Copyright (c) 2007 Jules White. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Jules White - initial API and implementation
 ******************************************************************************/
public class ExcelDeploymentPlan {

	public static Cell get(Row row, int col, int type){
		Cell c = row.getCell(col);
		if(c == null)
			c = row.createCell(col);
		
		c.setCellType(Cell.CELL_TYPE_BLANK);
		c.setCellType(type);
		
		return c;
	}
	
	public static Row get(Sheet s, int row){
		Row r = s.getRow(row);
		if(r == null)
			r = s.createRow(row);
		return r;
	}
	
	public static Cell get(Sheet s, int row, int col, int type){
		return get(get(s,row),col, type);
	}
	
	public static void write(DeploymentPlan plan, File target) throws IOException {
		try {

			Workbook workbook = new HSSFWorkbook();

			Sheet sheet = workbook.createSheet(DeploymentPlanHandler.DEPLOYMENT_PLAN_SHEET);

			Component[] comps = plan.getDeploymentConfiguration()
			.getComponents();
			Node[] nodes = plan.getDeploymentConfiguration().getNodes();
			
			Map<Node,Integer> indices = new HashMap<Node, Integer>();
			for(int i = 0; i < nodes.length; i++){
				indices.put(nodes[i],i+1);
				
				Cell c = get(sheet,0,i+1,Cell.CELL_TYPE_STRING);
				c.setCellValue(nodes[i].getLabel());
			}
			
			for (int i = 0; i < comps.length; i++) {

				Component c = comps[i];
				Cell cell = get(sheet,i+1,0,Cell.CELL_TYPE_STRING);
				cell.setCellValue(c.getLabel());
				
				Cell dc = get(sheet,i+1,indices.get(plan.getHost(c)),Cell.CELL_TYPE_NUMERIC);
				dc.setCellValue(1);
			}

			FileOutputStream fout = new FileOutputStream(target);
			
			workbook.write(fout); 
			fout.flush();
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public static DeploymentPlan read(File src, DeploymentConfig conf) throws Exception {
//		return read(src,DeploymentPlanHandler.DEPLOYMENT_PLAN_SHEET,conf);
//	}
	
//	public static DeploymentPlan read(File src, String worksheet, DeploymentConfig conf) throws Exception {
//		Workbook workbook = Workbook.getWorkbook(src);
//		DeploymentPlan dplan = null;
//
//		try {
//			Sheet sheet = workbook.getSheet(worksheet);
//			
//			dplan = new DeploymentPlan(conf,new VectorSolution(new int[conf.getComponents().length]));
//			
//			Map<String, Component> comps = new HashMap<String, Component>();
//			Map<String, Node> nodes = new HashMap<String, Node>();
//			for(Component c : conf.getComponents()){
//				comps.put(c.getLabel(), c);
//			}
//			for(Node n : conf.getNodes()){
//				nodes.put(n.getLabel(), n);
//			}
//			
//			(new DeploymentPlanHandler()).handleSheetImpl(dplan, sheet, comps, nodes);
//
//		} finally {
//			workbook.close();
//		}
//		
//		return dplan;
//	}
}
