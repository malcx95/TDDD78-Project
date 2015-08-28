package se.liu.ida.malvi108.tddd78.project.appointments;

import se.liu.ida.malvi108.tddd78.project.databases.CalendarDatabase;
import se.liu.ida.malvi108.tddd78.project.listeners.ReminderListener;
import se.liu.ida.malvi108.tddd78.project.reminders.Reminder;
import se.liu.ida.malvi108.tddd78.project.time.Date;

import java.io.Serializable;

/**
 * General class for appointments. All appointments have a date, which must not be null. Appointments also have
 * a subject, location, reminder and description, all of which are nullable. Appointments are stored in <code>Calendars</code>.
 *
 * @see Calendar
 * @see StandardAppointment
 * @see WholeDayAppointment
 */
public class Appointment implements Serializable, ReminderListener
{
    /**
     * The subject or "title" of the appointment.
     */
    protected String subject;
    /**
     * The location of the appointment.
     */
    protected String location;
    /**
     * Description of the appointment.
     */
    protected String description;
    /**
     * The date at which the appointment takes place.
     */
    protected Date date;
    /**
     * The reminder for this appointment.
     */
    protected transient Reminder reminder;

    /**
     * Constructor for general appointments. Only subclasses of <code>Appointment</code> should use this constructor.
     *
     * @param subject The subject or "title" of the appointment.
     * @param description Description of the appointment.
     * @param location The location of the appointment.
     * @param date The date at which the appointment takes place.
     * @param reminder A reminder for the appointment (can be null)
     */
    protected Appointment(final String subject, final String description,
                          final String location, final Date date, Reminder reminder) {
        if (date == null){
            throw new IllegalArgumentException("Date must not be null!");
        }
	this.subject = subject;
	this.description = description;
	this.location = location;
	this.date = date;
        this.reminder = reminder;
        if (reminder != null) {
            reminder.setReminderListener(this);
        }
    }

    public String getSubject() {
	return subject;
    }

    public String getLocation() {
	return location;
    }

    public String getDescription() {
	return description;
    }

    public Date getDate(){
	return date;
    }

    /**
     * Cancels the <code>Appointment's</code> reminder. Does nothing if the appointment has no reminder.
     */
    public void cancelReminder(){
        if (reminder != null) {
            reminder.cancel();
            reminder = null;
            CalendarDatabase.getInstance().tryToSaveChanges();
        }
    }

    public Reminder getReminder() {
        return reminder;
    }

    /**
     * Sets the reminder for this appointment, and sets the appointment as the reminders listener.
     */
    public void setReminder(Reminder reminder){
        this.reminder = reminder;
        reminder.setReminderListener(this);
    }

    /**
     * Invoked when the reminder is fired. Deletes the reminder once it's been fired.
     */
    @Override public void reminderFired() {
        cancelReminder();
    }
}
