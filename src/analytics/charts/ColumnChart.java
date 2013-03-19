package analytics.charts;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

public class ColumnChart extends BaseChart{
	
	public ColumnChart(Date startingDate, Date endingDate, List<String> products) {
		super(startingDate, endingDate, products);
	}
	
	@Override
	public JSONObject getChartData() {
		// TODO - generate SmoothChart content
		return new JSONObject();
	}
}
