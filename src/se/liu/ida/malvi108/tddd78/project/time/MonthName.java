package se.liu.ida.malvi108.tddd78.project.time;

/**
 * Enum for the names of the different months of a year
 */
public enum MonthName
{
    /**
     * January
     */
    JANUARY,
    /**
     * February
     */
    FEBRUARY,
    /**
     * March
     */
    MARCH,
    /**
     * April
     */
    APRIL,
    /**
     * May
     */
    MAY,
    /**
     * June
     */
    JUNE,
    /**
     * July
     */
    JULY,
    /**
     * August
     */
    AUGUST,
    /**
     * September
     */
    SEPTEMBER,
    /**
     * October
     */
    OCTOBER,
    /**
     * November
     */
    NOVEMBER,
    /**
     * December
     */
    DECEMBER;

    @Override public String toString() {
	switch (this){
	    case JANUARY:
		return "Januari";
	    case FEBRUARY:
		return "Februari";
	    case MARCH:
		return "Mars";
	    case APRIL:
		return "April";
	    case MAY:
		return "Maj";
	    case JUNE:
		return "Juni";
	    case JULY:
		return "Juli";
	    case AUGUST:
		return "Augusti";
	    case SEPTEMBER:
		return "September";
	    case OCTOBER:
		return "Oktober";
	    case NOVEMBER:
		return "November";
	    case DECEMBER:
		return "December";
	    default:
		return null;
	}
    }

    /**
     * Converts a MonthName to a month number.
     */
    public static int getMonthNumber(MonthName name){
	MonthName[] names = MonthName.values();
	for (int i = 1; i <= names.length; i++) {
	    if (names[i - 1].equals(name)){
		return i;
	    }
	}
	return -1; //this can never happen.
    }
}
