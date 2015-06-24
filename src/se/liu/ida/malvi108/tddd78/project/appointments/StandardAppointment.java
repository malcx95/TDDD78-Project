package se.liu.ida.malvi108.tddd78.project.appointments;

import se.liu.ida.malvi108.tddd78.project.reminders.Reminder;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimeSpan;

/**
 * <code>StandardAppointments</code> are appointments which have a specific duration in a day defined by a <code>TimeSpan</code>.
 * These appointments are suitable for events like "Dinner with family" or "Lunch meeting with company".
 * @see Appointment
 * @see WholeDayAppointment
 */
public class StandardAppointment extends Appointment
{
    private TimeSpan duration;

    public StandardAppointment(final TimeSpan duration, final Date date, final String subject,
                               final String location, final String description, Reminder reminder) {
	super(subject, description, location, date, reminder);
        assert duration != null: "Internal error, duration is null";
	this.duration = duration;
    }

    public TimeSpan getDuration() {
	return duration;
    }

    @Override public String toString() {
	return getSubject() + "\n" + duration +
	       "\nLocation: " + getLocation() + "\nDescription: " + getDescription() + "\n";
    }
}