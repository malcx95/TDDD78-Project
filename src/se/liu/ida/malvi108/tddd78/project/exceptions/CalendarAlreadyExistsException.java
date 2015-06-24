package se.liu.ida.malvi108.tddd78.project.exceptions;

import se.liu.ida.malvi108.tddd78.project.appointments.Calendar;

/**
 * Thrown when a calendar in the CalendarDatabase already exists.
 */
public class CalendarAlreadyExistsException extends Exception
{
    private final Calendar existingCalendar;

    public CalendarAlreadyExistsException(String message, Calendar existingCalendar){
	super(message);
	this.existingCalendar = existingCalendar;
    }

    public Calendar getExistingCalendar() {
	return existingCalendar;
    }
}
