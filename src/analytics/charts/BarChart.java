package analytics.charts;

import java.util.Date;
import java.util.List;

public class BarChart extends ColumnChart{
	
	public BarChart(Date startingDate, Date endingDate, List<String> daysFilter, List<String> products) {
		super(startingDate, endingDate, daysFilter, products);
	}
}
