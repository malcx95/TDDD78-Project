package se.liu.ida.malvi108.tddd78.project.appointments;

import se.liu.ida.malvi108.tddd78.project.databases.CalendarDatabase;
import se.liu.ida.malvi108.tddd78.project.listeners.CalendarListener;
import se.liu.ida.malvi108.tddd78.project.exceptions.InvalidCalendarNameException;
import se.liu.ida.malvi108.tddd78.project.listeners.CalendarPropertyListener;
import se.liu.ida.malvi108.tddd78.project.time.Date;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>Calendars</code> are essentially collections of <code>Appointments</code>. To the user, they act as
 * categories for appointments, such as "work" or "home". Calendars separate <code>WholeDayAppointments</code> from
 * <code>StandardAppointments</code> by storing them in different lists.
 * <br>
 * <br>
 * <code>Calendars</code> can be listened to in two ways, as a <code>CalendarListener</code>,
 * which is notified when the content (it's appointments) is changed, or
 * when the calendar is enabled or disabled, and as a <code>CalendarPropertyListener</code>, which is notified
 * when the name or color of a <code>Calendar</code> is changed.
 * <br>
 * <br>
 * Calendars are stored in the <code>CalendarDatabase</code>.
 *
 * @see CalendarListener
 * @see CalendarPropertyListener
 * @see Appointment
 */
public class Calendar implements Serializable
{
    /**
     * The name of the <code>Calendar</code>.
     */
    private String name;
    /**
     * The color of the <code>Calendar</code>, that is the color which every appointment in this
     * calendar will have in the view.
     */
    private Color color;
    private List<StandardAppointment> stdAppointments;
    private List<WholeDayAppointment> wholeDayAppointments;
    private transient List<CalendarListener> listeners;
    private transient List<CalendarPropertyListener> propertyListeners;
    private final static Logger LOGGER = Logger.getLogger(Calendar.class.getName());
    /**
     * Controls whether the calendar should be shown in the view.
     */
    private boolean enabled;
    /**
     * The maximum length allowed for a calendar name.
     */
    private final static int MAXIMUM_NAME_LENGTH = 20;
    /**
     * The minimum length allowed for a calendar name.
     */
    private final static int MINIMUM_NAME_LENGTH = 2;

    /**
     * Creates a calendar with the given name and color. The name length
     * must be within the MINIMUM_NAME_LENGTH = 2 and the MAXIMUM_NAME_LENGTH = 20.
     * The color must not be null.
     * @throws InvalidCalendarNameException If the calendar name is longer than the MAXIMUM_NAME_LENGTH
     * or shorter than the MINIMUM_NAME_LENGTH.
     */
    public Calendar(final String name, final Color color) throws InvalidCalendarNameException {
	checkCalendarName(name);
	assert color != null: "Internal error, color is null";
	assert name != null: "Internal error, name is null";
	this.name = name;
	this.color = color;
	stdAppointments = new ArrayList<>();
	wholeDayAppointments = new ArrayList<>();
	listeners = new ArrayList<>();
	propertyListeners = new ArrayList<>();
	enabled = true;
    }

    /**
     * Checks if a calendar name is valid in terms of
     * length. If not, it throws an <code>InvalidCalendarNameException</code>.
     * Does nothing if the name is valid.
     * @param name The name to be checked.
     * @throws InvalidCalendarNameException If the name is invalid.
     */
    private void checkCalendarName(final String name) throws InvalidCalendarNameException {
	if (name.length() > MAXIMUM_NAME_LENGTH) {
	    throw new InvalidCalendarNameException(name, InvalidCalendarNameException.NAME_TOO_LONG);
	} else if (name.length() < MINIMUM_NAME_LENGTH) {
	    throw new InvalidCalendarNameException(name, InvalidCalendarNameException.NAME_TOO_SHORT);
	}
    }

    /**
     * Sets the name of this calendar. The caller of this method must make sure the name
     * is not already taken in the database.
     *
     * @throws InvalidCalendarNameException If the name is too long or too short.
     */
    public void setName(final String name) throws InvalidCalendarNameException {
	checkCalendarName(name);
	assert !CalendarDatabase.getInstance().contains(name)
		|| name.equals(this.name): "Internal error: name already exists in database";
	this.name = name;
	notifyPropertyListeners();
    }

    private void notifyPropertyListeners() {
	for (CalendarPropertyListener listener : propertyListeners) {
	    listener.calendarPropertyChanged();
	}
    }

    public void setColor(final Color color) {
	this.color = color;
	notifyPropertyListeners();
    }

    /**
     * Gets all the <code>StandardAppointments</code> in this calendar that occur on
     * the given date.
     */
    public Iterable<StandardAppointment> getStandardAppointments(Date date){
	Collection<StandardAppointment> result = new ArrayList<>();
	for (StandardAppointment appointment : stdAppointments) {
	    if (date.equals(appointment.getDate())){
		result.add(appointment);
	    }
	}
	return result;
    }

    public Iterable<WholeDayAppointment> getWholeDayAppointments() {
	return wholeDayAppointments;
    }

    public Iterable<StandardAppointment> getStandardAppointments() {
    	return stdAppointments;
        }

    /**
     * Gets all <code>WholeDayAppointments</code> in this calendar that occur on the given date.
     */
    public Iterable<WholeDayAppointment> getWholeDayAppointments(Date date) {
	Collection<WholeDayAppointment> result = new ArrayList<>();
	for (WholeDayAppointment appointment : wholeDayAppointments) {
	    if (date.equals(appointment.getDate())){
		result.add(appointment);
	    }
	}
	return result;
    }

    public void addWholeDayAppointment(WholeDayAppointment app){
	wholeDayAppointments.add(app);
	notifyListeners();
	LOGGER.log(Level.INFO, "WholeDayAppointment \"" + app.getSubject() +
				       "\" added to calendar \"" + name + "\".");
	CalendarDatabase.getInstance().tryToSaveChanges();
    }

    public void addStandardAppointment(StandardAppointment app) {
	stdAppointments.add(app);
	notifyListeners();
	LOGGER.log(Level.INFO, "StandardAppointment \"" + app.getSubject() +
			       "\" added to calendar \"" + name + "\".");
	CalendarDatabase.getInstance().tryToSaveChanges();
    }

    /**
     * Adds a <code>WholeDayAppointment</code> to the calendar without
     * notifying the calendar's listeners listeners or saving.
     */
    public void silentlyAddWholeDayAppointment(WholeDayAppointment app){
	wholeDayAppointments.add(app);
    }

    /**
     * Adds a <code>StandardAppointment</code> to the calendar without
     * notifying the calendar's listeners listeners or saving.
     */
    public void silentlyAddStandardAppointment(StandardAppointment app){
	stdAppointments.add(app);
    }

    public void removeAppointment(Appointment app){
	assert this.contains(app): "Internal error, appointment isn't in the calendar";
	if (app instanceof StandardAppointment){
	    StandardAppointment stdApp = (StandardAppointment) app;
	    stdAppointments.remove(stdApp);
	    LOGGER.log(Level.INFO, "StandardAppointment \"" + app.getSubject() +
				   "\" removed from calendar \"" + name + "\".");
	} else{
	    WholeDayAppointment wdApp = (WholeDayAppointment) app;
	    wholeDayAppointments.remove(wdApp);
	    LOGGER.log(Level.INFO, "WholeDayAppointment \"" + app.getSubject() +
	    				   "\" removed from calendar \"" + name + "\".");
	}
	notifyListeners();
	CalendarDatabase.getInstance().tryToSaveChanges();
    }

    public String getName() {
	return name;
    }

    public Color getColor() {
	return color;
    }

    @Override public String toString() {
	return name;
    }

    public void notifyListeners(){
	for (CalendarListener listener : listeners) {
	    listener.calendarContentChangedOrEnabledToggled();
	}
    }

    public void addCalendarListener(CalendarListener listener){
	if (!listeners.contains(listener)){
	    listeners.add(listener);
	}
    }

    public void addPropertyListener(CalendarPropertyListener listener){
	if (!propertyListeners.contains(listener)){
	    propertyListeners.add(listener);
	}
    }

    /**
     * Gets the <code>Calendar</code> of which the given <code>Appointment</code> is associated with.
     * Returns <code>null</code> if the associated <code>Calendar</code> couldn't
     * be found (which should never happen since an <code>Appointment</code> always
     * has an associated <code>Calendar</code>).
     */
    public static Calendar getAssociatedCalendar(Appointment app){
	Calendar[] calendars = CalendarDatabase.getInstance().getCalendars();
	for (Calendar calendar : calendars) {
	    if (calendar.contains(app)) return calendar;
	}
	return null;
    }

    /**
     * Checks if this contains a given <code>Appointment</code>.
     */
    private boolean contains(Appointment app){
	if (app instanceof StandardAppointment){
	    StandardAppointment stdApp = (StandardAppointment) app;
	    return stdAppointments.contains(stdApp);
	}
	WholeDayAppointment wdApp = (WholeDayAppointment) app;
	return wholeDayAppointments.contains(wdApp);
    }

    public void setEnabled(final boolean enabled) {
	this.enabled = enabled;
	notifyListeners();
    }

    public boolean isEnabled() {
	return enabled;
    }

    public int getNumberOfStandardApps(){
	return stdAppointments.size();
    }

    public int getNumberOfWholeDayApps(){
	return wholeDayAppointments.size();
    }
}
