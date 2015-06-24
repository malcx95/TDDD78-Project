package se.liu.ida.malvi108.tddd78.project.time;

/**
 * Class for a day of a month, containing the monthnumber and the name of the day.
 */
public final class Day
{
    private int number;
    private WeekDay weekDay;
    private int weekNumber;

    public Day(int number, WeekDay weekDay){
	this.number = number;
	this.weekDay = weekDay;
    }

    public int getNumber() {
	return number;
    }

    public WeekDay getWeekDay() {
	return weekDay;
    }

    public WeekDay getTomorrowsWeekday(){
	if (this.weekDay == WeekDay.SUNDAY){
	    return WeekDay.MONDAY;
	}
	else {
	    int thisIndex = this.weekDay.ordinal();
	    return WeekDay.values()[thisIndex + 1];
	}
    }

    public void setWeekNumber(final int weekNumber) {
	this.weekNumber = weekNumber;
    }

    public int getWeekNumber() {
	return weekNumber;
    }
}
