package org.ascent.excel;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import jxl.Cell;
import jxl.CellFeatures;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.lmco.atl.naomi.attributes.Resource;
import com.lmco.atl.naomi.api.NaomiAttribute;
import com.lmco.atl.naomi.api.NaomiFileUtils;

public class ExConnector {
	
	private int totalCount_ = 0;
	
	private Hashtable<String, Object[]> cells_ = new Hashtable<String, Object[]>();
	
	private String inputFile_ = "";
	
	public void readInput(String loc) {
		
		inputFile_ = loc;
		File f = new File(loc);
		
		totalCount_ = 0;
		cells_ = new Hashtable<String, Object[]>();

		try {
			Workbook workbook = Workbook.getWorkbook(f);
			
			WorksheetManipulator manip = new WorksheetManipulator();
			int numSheets = workbook.getNumberOfSheets();
			for (int k = 0; k < numSheets; k++) {
				Sheet s = workbook.getSheet(k);
				int numCols = manip.getColumnCount(s);
				int numRows = manip.getRowCount(s);
			
				//Cell c = workbook.findCellByName("MyCell");
				//System.out.println("MyCell.value = " + c.getContents());

				for(int i = 0; i < numRows; i++) {
					for(int j = 0; j < numCols; j++) {
						Cell c = s.getCell(j,i);
						CellFeatures cf = c.getCellFeatures();
						if (cf != null) {
							String comment = cf.getComment();
							System.out.println("The comment is: " + comment);
							String name = "";
							if (comment.contains("\n")) {
								// This is necessary because the first line of the comment
								// is the author, not the name.
								name = comment.substring(comment.indexOf("\n")+1, 
										comment.indexOf("\n", comment.indexOf("\n")+1));
							}
							else {
								name = comment;
							}
							String contents = s.getCell(j,i).getContents();
							if (c.getType() == CellType.NUMBER) {
								int num = Integer.parseInt(contents);
								Object[] obj = new Object[1];
								obj[0] = num;
								cells_.put(name, obj);
								System.out.println("Added cell with name " + name + ".");
								System.out.println("and contents " + obj[0]);
								totalCount_++;
							}
							else {
								Object[] obj = new Object[1];
								obj[0] = contents;
								cells_.put(name, obj);
								System.out.println("Added cell with name " + name + ".");
								System.out.println("and contents " + obj[0]);
								totalCount_++;
							}
						}
					}
				}
			}
			
			workbook.close();
		}
		catch (Exception e) {
			System.out.print("Unhandled exception when opening excel file ");
			System.out.println(loc + " with exception message: ");
			System.out.println(e.getMessage());
			e.printStackTrace();
			return;
		}
	}
	
	// This function finds the current values of the resources for the attributes
	// requested by the xls sheet by comments that start with naomi_get.attrName[].
	// It puts the values of the resources in the hashtable, but does not change
	// them in the spreadsheet.
	// It also sets the current values of the resources for the attributes from
	// the xls sheet with comments that start with naomi_put.attrName[].  It changes
	// the attribute files, but not the hashtable or spreadsheet in this case
	public void readAttributes(String utils_loc) {
		
		try {
			// Create the NaomiFileUtils object
			NaomiFileUtils utils = new NaomiFileUtils(new File(utils_loc));
			List<File> attributes = utils.getAttributeFiles();
			
			// Get the names from the hash table we need to work with
			Enumeration<String> keys = cells_.keys();
			while (keys.hasMoreElements())
			{
				String current_name = keys.nextElement();
				if (current_name.startsWith("naomi_")) {
					// Check if this is a csv list
					// First change point to change [C,2] to [2]
					String attr_num = current_name.substring(
							current_name.indexOf(',')+1, current_name.indexOf(',')+2);
					int num = Integer.parseInt(attr_num);
					if (num == 1) {	//not a csv list
						String attr_name = current_name.substring(
								current_name.indexOf('.')+1, current_name.indexOf('['));
						System.out.print("Using the attribute " + attr_name);
						System.out.println(" from utils.");
						boolean found = false;
						for (int i = 0; i < attributes.size(); i++) {
							// Get the actual attribute we want to read in
							File f = attributes.get(i);
							if (f.getName().equals(attr_name)) {
								found = true;
								NaomiAttribute actual_attr = new NaomiAttribute(f);
								modifyThisAttribute(actual_attr, current_name, 
										cells_.get(current_name)[0].toString(), false);
							}
						}
						if (!found) {
							makeNewAttribute(current_name, attr_name, utils_loc, 
									cells_.get(current_name)[0].toString());
						}
					}
					else if (num > 1) {	//it is a csv list
						Object[] objs = cells_.get(current_name);
						String csv_list = objs[0].toString();
						System.out.println("csv_list = " + csv_list);
						String [] splitInput = csv_list.split(",");
						String new_list = "";
						for(String i : splitInput){
							String [] splitInput2 = i.split(":");
							String attr_name = splitInput2[0];
							Boolean found = false;
							for (int j = 0; j < attributes.size(); j++) {
								// Get the actual attribute we want to read in
								File f = attributes.get(j);
								if (f.getName().equals(attr_name)) {
									found = true;
									NaomiAttribute actual_attr = new NaomiAttribute(f);
									if (current_name.startsWith("naomi_get.")) {
										if (new_list == "") {
											new_list = attr_name + ":" + 
													modifyThisAttribute(actual_attr, 
															current_name, 
													splitInput2[1], true);
											System.out.println("new_list="+new_list);
										}
										else {
											new_list = new_list + "," + attr_name + ":"
													+ modifyThisAttribute(actual_attr, 
															current_name, 
															splitInput2[1], true);;
											System.out.println("new_list="+new_list);
										}
									}
									else {
										modifyThisAttribute(actual_attr, current_name, 
												splitInput2[1], true);
									}
								}
							}
							if (!found) {
								makeNewAttribute(current_name, attr_name, utils_loc, 
										splitInput2[1]);
							}
						}
						if (new_list != "") {
							Object[] newObjs = new Object[2];
							newObjs[0] = new_list;
							System.out.println("Putting new_list="+new_list);
							cells_.put(current_name, newObjs);
						}
					}
				}
			}
		}
		catch (Exception e) {
			System.out.print("Unhandled exception getting the attributes from ");
			System.out.println("the utils object.");
			System.out.println(e.getMessage());
			e.printStackTrace();
			return;
		}
	}
	
