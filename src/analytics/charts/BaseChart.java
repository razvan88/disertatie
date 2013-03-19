package analytics.charts;

import java.util.Date;
import java.util.List;

public abstract class BaseChart implements ChartInterface{
	protected Date startingDate;
	protected Date endingDate;
	protected List<String> products;
	
	public BaseChart(Date startingDate, Date endingDate, List<String> products) {
		this.startingDate = startingDate;
		this.endingDate = endingDate;
		this.products = products;
	}
}
