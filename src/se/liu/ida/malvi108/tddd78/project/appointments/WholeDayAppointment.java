package se.liu.ida.malvi108.tddd78.project.appointments;

import se.liu.ida.malvi108.tddd78.project.reminders.Reminder;
import se.liu.ida.malvi108.tddd78.project.time.Date;

/**
 * <code>WholeDayAppointments</code> are appointments which span over an entire day. These
 * appointments are suitable for events like "My birthday".
 */
public class WholeDayAppointment extends Appointment
{
    public WholeDayAppointment(final String subject, final String description, final String location,
			       final Date date, Reminder reminder){
	super(subject, description, location, date, reminder);
    }

    @Override public String toString() {
    	return getSubject() + "\n" + "The whole day" +
    	       "\nLocation: " + getLocation() + "\nDescription: " + getDescription() + "\n";
        }
}
