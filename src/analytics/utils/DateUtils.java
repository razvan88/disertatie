package analytics.utils;

import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
	public static long dayMillis = 86400000;
	private String startingDate, endingDate,
			startingDateTokens[], endingDateTokens[];
	private GregorianCalendar calendar;
	
	public DateUtils (String firstDate, String secondDate) {
		this.startingDate = firstDate;
		this.endingDate = secondDate;
		this.calendar = new GregorianCalendar();
	}
	
	public void sortDates() {
		this.startingDateTokens = this.startingDate.split("/");
		this.endingDateTokens = this.endingDate.split("/");
		
		boolean greaterStartingYear = Integer.parseInt(startingDateTokens[2]) > Integer.parseInt(endingDateTokens[2]);
		boolean greaterStartingMonth = greaterStartingYear && 
				(Integer.parseInt(startingDateTokens[0]) > Integer.parseInt(endingDateTokens[0]));
		boolean greaterStartingDay = greaterStartingYear && greaterStartingMonth && 
				(Integer.parseInt(startingDateTokens[1]) > Integer.parseInt(endingDateTokens[1]));
		if(greaterStartingYear || greaterStartingMonth || greaterStartingDay){
			String[] startingCopy = startingDateTokens.clone();
			for(int i = 0; i < startingDateTokens.length; i++) {
				startingDateTokens[i] = endingDateTokens[i];
				endingDateTokens[i] = startingCopy[i];
			}
		}
	}
	
	/**
	 * @return starting date tokens in an array, representing:
	 *  0 - year,
	 *  1 - month,
	 *  2 - day
	 */
	public String[] getStartingDateTokens() {
		return this.startingDateTokens;
	}
	
	/**
	 * @return ending date tokens in an array representing:
	 *  0 - year,
	 *  1 - month,
	 *  2 - day
	 */
	public String[] getEndingDateTokens() {
		return this.endingDateTokens;
	}
	
	public Date getStartingDate() {
		this.calendar.clear();
		this.calendar.set(Integer.parseInt(startingDateTokens[2]),
					 Integer.parseInt(startingDateTokens[0]) - 1,
					 Integer.parseInt(startingDateTokens[1]),
					 0, 0, 1);
		return this.calendar.getTime();
	}
	
	public Date getEndingDate() {
		this.calendar.clear();
		this.calendar.set(Integer.parseInt(endingDateTokens[2]),
					 Integer.parseInt(endingDateTokens[0]) - 1,
					 Integer.parseInt(endingDateTokens[1]),
					 23, 59, 59);
		return this.calendar.getTime();
	}
}
