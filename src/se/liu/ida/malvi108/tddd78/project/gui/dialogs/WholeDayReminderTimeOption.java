package se.liu.ida.malvi108.tddd78.project.gui.dialogs;
/**
 * The different options for the amount of time before a <code>WholeDayAppointment</code> is due to be reminded on.
 */
enum WholeDayReminderTimeOption implements ReminderTimeOption
{
    ON_TIME,
    ONE_DAY,
    TWO_DAYS,
    THREE_DAYS,
    ONE_WEEK,
    TWO_WEEKS,
    FOUR_WEEKS;

    @Override public String toString() {
	switch (this){
	    case ON_TIME:
		return "På aktivitetsdagen";
	    case ONE_DAY:
		return "En dag före";
	    case TWO_DAYS:
		return "Två dagar före";
	    case THREE_DAYS:
		return "Tre dagar före";
	    case ONE_WEEK:
		return "En vecka före";
	    case TWO_WEEKS:
		return "Två veckor före";
	    case FOUR_WEEKS:
		return "Fyra veckor före";
	    default:
		return "";
	}
    }
}
