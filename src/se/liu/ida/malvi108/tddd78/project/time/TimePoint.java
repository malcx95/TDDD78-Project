package se.liu.ida.malvi108.tddd78.project.time;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * Class for a point in time of a day. Contains values for hours and minutes.
 */
public final class TimePoint implements Serializable
{
    private final static int MINUTES_IN_HOUR = 60;
    private final static int HOURS_IN_DAY = 24;
    private final static int MINUTES_IN_DAY = 1440;
    private final static int MAX_HOUR = 23;
    private final static int MAX_MINUTE = 59;
    private int hour;
    private int minute;

    public TimePoint(int hour, int minute){
	if (hour > MAX_HOUR || hour < 0){
	    throw new IllegalArgumentException("Invalid hour: " + Integer.toString(hour));
	} else if (minute < 0 || minute > MAX_MINUTE){
	    throw new IllegalArgumentException("Invalid minute: " + Integer.toString(minute));
	}
	this.hour = hour;
	this.minute = minute;
    }

    public int getHour() {
	return hour;
    }

    public int getMinute() {
	return minute;
    }

    /**
     * Determines whether this TimePoint precedes another.
     */
    public boolean precedes(TimePoint other){
	int thisHour = this.hour;
	int otherHour = other.hour;
	if (thisHour < otherHour) return true;
	else if (thisHour > otherHour) return false;
	// Now we know it's the same hour.

	int thisMinute = this.minute;
	int otherMinute = other.minute;
	if (thisMinute < otherMinute) return true;
	return false;
    }

    /**
     * Gets the distance in minutes of this <code>TimePoint</code> to a given other <code>TimePoint</code>.
     * Negative if the other precedes this.
     */
    public int getDistanceTo(TimePoint other){
	return (other.hour * MINUTES_IN_HOUR + other.minute) - (this.hour * MINUTES_IN_HOUR + this.minute);
    }

    public boolean precedesOrEquals(TimePoint other){
	return precedes(other) || equals(other);
    }

    @Override public String toString() {
	String minute = Integer.toString(this.minute);
	String hour = Integer.toString(this.hour);
	if (this.minute < 10){
	    minute = "0" + this.minute;
	} else if (this.hour < 10) {
	    hour = "0" + this.hour;
	}
	return hour + ":" + minute;
    }

    @Override public boolean equals(final Object obj) {
	if (!(obj instanceof TimePoint)) return false;
	TimePoint other = (TimePoint) obj;
	return this.minute == other.minute &&
	       this.hour == other.hour;
    }

    /**
     * Converts minutes from 12PM to a TimePoint object.
     * @param minutes Number of minutes since 12PM.
     */
    public static TimePoint minutesToTimePoint(int minutes){
	int minute = minutes % MINUTES_IN_HOUR;
	minutes -= minute;
	int hour = minutes/MINUTES_IN_HOUR;
	return new TimePoint(hour, minute);
    }

    /**
     * Extracts the <code>TimePoint</code> from a java.util.Date object.
     */
    public static TimePoint extractTimePoint(java.util.Date date){
	//java.util.Date must be used, since the JSpinners in
	//the appointment dialog return this class.
	//java.util.Calendar is used to avoid some deprecated methods
	//in java.util.Date.
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	return new TimePoint(cal.get(Calendar.HOUR_OF_DAY),
			     cal.get(Calendar.MINUTE));
    }

    public static TimePoint getNow(){
	LocalTime time = LocalTime.now();
	return new TimePoint(time.getHour(), time.getMinute());
    }

    /**
     * Gets a <code>TimePoint</code> that is one hour later than this <code>TimePoint</code>.
     * Returns <i>23:59</i> if <code>this</code> is <i>22:59</i> or later.
     */
    public TimePoint getNextHour(){
	//if this is earlier than 22:59
	if (this.precedesOrEquals(new TimePoint(MAX_HOUR - 1, MAX_MINUTE))){
	    return new TimePoint(hour + 1, minute);
	} else {
	    return new TimePoint(MAX_HOUR, MAX_MINUTE);
	}
    }

    /**
     * Creates a <code>TimePoint</code> that is a copy of this <code>TimePoint</code>,
     * but decremented by the given number of hours. If the given number of hours
     * to be decremented is more than the amount of hours in this day, it will
     * cycle through the previous day. For example, if <code>this</code> is 4:00,
     * and the given hour is 5, the result will be 23:00.
     * @param hours The number of hours to decrement.
     */
    public TimePoint getDecrementedHours(int hours){
	return new TimePoint((hour - hours) % HOURS_IN_DAY, minute);
    }

    public boolean canDecrementHour(int hours){
	return hour - hours < 0;
    }

    /**
     * Creates a <code>TimePoint</code> that is a copy of this <code>TimePoint</code>, but incremented by the given
     * number of minutes. The given number of minutes must be less than 60 and not less than 0.
     * @throws IllegalArgumentException If the given number of minutes is more than 60 or less than 0, or
     * if it causes a time that procedes 23:59.
     */
    public TimePoint getIncremented(int minutes){
	if (minutes >= MINUTES_IN_HOUR || minutes < 0){
	    throw new IllegalArgumentException("Number of minutes must be between 0 and 59");
	}
	int newMinute;
	int newHour;
	int minuteSum = minutes + minute;
	if (minuteSum >= MINUTES_IN_HOUR){
	    newMinute = minuteSum % MINUTES_IN_HOUR;
	    newHour = hour + 1;
	} else {
	    newMinute = minuteSum;
	    newHour = hour;
	}
	if (newHour > MAX_HOUR) throw new IllegalArgumentException("New hour: " + newHour + ": TimePoint can't be incremented by this much: " + minutes);
	return new TimePoint(newHour, newMinute);
    }

    /**
     * Creates a <code>TimePoint</code> that is a copy of this <code>TimePoint</code>, but decremented by the given
     * number of minutes. The given number of minutes must be between 0 and 60.
     * @throws IllegalArgumentException If the given number of minutes is more than 60 or less than 0, or
     * if it causes a time that procedes 23:59.
     */
    public TimePoint getDecremented(int minutes){
	if (minutes >= MINUTES_IN_HOUR || minutes < 0){
	    throw new IllegalArgumentException("Number of minutes must be less than 60");
	}
	int newMinute;
	int newHour;
	int minuteDiff = minute - minutes;
	if (minuteDiff < 0){
	    newMinute = MINUTES_IN_HOUR + minuteDiff;
	    newHour = hour - 1;
	} else {
	    newMinute = minuteDiff;
	    newHour = hour;
	}
	if (newHour == -1) newHour = MAX_HOUR;
	return new TimePoint(newHour, newMinute);
    }

    /**
     * Checks whether a <code>TimePoint</code> can be decremented by the given number
     * of minutes without going into the previous day. The given number of minutes
     * must be between 0 and 60.
     * @throws IllegalArgumentException If the number of minutes is not between 0 and 60.
     */
    public boolean canDecrement(int minutes){
	if (minutes >= MINUTES_IN_HOUR || minutes < 0){
	    throw new IllegalArgumentException("Minutes must be between 0 and 60");
	}
	if (hour > 0) return false;
	if (minute - minutes < 0) return true;
	return false;
    }
}