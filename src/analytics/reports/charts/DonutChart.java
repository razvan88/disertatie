package analytics.reports.charts;

import java.util.Date;
import java.util.List;

public class DonutChart extends ColumnChart{
	
	public DonutChart(Date startingDate, Date endingDate, List<String> daysFilter, List<String> products) {
		super(startingDate, endingDate, daysFilter, products);
	}
}