	// After lunch: test all the new stuff to make sure didn't lose any functionality
	//				test the really new stuff to make sure can get and put values
	// 				write the users guide for Jules
	
	private String modifyThisAttribute(NaomiAttribute a, String name, String contents, 
				boolean csvFlag) {
		if (name.startsWith("naomi_get.")) {
			String returnList = "";
			if (a.getValue() != null) {
				Object[] objs = new Object[1];
				objs[0] = a.getValue();
				if (!csvFlag) {
					cells_.put(name, objs);
				}
				else {
					returnList = objs[0].toString();
				}
				System.out.print("Modified cell with name ");
				System.out.print(name+" and contents ");
				System.out.println(objs[0]);
			}
			else {
				Resource[] arr = a.getResource();
				Object[] objs = new Object[1];
				objs[0] = arr[0].getUri();
				if (!csvFlag) {
					cells_.put(name, objs);
				}
				else {
					returnList = objs[0].toString();
				}
				System.out.print("Modified cell with name ");
				System.out.print(name+" and contents ");
				System.out.println(objs[0]);
			}
			if (csvFlag) {
				return returnList;
			}
		}
		else if (name.startsWith("naomi_put.")) {
			if (a.getValue() != null) {
				//Object[] objs = cells_.get(name);
				//String value = objs[0].toString();
				//a.setValue(value);
				a.setValue(contents);
				try {
					a.save(); 
				}
				catch (Exception e) {
					System.out.print("Error saving an attribute that has been ");
					System.out.println("modified.  Aborting.");
					return null;
				}
				System.out.print("Modified attribute with name ");
				System.out.print(name+" and contents ");
				System.out.println(contents);
			}
			else {
				Resource[] arr = a.getResource();
				//Object[] objs = cells_.get(name);
				Resource r = new Resource();
				r.setName(arr[0].getName());
				r.setChecksum(arr[0].getChecksum());
				//r.setUri(objs[0].toString());
				r.setUri(contents);
				// Switch this line for the next two to make it
				// append the modified resource instead of changing
				// the original.
				//actual_attr.addResource(r);
				arr[0] = r;
				a.setResource(arr);
				try {
					a.save(); 
				}
				catch (Exception e) {
					System.out.print("Error saving an attribute that has been ");
					System.out.println("modified.  Aborting.");
					return null;
				}
				System.out.print("Modified attribute with name ");
				System.out.print(name+" and contents ");
				System.out.println(contents);
			}
		}
		return null;
	}
	
