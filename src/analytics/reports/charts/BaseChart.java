package analytics.reports.charts;

import java.util.Date;
import java.util.List;

public abstract class BaseChart implements ChartInterface{
	protected Date startingDate;
	protected Date endingDate;
	protected List<String> products;
	protected List<String> daysFilter;
	
	public BaseChart(Date startingDate, Date endingDate, List<String> daysFilter, List<String> products) {
		this.startingDate = startingDate;
		this.endingDate = endingDate;
		this.products = products;
		this.daysFilter = daysFilter;
	}
	
	protected boolean hasFilter() {
		return this.daysFilter.size() > 0;
	}
}
