package org.ascent.excel;

public class naomi_excel_xls {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String in_file = args[0];
		String file_prefix = in_file.substring(0,in_file.lastIndexOf("\\"));
		String out_file = file_prefix + "\\" + args[1];
		String utils_loc = args[2];
		String option = args[3];
		
		ExConnector ex = new ExConnector();
		ex.readInput(in_file);
		ex.readAttributes(utils_loc);
		ex.execute();
		// Three possibilities: (in_file,"same"),(in_file,"sheet"),(out_file,"workbook").
		if (option.equals("workbook")) {
			ex.writeOutput(out_file, option);
		}
		else {
			ex.writeOutput(in_file, option);
		}
		
	}

}
