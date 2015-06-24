package se.liu.ida.malvi108.tddd78.project.time;

/**
 * Class for years, the constructor takes in a year number and creates all the months and days
 * of the desired year, the contents of a year object corresponds to reality.
 */
public class Year
{
    /**
     * The standard frequency of leap years (every four years).
     */
    private final static int LEAP_YEAR_FREQUENCY = 4;
    /**
     * The frequency by which the leap year is skipped (every 100 years).
     */
    private final static int LEAP_YEAR_SKIP_FREQUENCY = 100;
    /**
     * The frequency by which the "skipped leap year rule" is skipped,
     * that is how often it is a leap year when it really shouldn't
     * because of the "skip the leap year every 100 years"-rule
     * (every 400 years).
     */
    private static final int SKIP_RULE_SKIPPED_FREQUENCY = 400;
    private static final int WEEK_52 = 52;
    private static final int WEEK_53 = 53;
    private static final int WEEK_ONE = 1;
    /**
     * The day in december which decides whether the year has 52 or 53 weeks.
     */
    private static final int DECEMBER_28TH = 28;
    private final static int NUMBER_OF_MONTHS_IN_YEAR = 12;
    private Month[] months;
    private int yearNumber;
    /**
     * The year from which <code>Year</code> counts from to create year objects.
     * This means that no year
     */
    public final static int STARTING_YEAR = 2007;

    /**
     * Creates an accurate-to-reality year object.
     * @param yearNumber The number of the year you want to create.
     */
    public Year(final int yearNumber) {
	if (yearNumber < STARTING_YEAR) throw new IllegalArgumentException("Year " + yearNumber + " comes before 2007");
	this.yearNumber = yearNumber;
	boolean leapYear = isLeapYear(yearNumber);
	WeekDay firstDay = getFirstDay(yearNumber);
	months = new Month[NUMBER_OF_MONTHS_IN_YEAR];
	months[0] = new Month(Month.JANUARY_NUMBER, leapYear, firstDay);
	WeekDay nextStartingDay = months[0].getLastDay().getTomorrowsWeekday();
	for (int i = 1; i < NUMBER_OF_MONTHS_IN_YEAR; i++) { //loop starts at 1 since the first month is already added
	    months[i] = new Month(i + 1, leapYear, nextStartingDay);
	    nextStartingDay = months[i].getLastDay().getTomorrowsWeekday();
	}
	assignWeekNumbers(firstDay);
    }

    private WeekDay getFirstDay(int yearNumber){
	//2007 was in 2015 the last year that started with a Monday
	int numberOfDaysSince2007 = getNumberOfDaysSince2007(yearNumber);
	WeekDay[] weekDays = WeekDay.values();
	return weekDays[numberOfDaysSince2007 % 7];
    }

    /**
     * Gets the number of days since the first day in 2007 to the first day
     * in the given year number, for the purpose of calculating the first
     * weekday in a particular year.
     */
    private int getNumberOfDaysSince2007(int yearNumber){
	final int daysInAYear = 365;
	if (yearNumber == STARTING_YEAR) return 0;
	int result = 0;
	int numberOfLeapYears = getNumberOfLeapYearsSince2007(yearNumber);
	for (int year = STARTING_YEAR; year < yearNumber; year++) {
	    result += daysInAYear;
	}
	return result + numberOfLeapYears;
    }

    /**
     * Returns how many of the years passed since 2007 to the given
     * year number have been leap years (not including the year number).
     */
    private int getNumberOfLeapYearsSince2007(int yearNumber){
	//2008 is the first leap year since 2007.
	if (yearNumber == STARTING_YEAR) return 0;
	final int firstLeapYear = 2008;
	int result = 0;
	for (int year = firstLeapYear; year < yearNumber; year++) {
	    if (isLeapYear(year)) result++;
	}
	return result;
    }

