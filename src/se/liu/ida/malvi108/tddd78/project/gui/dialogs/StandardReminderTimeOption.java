package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

/**
 * The different options for the amount of time before a <code>StandardAppointment</code> is due to be reminded on.
 */
enum StandardReminderTimeOption implements ReminderTimeOption
{
    ON_TIME,
    FIVE_MINUTES,
    TEN_MINUTES,
    FIFTEEN_MINUTES,
    THIRTY_MINUTES,
    ONE_HOUR,
    TWO_HOURS,
    SIX_HOURS,
    ONE_DAY,
    TWO_DAYS,
    ONE_WEEK,
    TWO_WEEKS;

    @Override public String toString() {
	switch(this){
	    case ON_TIME:
		return "Vid aktivitetstiden";
	    case FIVE_MINUTES:
		return "Fem minuter före";
	    case TEN_MINUTES:
		return "Tio minuter före";
	    case FIFTEEN_MINUTES:
		return "Femton minuter före";
	    case THIRTY_MINUTES:
		return "En halvtimme före";
	    case ONE_HOUR:
		return "En timme före";
	    case TWO_HOURS:
		return "Två timmar före";
	    case SIX_HOURS:
		return "Sex timmar före";
	    case ONE_DAY:
		return "En dag före";
	    case TWO_DAYS:
		return "Två dagar före";
	    case ONE_WEEK:
		return "En vecka före";
	    case TWO_WEEKS:
		return "Två veckor före";
	    default:
		return "";
	}
    }
}
