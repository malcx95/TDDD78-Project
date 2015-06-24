package se.liu.ida.malvi108.tddd78.project.time;

/**
 * Enum for the names of the different days of the week.
 */
public enum WeekDay
{
    /**
     * Monday
     */
    MONDAY,
    /**
     * Tuesday
     */
    TUESDAY,
    /**
     * Wednesday
     */
    WEDNESDAY,
    /**
     * Thursday
     */
    THURSDAY,
    /**
     * Friday
     */
    FRIDAY,
    /**
     * Saturday
     */
    SATURDAY,
    /**
     * Sunday
     */
    SUNDAY;

    @Override public String toString() {
	switch (this){
	    case MONDAY:
		return "Måndag";
	    case TUESDAY:
		return "Tisdag";
	    case WEDNESDAY:
		return "Onsdag";
	    case THURSDAY:
		return "Torsdag";
	    case FRIDAY:
		return "Fredag";
	    case SATURDAY:
		return "Lördag";
	    case SUNDAY:
		return "Söndag";
	    default:
		return null;
	}
    }

    /**
     * Gets the order of the WeekDay in a week (Monday = 1, Tuesday = 2...)
     */
    public static int getWeekDayNumber(WeekDay weekDay){
	WeekDay[] weekDays = WeekDay.values();
	for (int i = 1; i <= weekDays.length; i++) {
	    if (weekDays[i - 1].equals(weekDay)){
		return i;
	    }
	}
	return -1; //this can never happen.
    }
}
