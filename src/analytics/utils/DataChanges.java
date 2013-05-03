package analytics.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;


public class DataChanges {
	
	public static void writeDataToFile(String fileName, int[][] data) {
		try {
			File file = new File(fileName);
			if(file.exists()) {
				file.delete();
			}
			file.createNewFile();
			PrintWriter pw = new PrintWriter(new FileOutputStream(file));
			pw.write(DataChanges.getStringFromMatrix(data));
			pw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getStringFromMatrix(int[][] data) {
		StringBuilder output = new StringBuilder();
		
		int rows = data.length;
		for(int i = 0; i < rows; i++) {
			for(int val : data[i]) {
				output.append(val + ",");
			}
			output.deleteCharAt(output.length() - 1);
			output.append(System.lineSeparator());
		}
		
		return output.toString();
	}
	
	public static boolean containsTokens(String target, String rawToken) {
		boolean foundAny = true;
		String[] tokens = rawToken.split(",");
		
		for(String token : tokens) {
			if(!target.contains(token)) {
				foundAny = false;
				break;
			}
		}
		
		return foundAny;
	}
	
	public static int[] getIntFromString(String[] columns) {
		int[] cols = new int[columns.length];
		for(int i = 0; i < columns.length; i++) {
			cols[i] = Integer.parseInt(columns[i]);
		}
		
		return cols;
	}
	
	public static Integer[] getIntFromString(String columns) {
		String[] cols = columns.split(",");
		Integer[] values = new Integer[cols.length];
		for(int i = 0; i < cols.length; i++) {
			values[i] = Integer.parseInt(cols[i].trim());
		}
		
		return values;
	}
}