	// This doesn't work for csv stuff - it needs to create a new attribute
	// with a name specified by the caller, not with the current_name in the hash.
	// This is done.
	private String makeNewAttribute(String name, String attr_name, String utils_loc,
			String contents) {
		// We need to create a new attribute to hold this value.
		if (name.startsWith("naomi_put.")) {
			//Object[] objs = cells_.get(name);
			NaomiAttribute actual_attr = new NaomiAttribute();
			//String contents = objs[0].toString();
			if (!contents.contains(":/")) {
				// This needs to be non-null so modifyThisAttribute knows what to do
				actual_attr.setValue(contents);
			}
			else {
				// This stuff needs to be there so modifyThisAttribute can use it
				Resource r = new Resource();
				r.setName(attr_name + " Resource 1");
				r.setChecksum("123456789");
				Resource[] arr = new Resource[1];
				arr[0] = r;
				actual_attr.setResource(arr);
			}
			try {
				actual_attr.setFile(new File(utils_loc + 
						"//attributes//" + attr_name));
				actual_attr.save();
			}
			catch (Exception e) {
				System.out.print("Error saving an attribute that has been ");
				System.out.println("created.  Aborting.");
				return null;
			}
			System.out.print("Modified attribute with name "+name);
			System.out.println(" and contents " + contents);
			return modifyThisAttribute(actual_attr, name, contents, false);
		}
		return null;
	}
	
	public void execute() {
		
		/**********************************************************************
		 * Add code here to modify the Hashtable cells_ in any way desired before
		 * it is written to the output excel file
		 **********************************************************************/
		
	}
	
