package analytics.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import analytics.database.DBBasicOperations;
import analytics.utils.DateUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ColumnChart extends BaseChart{
	
	public ColumnChart(Date startingDate, Date endingDate, List<String> daysFilter, List<String> products) {
		super(startingDate, endingDate, daysFilter, products);
	}
	
	@Override
	public JSONArray getChartData() {
		java.sql.Date initialDay = new java.sql.Date(this.startingDate.getTime());
		java.sql.Date finalDay = new java.sql.Date(this.endingDate.getTime());
		JSONArray json;
		
		DBBasicOperations db = DBBasicOperations.getInstance();
		db.openConnection();
		
		if(!this.hasFilter()) {
			json = this.getJson(db, initialDay, finalDay);
		} else {
			json = this.getJson(db);
		}
		
		db.closeConnection();
		
		return json;
	}
	
	private JSONArray getJson(DBBasicOperations db, java.sql.Date initialDay, java.sql.Date finalDay) {
		JSONArray json = new JSONArray();
		
		for(String product : this.products) {
			int qty = db.getEntryTotalValuePerInterval(product, initialDay, finalDay);
			
			JSONObject entry = new JSONObject();
			entry.put("product", product);
			entry.put("qty", qty);
			
			json.add(entry);
		}
		
		return json;
	}
	
	@SuppressWarnings("deprecation")
	private JSONArray getJson(DBBasicOperations db) {
		long startingMillis = this.startingDate.getTime();
		long endingMillis = this.endingDate.getTime();
		List<Integer> values = new ArrayList<Integer>(Collections.nCopies(this.products.size(), 0));
		JSONArray json = new JSONArray();
		
		do {
			java.sql.Date day = new java.sql.Date(startingMillis);
			Integer currentDay = day.getDay();
			
			if(this.daysFilter.contains(currentDay.toString())) {
				for(String product : products) {
					int index = products.indexOf(product);
					int oldAmount = values.get(index);
					int currentAmount = db.getEntryTotalValuePerDay(product, day);
					values.set(index, currentAmount + oldAmount);
				}
			}
			
			startingMillis += DateUtils.dayMillis;
		}while(startingMillis <= endingMillis);
		
		for(int i = 0; i < this.products.size(); i++) {
			JSONObject entry = new JSONObject();
			entry.put("product", this.products.get(i));
			entry.put("qty", values.get(i));
			json.add(entry);
		}
		
		return json;
	}
}
