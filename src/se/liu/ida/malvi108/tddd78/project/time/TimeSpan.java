package se.liu.ida.malvi108.tddd78.project.time;

import java.io.Serializable;

/**
 * Class for spans of time.
 */
public final class TimeSpan implements Serializable
{
    private static final int MINUTES_IN_HOUR = 60;
    private TimePoint start;
    private TimePoint end;

    public TimeSpan(final TimePoint start, final TimePoint end) {
        if (end.precedes(start)){
            throw new IllegalArgumentException("End precedes start!");
        }
	this.start = start;
	this.end = end;
    }

    public TimePoint getEnd() {
	return end;
    }

    public TimePoint getStart() {
	return start;
    }

    /**
     * Gets the end time in hours.
     */
    public double getEndTimeInHours(){
	int endHour = end.getHour();
	int endMinute = end.getMinute();
	// since there are 60 minutes in an hour, the minutes must be divided by 60
	// to get the correct timeMultiplier
	return endHour + (double)endMinute/ MINUTES_IN_HOUR;
    }

    /**
     * Gets the end time, measured from 12pm in minutes.
     */
    public int getEndTimeInMinutes(){
	int endHour = end.getHour();
	int endMinute = end.getMinute();
	return endHour * MINUTES_IN_HOUR + endMinute;
    }

    /**
     * Gets the start time, measured from 12pm in minutes.
     */
    public int getStartTimeInMinutes(){
	int startHour = start.getHour();
	int startMinute = start.getMinute();
	return startHour * MINUTES_IN_HOUR + startMinute;
    }

    /**
     * Gets the start time in hours.
     */
    public double getStartTimeInHours(){
	int startHour = start.getHour();
	int startMinute = start.getMinute();
	// since there are 60 minutes in an hour, the minutes must be divided by 60
	// to get the correct timeMultiplier
	return startHour + (double)startMinute/MINUTES_IN_HOUR;
    }

    /**
     * Gets the duration in hours. Used by the dayview- and weekview component to
     * correctly size the appointments in the view. Does not take into account
     * whether the timespan spans over several days.
     */
    public double getDurationInHours(){
	return getEndTimeInHours() - getStartTimeInHours();
    }

    /**
     * Checks whether this TimeSpan overlaps with another.
     */
    public boolean overlapsWith(TimeSpan other){
	if (this.equals(other)) return true;

	TimePoint start1 = this.start;
	TimePoint start2 = other.start;
	if (start1.equals(start2)) return true;

	TimePoint end1 = this.end;
	TimePoint end2 = other.end;
	if (end1.equals(end2)) return true;
	//At this point, we know that the start and endpoints are different
        else if (start1.precedes(start2) && start2.precedes(end1) ||
	    start2.precedes(start1) && start1.precedes(end2)){
	    return true;
	}
	return false;
    }

    /**
     * Returns whether a TimePoint is within a time span (not including the endpoints).
     */
    public boolean contains(TimePoint time){
	if (time.precedes(end) && !time.precedesOrEquals(start) ) return true;
	return false;
    }

    @Override public String toString() {
	return start + " - " + end;
    }

    @Override public boolean equals(final Object obj) {
        if (!(obj instanceof TimeSpan)) return false;
        TimeSpan other = (TimeSpan) obj;
        return start.equals(other.start) && end.equals(other.end);
    }
}