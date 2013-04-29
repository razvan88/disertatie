package analytics.analysis.algorithms;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import analytics.database.DBBasicOperations;
import analytics.utils.AprioriHelper;
import analytics.utils.DataChanges;


public class Associations {
	private double deltaValue;
	private double lowerBoundMinSupportValue;
	private double minMetricValue;
	private int numRulesValue;
	private double upperBoundMinSupportValue;
	private int data[][], rows = 0;
	private final String dataFileName = "input.in";
	
	public Associations() {
		this.initiateAlgVariables();
	}
	
	private void initiateAlgVariables() {
		//TODO - REFACTOR THIS PART
		this.deltaValue = 0.05;
		this.lowerBoundMinSupportValue = 0.1;
		this.minMetricValue = 0.5;
		this.numRulesValue = 20;
		this.upperBoundMinSupportValue = 1.0;
	}
	
	public void buildData() {
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();
		HashMap<Integer, List<Integer>> transactions = db.getTransactions();
		HashMap<Integer, String> products = db.getProducts();
		db.closeConnection();
				
		Object[] productsCodes = products.keySet().toArray();
		Arrays.sort(productsCodes);
		Set<Integer> codes = products.keySet();
		this.data = new int[transactions.size()][];
		
		for(int transactionCode : transactions.keySet()) {
			int[] row = AprioriHelper.getDataRow(transactions.get(transactionCode), codes);
			this.data[this.rows++] = row;
		}
		
		DataChanges.writeDataToFile(this.dataFileName, this.data);
	}
	
	public static void main(String[] args) {
		List<String> step1 = AprioriHelper.getAllCombinations(4);
		List<String> step2 = AprioriHelper.removeColumnsCombination(step1, "0,1,2");
		
		System.out.println(step1);
		System.out.println(step2);
	}
}
