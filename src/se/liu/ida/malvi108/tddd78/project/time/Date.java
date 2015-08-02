package se.liu.ida.malvi108.tddd78.project.time;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Class for dates. Contains a day, month, year.
 */
public class Date implements Serializable
{
    private int day;
    private int month;
    private int year;
    private final static int DECEMBER_MONTH = 12;
    /**
     * The earliest date the calendar can display.
     */
    public final static Date EARLIEST_ALLOWED_DATE = new Date(1, 1, Year.STARTING_YEAR);

    public Date(final int day, final int month, final int year) {
	this.day = day;
	this.month = month;
	this.year = year;
    }

    /**
     * Gets a date representing the next day in relation to the given date.
     */
    public Date getNextDay(){
	Date result = new Date(day, month, year);
	int monthLength = Month.getMonthLength(month, Year.isLeapYear(year));
	if (monthLength == Month.LONG_MONTH_LENGTH && month == DECEMBER_MONTH){ //If it's the last day of december
	    result.day = 1;
	    result.month = 1;
	    result.year++;
	}
	else if (isLastDayOfMonth()){
	    result.day = 1;
	    result.month++;
	}
	else {
	    result.day++;
	}
	return result;
    }

    /**
     * Gets a date representing the previous day in relation to the given date.
     */
    public Date getPreviousDay(){
	Date result = new Date(day, month, year);
	if (month == 1 && day == 1){ //if it's the first of January
	    result.year--;
	    result.month = DECEMBER_MONTH;
	    result.day = Month.LONG_MONTH_LENGTH;
	}
	else if (day == 1){
	    result.day = Month.getMonthLength(month - 1, Year.isLeapYear(year));
	    result.month--;
	}
	else {
	    result.day--;
	}
	return result;
    }

    /**
     * Gets a date representing n days before this date.
     */
    public Date getDaysBefore(int n) {
	//TODO effektivisera detta
	Date result = this;
	while (true) {
	    if (n == 0) {
		return result;
	    } else {
		n -= 1;
		result = result.getPreviousDay();
	    }
	}
    }

    private boolean isLastDayOfMonth(){
	return day == Month.getMonthLength(month, Year.isLeapYear(year));
    }

    public int getDay() {
	return day;
    }

    public int getMonth() {
	return month;
    }

    /**
     * Gets the monthname of this date.
     */
    public MonthName getMonthName(){
	return MonthName.values()[month - 1];
    }

    public int getYear() {
	return year;
    }

    public static Date getToday(){
	int day = LocalDate.now().getDayOfMonth();
	int month = LocalDate.now().getMonthValue();
	int year = LocalDate.now().getYear();
	return new Date(day, month, year);
    }

    /**
     * Gets the weekday of the day that corresponds to the date.
     */
    public WeekDay getWeekday(){
	Year year = new Year(this.year);
	return year.getDay(month, day).getWeekDay();
    }

    /**
     * Gets the week number of the day that corresponds to the date.
     */
    public int getWeekNumber(){
	Year year = new Year(this.year);
	return year.getDay(month, day).getWeekNumber();
    }

    @Override public boolean equals(final Object obj) {
	if (!(obj instanceof Date)) return false;
	Date other = (Date) obj;
	return this.day == other.day &&
	       this.month == other.month &&
	       this.year == other.year;
    }

    /**
     * Tells whether a date precedes another.
     */
    public boolean precedes(Date other){
	//if (this.equals(other)) return true;
	if (year < other.year) return true;
	if (month < other.month) return true;
	if (day < other.day) return true;
	return false;
    }

    @Override public String toString() {
	return day + " " + MonthName.values()[month - 1] + " " + year;
    }
}