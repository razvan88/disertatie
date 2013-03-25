package analytics.charts;

import java.util.Date;
import java.util.List;

import analytics.database.DBBasicOperations;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DonutChart extends BaseChart{
	
	public DonutChart(Date startingDate, Date endingDate, List<String> products) {
		super(startingDate, endingDate, products);
	}
	
	@Override
	public JSONArray getChartData() {
		JSONArray json = new JSONArray();
		java.sql.Date initialDay = new java.sql.Date(this.startingDate.getTime());
		java.sql.Date finalDay = new java.sql.Date(this.endingDate.getTime());
		
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();
		
		for(String product : products) {
			int qty = db.getEntryTotalValuePerInterval(product, initialDay, finalDay);
			
			JSONObject entry = new JSONObject();
			entry.put("product", product);
			entry.put("qty", qty);
			
			json.add(entry);
		}
		
		db.closeConnection();
		
		return json;
	}
}