    /**
     * Checks whether a given year is a leap year, is only used by the constructor
     * of a year object and it's help methods.
     */
    public static boolean isLeapYear(int yearNumber) {
	// If the year number is divisble by 4, it's a leap year, unless it's a whole century,
	// like 1900 or 2100. To add another level of complexity, it IS a leap year if the
	// year number is divisible by 400, like 1600 or 2000.
	if (isDivisible(yearNumber, LEAP_YEAR_FREQUENCY)){
	    if (isDivisible(yearNumber, LEAP_YEAR_SKIP_FREQUENCY)){
		return isDivisible(yearNumber, SKIP_RULE_SKIPPED_FREQUENCY);
	    }
	    return true;
	}
	return false;
    }


    /**
     * Checks whether two numbers are divisible, that is whether
     * the numerator is evenly divisible by the denominator.
     * @param numerator The numerator
     * @param denominator The denominator
     */
    private static boolean isDivisible(int numerator, int denominator){
	return numerator % denominator == 0;
    }

    public Month getMonth(int monthNumber){
	return months[monthNumber - 1];
    }

    /**
     * Assigns the weeknumbers to all of the days of the year. This works independently
     * from how many days the year is since (or before) 2007.
     */
    private void assignWeekNumbers(WeekDay firstDay){
	int weekNumber = getFirstWeekNumber(firstDay);
	for (Month month : months) {
	    for (Day day : month.getDays()) {
		if (day.getWeekDay() == WeekDay.MONDAY) {
		    // if the current day is a monday, the week number must be updated
		    weekNumber = updateWeekNumber(weekNumber, month, day);
		}
		day.setWeekNumber(weekNumber);
	    }
	}
    }

    /**
     * Updates the week number according to certain rules for weeks. Works under the assumption that
     * the current day is a monday.
     * @return A new week number.
     */
    private int updateWeekNumber(int currentNumber, final Month currentMonth, final Day currentDay) {
	if (currentMonth.getMonthNumber() == Month.DECEMBER_NUMBER) { //if it's december
	    if (currentNumber == WEEK_52) {
		// if the previous week was 52...
		if (currentDay.getNumber() > DECEMBER_28TH){
		    // ...and if the new week starts after 28/12,
		    // the next week is 1.
		    return WEEK_ONE;
		}
		else {
		    //otherwise, the new week is 53.
		    currentNumber++;
		}

	    } else if (currentNumber == WEEK_53) currentNumber = WEEK_ONE; // if the previous week was 53, the new is always 1.

	    else { //if it's the start of any other week in december, just increment the week number.
		currentNumber++;
	    }
	}
	else if (currentMonth.getMonthNumber() == Month.JANUARY_NUMBER){
	    //If it's the first day of January, or if the previous week number was 52 or 53 (in January), it should be week one.
	    if (currentDay.getNumber() == 1 || currentNumber == WEEK_52 || currentNumber == WEEK_53){
		return WEEK_ONE;
	    } else {
		currentNumber++;
	    }
	}
	//if it's neither January or December, just increment the week number.
	else currentNumber++;
	return currentNumber;
    }

    /**
     * Gets the first week number of the year, depending on a given day.
     * @param firstDay The weekday of January 1st.
     */
    private int getFirstWeekNumber(WeekDay firstDay){
	if (WeekDay.getWeekDayNumber(firstDay) <= WeekDay.getWeekDayNumber(WeekDay.THURSDAY)){
	    return WEEK_ONE;
	}
	else if (firstDay == WeekDay.FRIDAY){
	    return WEEK_53;
	}
	else if (firstDay == WeekDay.SATURDAY){
	    // this means the week number could be 52 OR 53, which means we have to check
	    // the week number of the last day of the previous year.
	    Year previous = new Year(yearNumber - 1);
	    return previous.getMonth(Month.DECEMBER_NUMBER).getLastDay().getWeekNumber();
	}
	else return WEEK_52;
    }

    @Override public String toString() {
	StringBuilder builder = new StringBuilder();
	for (Month month : months) {
	    builder.append(month).append("\n");
	}
	return builder.toString();
    }


    public Day getDay(int month, int day){
	return months[month - 1].getDays()[day - 1];
    }

}
