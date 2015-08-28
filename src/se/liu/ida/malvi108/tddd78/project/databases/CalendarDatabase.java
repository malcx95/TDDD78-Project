package se.liu.ida.malvi108.tddd78.project.databases;

import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.appointments.Calendar;
import se.liu.ida.malvi108.tddd78.project.appointments.StandardAppointment;
import se.liu.ida.malvi108.tddd78.project.appointments.WholeDayAppointment;
import se.liu.ida.malvi108.tddd78.project.exceptions.CalendarAlreadyExistsException;
import se.liu.ida.malvi108.tddd78.project.exceptions.FileCorruptedException;
import se.liu.ida.malvi108.tddd78.project.exceptions.InvalidCalendarNameException;
import se.liu.ida.malvi108.tddd78.project.gui.dialogs.ReminderTimeOption;
import se.liu.ida.malvi108.tddd78.project.gui.dialogs.Ringtone;
import se.liu.ida.malvi108.tddd78.project.listeners.CalendarDatabaseListener;
import se.liu.ida.malvi108.tddd78.project.listeners.DatabaseSaveErrorListener;
import se.liu.ida.malvi108.tddd78.project.reminders.Reminder;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collection;
import java.awt.Color;
import java.util.List;
import java.util.logging.Level;

/**
 * The database that stores <code>Calendars</code>.
 *
 * @see Database
 * @see ToDoListDatabase
 */

public final class CalendarDatabase extends Database<Calendar>
{
    private final static CalendarDatabase INSTANCE = new CalendarDatabase();
    private List<CalendarDatabaseListener> listeners;

    public static CalendarDatabase getInstance() {
	return INSTANCE;
    }

    private CalendarDatabase(){
	super("calendar_database.dat");
	listeners = new ArrayList<>();
    }

    public void addCalendar(Calendar calendar) throws CalendarAlreadyExistsException{
	if (contains(calendar.getName())){
	    throw new CalendarAlreadyExistsException("Calendar already exists", get(calendar.getName()));
	}
	silentlyAddCalendar(calendar);
	notifyListeners();
	LOGGER.log(Level.INFO, "New calendar \"" + calendar.getName() + "\" added to the database.");
    }

    /**
     * Adds a calendar without notifying the <code>DatabaseListeners</code>.
     */
    public void silentlyAddCalendar(Calendar calendar) {
	elements.add(calendar);
    }

    public boolean contains(String name) {
	for (Calendar calendar : elements) {
	    if (calendar.getName().equals(name)){
		return true;
	    }
	}
	return false;
    }

    /**
     * Writes the changes made to the database to the database file. Writes in this format:<br>
     * - Number of calendars in the database<br>
     * (for each calendar in the database){ <br>
     * 	- The calendar's name. <br>
     * 	- The calendar's color. <br>
     * 	- The number of <code>StandardAppointments</code>. <br>
     * 	- Writes all <code>StandardAppointments</code> in the calendar (as their own objects and not a list).<br>
     * 	    For each appointment, a flag is written after it telling whether the appointment has a reminder,<br>
     * 	    and if it does, it writes the reminder's date, time, ringtone, subject, relTime string and timeOption.<br>
     *  - The number of <code>WholeDayAppointments</code>. Writes reminders in the same way as for standard appointments.<br>
     * 	- Writes all <code>WholeDayAppointments</code> in the calendar. (as their own objects and not a list) <br>
     * }
     * <br>
     * If the save fails, it notifies it's <code>ErrorListeners</code>.
     */
    public void tryToSaveChanges(){
	try (ObjectOutput out = new ObjectOutputStream(new FileOutputStream(databaseFile))){
	    out.writeObject(elements.size()); //writes the size of the database
	    for (Calendar calendar : elements) {
		out.writeObject(calendar.getName());
		out.writeObject(calendar.getColor());
		out.writeObject(calendar.getNumberOfStandardApps());
		writeStandardAppointments(out, calendar);
		out.writeObject(calendar.getNumberOfWholeDayApps());
		writeWholeDayAppointments(out, calendar);
	    }
	    LOGGER.log(Level.INFO, "CalendarDatabase sucessfully saved.");
	} catch (FileNotFoundException ignored){
	    //If the save file is not found, probably because someone tampered with it while the application was running,
	    //a new file is created.
	    LOGGER.log(Level.WARNING, "CalendarDatabase file not found, trying to create new file...");
	    try {
		createDatabaseFile();
		tryToSaveChanges();
	    	LOGGER.log(Level.INFO, "New CalendarDatabase file created.");
	    } catch (IOException ex) {
		LOGGER.log(Level.SEVERE, "Couldn't create new CalendarDatabase file! " + ex.getMessage());
		notifyErrorListeners(ex);
	    }
	} catch (IOException ex){
	    LOGGER.log(Level.SEVERE, "Couldn't save the CalendarDatabase! Error: " + ex.getMessage());
	    notifyErrorListeners(ex);
	}
    }

    private void writeWholeDayAppointments(final ObjectOutput out, final Calendar calendar)
	    throws IOException {
	for (WholeDayAppointment appointment : calendar.getWholeDayAppointments()) {
	    out.writeObject(appointment);
	    writeReminder(out, appointment.getReminder());
	}
    }

    private void writeStandardAppointments(final ObjectOutput out, final Calendar calendar)
	    throws IOException {
	for (StandardAppointment appointment : calendar.getStandardAppointments()) {
	    out.writeObject(appointment);
	    writeReminder(out, appointment.getReminder());
	}
    }

