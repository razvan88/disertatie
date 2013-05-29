package analytics.analysis.algorithms.dbscan;

import java.util.HashMap;
import java.util.List;

import analytics.database.DBBasicOperations;
import analytics.utils.DbscanHelper;

public class Clustering {
	private HashMap<Integer, Integer> points;
	
	public Clustering() {
		points = new HashMap<Integer, Integer>();
	}
	
	public void initData() {
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();
		HashMap<Integer, List<String>> categories = db.getCategories();
		db.closeConnection();
		
		DbscanHelper dbscanHelper = DbscanHelper.getInstance();
		for(Integer key : categories.keySet()) {
			points.put(key, dbscanHelper.getValue(categories.get(key)));
		}
	}
}
