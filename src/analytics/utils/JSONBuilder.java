package analytics.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONBuilder {
	
	private static JSONBuilder jsonBuilderInstance = null;
	
	static {
		jsonBuilderInstance = new JSONBuilder();
	}
	
	private JSONBuilder() {}
	
	public static JSONBuilder getInstance() {
		return jsonBuilderInstance;
	}
	
	public JSONArray getTestJson() {
		JSONArray json = new JSONArray();
		Random random = new Random();
		
		Calendar calendar = GregorianCalendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		for(int i = 0; i < 5; i++) {
			JSONObject jsonObj = new JSONObject();
			
			jsonObj.put("dd", day + i);
			jsonObj.put("mm", month);
			jsonObj.put("yyyy", year);
			jsonObj.put("value1", random.nextDouble());
			jsonObj.put("value2", random.nextDouble());
			
			json.add(jsonObj);
		}
		
		return json;
	}
}
