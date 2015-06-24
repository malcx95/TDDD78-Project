package se.liu.ida.malvi108.tddd78.project.time;

/**
 * Month class that stores the month number, the name of the month and the number of days.
 */
public final class Month
{
    private final static int DAYS_IN_A_WEEK = 7;
    private int monthNumber;
    private MonthName name;
    private Day[] days;
    /**
     * The number of days in long months, such as January.
     */
    public final static int LONG_MONTH_LENGTH = 31;
    /**
     * The number of days in short months, such as June (not February).
     */
    public final static int SHORT_MONTH_LENGTH = 30;
    /**
     * The number of days int non-leap-year February.
     */
    public final static int FEBRUARY_LENGTH = 28;
    /**
     * The number of days in February on a leap year.
     */
    public final static int LEAP_YEAR_FEBRUARY_LENGTH = 29;

    private final static int NUMBER_OF_MONTHS_IN_YEAR = 12;

    /**
     * January's month number.
     */
    public final static int JANUARY_NUMBER = 1;
    /**
     * February's month number.
     */
    public final static int FEBRUARY_NUMBER = 2;
    /**
     * March's month number.
     */
    public final static int MARCH_NUMBER = 3;
    /**
     * April's month number.
     */
    public final static int APRIL_NUMBER = 4;
    /**
     * May's month number.
     */
    public final static int MAY_NUMBER = 5;
    /**
     * June's month number.
     */
    public final static int JUNE_NUMBER = 6;
    /**
     * July's month number.
     */
    public final static int JULY_NUMBER = 7;
    /**
     * August's month number.
     */
    public final static int AUGUST_NUMBER = 8;
    /**
     * September's month number.
     */
    public final static int SEPTEMBER_NUMBER = 9;
    /**
     * October's month number.
     */
    public final static int OCTOBER_NUMBER = 10;
    /**
     * November's month number.
     */
    public final static int NOVEMBER_NUMBER = 11;
    /**
     * December's month number.
     */
    public final static int DECEMBER_NUMBER = 12;

    /**
     * Gets a month object. This constructor is only used by the Year-constructor
     * to create accurate years.
     * @param monthNumber The number of the month
     * @param leapYear Whether the month is in a leap year
     * @param firstDay The weekday of the first day.
     * @throws IllegalArgumentException
     */
    public Month(final int monthNumber, final boolean leapYear, final WeekDay firstDay) throws IllegalArgumentException{
	if (monthNumber < 1 || monthNumber > NUMBER_OF_MONTHS_IN_YEAR) {
	    throw new IllegalArgumentException("Monthnumber " + monthNumber + " is not within 1-12");
	}
	MonthName name = MonthName.values()[monthNumber-1];
	int numberOfDays = getMonthLength(monthNumber, leapYear);
	days = new Day[numberOfDays];
	days[0] = new Day(1, firstDay);

	for (int i = 1; i < numberOfDays; i++) { //loop starts at 1 since the first day is already added
	    days[i] = new Day(i + 1, days[i-1].getTomorrowsWeekday());
	}
	this.monthNumber = monthNumber;
	this.name = name;
    }

    public static int getMonthLength(int monthNumber, boolean leapYear){
	switch (monthNumber){
	    case JANUARY_NUMBER:
		return LONG_MONTH_LENGTH;
	    case FEBRUARY_NUMBER:
		if (leapYear){
		    return LEAP_YEAR_FEBRUARY_LENGTH;
		}
		else {
		    return FEBRUARY_LENGTH;
		}
	    case MARCH_NUMBER:
		return LONG_MONTH_LENGTH;
	    case APRIL_NUMBER:
		return SHORT_MONTH_LENGTH;
	    case MAY_NUMBER:
		return LONG_MONTH_LENGTH;
	    case JUNE_NUMBER:
		return SHORT_MONTH_LENGTH;
	    case JULY_NUMBER:
		return LONG_MONTH_LENGTH;
	    case AUGUST_NUMBER:
		return LONG_MONTH_LENGTH;
	    case SEPTEMBER_NUMBER:
		return SHORT_MONTH_LENGTH;
	    case OCTOBER_NUMBER:
		return LONG_MONTH_LENGTH;
	    case NOVEMBER_NUMBER:
		return SHORT_MONTH_LENGTH;
	    case DECEMBER_NUMBER:
		return LONG_MONTH_LENGTH;
	    default:
		throw new IllegalArgumentException("Invalid monthname"); // Hopefully this never happens
	}
    }

    public int getMonthNumber() {
	return monthNumber;
    }

    public int getNumberOfDays() {
	return days.length;
    }

    public MonthName getName() {
	return name;
    }

    public Day getLastDay(){
	return days[days.length - 1];
    }

    public Day getFirstDay(){
	return days[0];
    }

    public Day[] getDays() {
	return days;
    }

    @Override public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append(name).append("\n");
	for (int i = 0; i < days.length; i++) {
	    builder.append("W").append(days[i].getWeekNumber()).append(" ").append(days[i].getNumber()).append(".")
		    .append(days[i].getWeekDay()).append(" "); //apparently IDEA thinks this looks better than concatenating it
	    							//inside the append-calls.
	    if ((i + 1)% DAYS_IN_A_WEEK == 0) builder.append("\n");
	}
	return builder.toString();
    }

    public static Month getMonth(int monthNumber, int year){
	Year yr = new Year(year);
	return yr.getMonth(monthNumber);
    }

    public static Month getPreviousMonth(int monthNumber, int year){
	if (monthNumber == JANUARY_NUMBER){
	    return getMonth(DECEMBER_NUMBER, year - 1);
	}
	return getMonth(monthNumber - 1, year);
    }
}