    private void writeReminder(final ObjectOutput out, final Reminder reminder) throws IOException{
	if (reminder == null) {
	    out.writeObject(false); //no reminder
	} else {
	    out.writeObject(true); // flag telling whether the appointment has a reminder.
	    out.writeObject(reminder.getDate());
	    out.writeObject(reminder.getTime());
	    out.writeObject(reminder.getRingtone());
	    out.writeObject(reminder.getSubject());
	    out.writeObject(reminder.getRelTime());
	    out.writeObject(reminder.getTimeOption());
	}
    }

    public void loadDatabase() throws FileNotFoundException, IOException, FileCorruptedException{
	try (ObjectInput in = new ObjectInputStream(new FileInputStream(databaseFile))) {
	    int size = (int) in.readObject();
	    for (int i = 0; i < size; i++) {
		Calendar calendar = createCalendar(in);
		elements.add(calendar);
		loadStandardAppointments(in, calendar);
		loadWholeDayAppointments(in, calendar);
	    }
	    LOGGER.log(Level.INFO, "CalendarDatabase successfully loaded.");
	} catch (InvalidCalendarNameException | ClassNotFoundException | StreamCorruptedException ex){
	    throw new FileCorruptedException("CalendarDatabase file corrupted.", ex);
	} catch (FileNotFoundException ex){
	    LOGGER.log(Level.WARNING, "CalendarDatabase file not found, creating new file...");
	    createDatabaseFile();
	    LOGGER.log(Level.INFO, "New CalendarDatabase file sucessfully created.");
	    throw ex;
	}
    }

    private void loadWholeDayAppointments(final ObjectInput in, final Calendar calendar)
	    throws IOException, ClassNotFoundException {
	int size = (int) in.readObject();
	for (int i = 0; i < size; i++) {
	    WholeDayAppointment appointment = (WholeDayAppointment) in.readObject();
	    readReminder(in, appointment);
	    calendar.silentlyAddWholeDayAppointment(appointment);
	}
    }

    private void loadStandardAppointments(final ObjectInput in, final Calendar calendar)
	    throws IOException, ClassNotFoundException {
	int size = (int) in.readObject();
	for (int i = 0; i < size; i++) {
	    StandardAppointment appointment = (StandardAppointment) in.readObject();
	    readReminder(in, appointment);
	    calendar.silentlyAddStandardAppointment(appointment);
	}
    }

    private void readReminder(final ObjectInput in, final Appointment appointment)
	    throws ClassNotFoundException, IOException
    {
	boolean hasReminder = (boolean) in.readObject();
	if (hasReminder){
	    Date date = (Date) in.readObject();
	    TimePoint time = (TimePoint) in.readObject();
	    Ringtone ringtone = (Ringtone) in.readObject();
	    String subject = (String) in.readObject();
	    String relTime = (String) in.readObject();
	    ReminderTimeOption option = (ReminderTimeOption) in.readObject();
	    if (reminderTimeHasPassed(time, date)) {
		relTime = appointment.getDate().toString();
		if (appointment instanceof StandardAppointment){
		    relTime += ", kl " + ((StandardAppointment) appointment).getDuration().getStart();
		}
	    }
	    appointment.setReminder(new Reminder(date, time, ringtone, subject, relTime, option));
	}
    }

    private boolean reminderTimeHasPassed(TimePoint time, Date date) {
	Date today = Date.getToday();
 	return (time.precedes(TimePoint.getNow()) && date.equals(today) || date.precedes(today));
    }

    private Calendar createCalendar(final ObjectInput in)
	    throws IOException, ClassNotFoundException, InvalidCalendarNameException{
	String name = (String) in.readObject();
	Color color = (Color) in.readObject();
	Calendar calendar = new Calendar(name, color);
	return calendar;
    }


    /**
     * Gets all the calendars in the database.
     */
    public Calendar[] getCalendars() {
	Calendar[] result = new Calendar[elements.size()];
	for (int i = 0; i < result.length; i++) {
	    result[i] = elements.get(i);
	}
	return result;
    }

    /**
     * Gets all the enabled calendars in the database.
     */
    public Iterable<Calendar> getEnabledCalendars(){
	Collection<Calendar> result = new ArrayList<>();
	for (Calendar calendar : elements) {
	    if (calendar.isEnabled()){
		result.add(calendar);
	    }
	}
	return result;
    }

    /**
     * Removes the calendars provided by the <code>calendars</code> parameter
     * from the database.
     */
    public void removeCalendars(Iterable<Calendar> calendars){
	for (Calendar calendar : calendars) {
	    elements.remove(calendar);
	}
	LOGGER.log(Level.INFO, "Calendars removed: \n" + calendars);
	notifyListeners();
    }

    /**
     * Gets the <code>Calendar</code> in the database with the given name. Returns null if
     * calendar does not exist.
     */
    public Calendar get(String name){
	for (Calendar calendar : elements) {
	    if (calendar.getName().equals(name)) return calendar;
	}
	return null;
    }

    public void addDatabaseListener(CalendarDatabaseListener listener) {
	listeners.add(listener);
    }

    public void notifyListeners() {
        for (CalendarDatabaseListener listener : listeners) {
            listener.databaseChanged();
        }
    }

    /**
     * Signals the <code>ErrorListeners</code> that the database failed to save.
     * @param ex The IOException that caused the error.
     */
    private void notifyErrorListeners(final IOException ex) {
	for (DatabaseSaveErrorListener listener : errorListeners) {
	    listener.calendarDatabaseSaveFailed(ex);
	}
    }
}