package analytics.analysis.algorithms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import weka.associations.Apriori;
import weka.core.Instances;
import analytics.database.DBBasicOperations;

public class WekaAssociations {
	private Apriori algorithm;
	private Instances data;
	private double deltaValue;
	private double lowerBoundMinSupportValue;
	private double minMetricValue;
	private int numRulesValue;
	private double upperBoundMinSupportValue;
	private final String arffFileName = "input.ARFF";
	
	public WekaAssociations() {
		this.algorithm = new Apriori();
		this.data = this.getData();
		
		this.chooseAllVariables();
		this.setAllVariables();
	}
	
	private void chooseAllVariables() {
		//TODO - REFACTOR THIS
		this.deltaValue = 0.05;
		this.lowerBoundMinSupportValue = 0.1;
		this.minMetricValue = 0.5;
		this.numRulesValue = 20;
		this.upperBoundMinSupportValue = 1.0;
	}
	
	private void setAllVariables() {
		this.algorithm.setDelta(this.deltaValue);
		this.algorithm.setLowerBoundMinSupport(this.lowerBoundMinSupportValue);
		this.algorithm.setNumRules(this.numRulesValue);
		this.algorithm.setUpperBoundMinSupport(this.upperBoundMinSupportValue);
		this.algorithm.setMinMetric(this.minMetricValue);
	}
	
	public String runAlgorithm() {
		try {
			this.algorithm.buildAssociations(this.data);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return algorithm.toString();
	}
	
	private Instances getData() {
		FileReader reader;
		Instances result = null;
		this.createData();
		
		try {
			reader = new FileReader(new File(this.arffFileName));
			result = new Instances(reader);
			result.setClassIndex(result.numAttributes() - 1);
			reader.close();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	private void createData() {
		StringBuilder sb = new StringBuilder();
		String newLine = System.lineSeparator();
		sb.append("@RELATION products");
		sb.append(newLine + newLine);
		
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();
		HashMap<Integer, List<Integer>> transactions = db.getTransactions();
		HashMap<Integer, String> products = db.getProducts();
		db.closeConnection();
		
		for(String product : products.values()) {
			sb.append("@ATTRIBUTE '" + product + "' {0,1}" + newLine);
		}
		
		sb.append(newLine + "@DATA" + newLine);
		Object[] productsCodes = products.keySet().toArray();
		Arrays.sort(productsCodes);
		Set<Integer> codes = products.keySet();
		
		for(int transactionCode : transactions.keySet()) {
			String row = this.getARFFDataRow(transactions.get(transactionCode), codes) + newLine;
			sb.append(row);
		}
		
		String output = sb.append(newLine).toString();
		this.writeARFFFile(output);
	}
	
	private String getARFFDataRow(List<Integer> products, Set<Integer> codes) {
		StringBuilder row = new StringBuilder();
		Object[] sortedProducts = products.toArray();
		int currentProductIndex = 0,
				maxProductIndex = sortedProducts.length - 1;
		Arrays.sort(sortedProducts);
		
		for(int code : codes) {
			if(currentProductIndex > maxProductIndex) {
				row.append("0,");
				continue;
			}
			
			if(code != (Integer)sortedProducts[currentProductIndex]) {
				row.append("0,");
			} else {
				row.append("1,");
				currentProductIndex++;
			}
		}
		
		row.deleteCharAt(row.length() - 1);
		
		return row.toString();
	}
	
	private void writeARFFFile(String outputText) {
		try {
			File file = new File(this.arffFileName);
			if(file.exists()) {
				file.delete();
			}
			file.createNewFile();
			PrintWriter pw = new PrintWriter(new FileOutputStream(file));
			pw.write(outputText);
			pw.close();
		} catch(Exception e) {
			
		}
	}
	/*
	public static void main(String[] args) {
		WekaAssociations x = new WekaAssociations();
		x.runAlgorithm();
	}
	*/
}
