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
			}
		}
		
		return foundAny;
	}
}
