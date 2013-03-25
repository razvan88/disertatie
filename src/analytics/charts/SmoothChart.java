package analytics.charts;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import analytics.database.DBBasicOperations;


public class SmoothChart extends BaseChart{

	public SmoothChart(Date startingDate, Date endingDate, List<String> products) {
		super(startingDate, endingDate, products);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public JSONArray getChartData() {
		JSONArray json = new JSONArray();
		long dayMillis = 86400000;
		long startingMillis = this.startingDate.getTime();
		long endingMillis = this.endingDate.getTime();
		
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();
		
		do {
			java.sql.Date day = new java.sql.Date(startingMillis);
			List<Integer> values = new ArrayList<Integer>();
			for(String product : products)
				values.add(db.getEntryTotalValuePerDay(product, day));
			
			JSONObject entry = new JSONObject();
			entry.put("dd", day.getDate());
			entry.put("mm", day.getMonth());
			entry.put("yyyy", 1900 + day.getYear());
			for(int i = 0; i < values.size(); i++) {
				entry.put("value" + (i + 1), values.get(i));
			}
			
			json.add(entry);
			
			startingMillis += dayMillis;
		}while(startingMillis <= endingMillis);
		
		db.closeConnection();
		
		return json;
	}

}