	// @param loc = the name of the output file to be created if opt = "workbook"
	// 			or loc = the name of the input file to be modified if opt = "sheet"
	//			or if opt = "same".  These are the only possible value of opt.
	public void writeOutput(String loc, String opt) {
		// Create the new excel file
		if (opt.equals("workbook")) {
			try {
				if (loc.equals(inputFile_)) {
					System.out.print("You are trying to overwrite the input ");
					System.out.println("workbook, which is not allowed.");
					System.out.println("Your output workbook will be placed in: ");
					System.out.println(loc + ".modified.xls");
					loc = loc + ".modified.xls";
				}
				WritableWorkbook out_workbook = Workbook.createWorkbook(new File(loc));
				WritableSheet out_sheet = out_workbook.createSheet("First Sheet", 0);
			
				Enumeration<String> keys = cells_.keys();
				int i = 0;
				while (keys.hasMoreElements())
				{
					String current_name = keys.nextElement();
					Object[] val = cells_.get(current_name);
					if (current_name.indexOf(",",current_name.indexOf(",")+1) == -1) {
						if (val[0] instanceof String) {
							String contents = (val[0]).toString();
							System.out.println("About to write the value " + contents);
							Label label = new Label(0, i, contents);
							i++;
							out_sheet.addCell(label);
						}
						else if (val[0] instanceof Integer) {
							int contents = Integer.parseInt((val[0]).toString());
							System.out.println("About to write the value " + contents);
							Number n = new Number(0, i, contents);
							i++;
							out_sheet.addCell(n);
						}
					}
					else {
						int num = Integer.parseInt(current_name.substring(
								current_name.indexOf(",",current_name.indexOf(","))+1,
								current_name.indexOf(",",current_name.indexOf(","))+2));
						String csv_list = val[0].toString();
						System.out.println("csv_list to output = " + csv_list);
						String [] splitInput = csv_list.split(",");
						for (int j = 0; j < num - 1; j++) {
							String contents = splitInput[j];
							System.out.println("About to write the value " + contents);
							Label label = new Label(0, i, contents);
							i++;
							out_sheet.addCell(label);
						}
						String contents = "";
						for (int j = num - 1; j < splitInput.length; j++) {
							contents += splitInput[j];
						}
						System.out.println("About to write the value " + contents);
						Label label = new Label(0, i, contents);
						i++;
						out_sheet.addCell(label);
					}
				}
			
				out_workbook.write();
				out_workbook.close();
			}
			catch (Exception e) {
				System.out.print("Unhandled exception creating new excel file ");
				System.out.println("output.xls with exception message: ");
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			}
		}
		else if (opt.equals("sheet")) {
			try {
				Workbook workbook = Workbook.getWorkbook(new File(loc));
				WritableWorkbook copy = Workbook.createWorkbook(
						new File(loc+".modified.xls"), workbook);	
				WritableSheet s = copy.createSheet("New Sheet",copy.getNumberOfSheets()+1);
				
				Enumeration<String> keys = cells_.keys();
				int i = 0;
				while (keys.hasMoreElements())
				{
					String current_name = keys.nextElement();
					Object[] val = cells_.get(current_name);
					if (current_name.indexOf(",",current_name.indexOf(",")+1) == -1) {
						if (val[0] instanceof String) {
							String contents = (val[0]).toString();
							System.out.println("About to write the value " + contents);
							Label label = new Label(0, i, contents);
							i++;
							s.addCell(label);
						}
						else if (val[0] instanceof Integer) {
							int contents = Integer.parseInt((val[0]).toString());
							System.out.println("About to write the value " + contents);
							Number n = new Number(0, i, contents);
							i++;
							s.addCell(n);
						}
					}
					else {
						int num = Integer.parseInt(current_name.substring(
								current_name.indexOf(",",current_name.indexOf(","))+1,
								current_name.indexOf(",",current_name.indexOf(","))+2));
						String csv_list = val[0].toString();
						System.out.println("csv_list to output = " + csv_list);
						String [] splitInput = csv_list.split(",");
						for (int j = 0; j < num - 1; j++) {
							String contents = splitInput[j];
							System.out.println("About to write the value " + contents);
							Label label = new Label(0, i, contents);
							i++;
							s.addCell(label);
						}
						String contents = "";
						for (int j = num - 1; j < splitInput.length; j++) {
							contents += splitInput[j];
						}
						System.out.println("About to write the value " + contents);
						Label label = new Label(0, i, contents);
						i++;
						s.addCell(label);
					}
				}
			
				copy.write();
				copy.close();
			}
			catch (Exception e) {
				System.out.print("Unhandled exception creating new excel file ");
				System.out.println("output.xls with exception message: ");
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			}
			
		}
		else if (opt.equals("same")) {
			try {
				Workbook workbook = Workbook.getWorkbook(new File(loc));
				WritableWorkbook copy = Workbook.createWorkbook(
						new File(loc+".modified.xls"), workbook);
				int numSheets = copy.getNumberOfSheets();
				for (int m = 0; m < numSheets; m++) {
					WritableSheet s = copy.getSheet(m);
				
					WorksheetManipulator manip = new WorksheetManipulator();
					int numCols = manip.getColumnCount(s);
					int numRows = manip.getRowCount(s);
				
					for(int i = 0; i < numRows; i++) {
						for(int j = 0; j < numCols; j++) {
							WritableCell c = s.getWritableCell(j,i);
							CellFeatures cf = c.getCellFeatures();
							if (cf != null) {
								String comment = cf.getComment();
								String name = "";
								if (comment.contains("\n")) {
									// This is necessary because the first line of the comment
									// is the author, not the name.
									name = comment.substring(comment.indexOf("\n")+1, 
											comment.indexOf("\n", comment.indexOf("\n")+1));
								}
								else {
									name = comment;
								}
								Enumeration<String> keys = cells_.keys();
								while (keys.hasMoreElements())
								{
									String current_name = keys.nextElement();
									if (current_name.equals(name)) {
										Object[] val = cells_.get(current_name);
										if (current_name.indexOf(",",current_name.indexOf(",")+1) == -1) {
											System.out.println("Output: not a csv list at "+j+", "+i);
											if (val[0] instanceof String) {
												if (c.getType() == CellType.LABEL) {
													Label l = (Label) c;
													String contents = (val[0]).toString();
													l.setString(contents);
												}
												else {
													String contents = (val[0]).toString();
													Label label = new Label(c.getColumn(), 
															c.getRow(), contents);
													System.out.println("**");
													label.setCellFeatures(new WritableCellFeatures());
													label.getCellFeatures().setComment(
															cf.getComment());
													System.out.println("***");
													s.addCell(label);
												}
											}
											else if (val[0] instanceof Integer) {
												if (c.getType() == CellType.NUMBER) {
													Number n = (Number) c;
													int contents = Integer.parseInt((val[0]).toString());
													n.setValue(contents);
												}
												else {
													int contents = Integer.parseInt(
															(val[0]).toString());
													Number n = new Number(c.getColumn(), 
															c.getRow(), contents);
													n.setCellFeatures(new WritableCellFeatures());
													n.getCellFeatures().setComment(
															cf.getComment());
													s.addCell(n);
												}
											}
										}
										else {
											int num = Integer.parseInt(current_name.substring(
													current_name.indexOf(",",current_name.indexOf(",")+1)+1,
													current_name.indexOf("]")));
											String csv_list = val[0].toString();
											System.out.println("csv_list to output = " + csv_list+" at "+j+", "+i);
											String [] splitInput = csv_list.split(",");
											int tempi = i;
											for (int k = 0; k < num - 1; k++) {
												String contents = splitInput[k];
												System.out.println("1About to write the value " + contents);
												Label label = new Label(j, tempi, contents);
												if (tempi == i) {
													label.setCellFeatures(new WritableCellFeatures());
													label.getCellFeatures().setComment(
															cf.getComment());
												}
												tempi++;
												s.addCell(label);
											}
											String contents = "";
											for (int k = num - 1; k < splitInput.length; k++) {
												contents += splitInput[k];
											}
											System.out.println("2About to write the value " + contents);
											Label label = new Label(j, tempi, contents);
											//label.setCellFeatures(new WritableCellFeatures());
											//label.getCellFeatures().setComment(
											//		cf.getComment());
											//i++;
											s.addCell(label);
										}
									}
								}
							}
						}
					}
				}
				
				copy.write();
				copy.close();
			}
			catch (Exception e) {
				System.out.print("Unhandled exception creating new excel file ");
				System.out.println("output.xls with exception message: ");
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			}
		}
	}

}
