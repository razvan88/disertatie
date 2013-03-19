package analytics.charts;

import java.util.Date;
import java.util.List;

public class ChartFactory {
	
	public static ChartInterface getChartInstance(Date startingDate, Date endingDate, List<String> products, String chartType) {
		
		ChartInterface chart = null;
		
		switch(chartType) {
			case "smooth":
				chart = new SmoothChart(startingDate, endingDate, products);
				break;
			case "donut":
				chart = new DonutChart(startingDate, endingDate, products);
				break;
			case "bar":
				chart = new BarChart(startingDate, endingDate, products);
				break;
			case "column":
				chart = new ColumnChart(startingDate, endingDate, products);
				break;
		}
		
		return chart;
	}
}
