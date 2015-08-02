package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.appointments.Calendar;
import se.liu.ida.malvi108.tddd78.project.databases.CalendarDatabase;
import se.liu.ida.malvi108.tddd78.project.appointments.StandardAppointment;
import se.liu.ida.malvi108.tddd78.project.appointments.WholeDayAppointment;
import se.liu.ida.malvi108.tddd78.project.gui.miscellaneous.DateSelector;
import se.liu.ida.malvi108.tddd78.project.reminders.Reminder;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;
import se.liu.ida.malvi108.tddd78.project.time.TimeSpan;

import javax.swing.*;
import javax.swing.JSpinner.DateEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for inputing appointments. The <code>setInitialParameters</code> method
 * (as well as all other setters)  can be used for determining what values should
 * be pre-entered into the dialog, and <code>inputAppointment</code> for showing
 * the dialog, inputing the appointment and saving it to the database.
 */
public final class AppointmentDialog
{
    /**
     * Tells the inputDialog method that the caller wants to add a new appointment
     */
    public final static int NEW = 0;
    /**
     * Tells the inputDialog method that the caller wants to edit an existing appointment
     */
    public final static int EDIT = 1;
    /**
     * The minimum duration an appointment can have in minutes.
     */
    private final static int MINIMUM_DURATION = 5;
    private static final int DESCRIPTION_HEIGHT = 100;
    private final static int LATEST_HOUR = 23;
    private final static int LATEST_START_MINUTE = 54;
    private final static int LATEST_END_MINUTE = 59;
    /**
     * The latest value you can enter into the startTimeSpinner, because of the minimum duration an appointment can have.
     */
    private final static TimePoint LATEST_START = new TimePoint(LATEST_HOUR, LATEST_START_MINUTE);
    /**
     * The latest value you can enter into the endTimeSpinner.
     */
    private final static TimePoint LATEST_END = new TimePoint(LATEST_HOUR, LATEST_END_MINUTE);
    /**
     * The earliest value you can enter into the endTimeSpinner.
     */
    private final static TimePoint EARLIEST_END = new TimePoint(0, MINIMUM_DURATION);
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;
    private JTextField subjectField;
    private JTextField locationField;
    private DateSelector dateSelector;
    private JCheckBox wholeDayBox;
    private JComboBox<Calendar> calendarSelector;
    private JPanel timePanel;
    private JTextArea descriptionArea;
    private ReminderPanel reminderPanel;

    public AppointmentDialog(){
	subjectField = new JTextField("Ny aktivitet");
	locationField = new JTextField();

	dateSelector = new DateSelector();
	wholeDayBox = new JCheckBox("Heldag", false);
	startTimeSpinner = new JSpinner();
	endTimeSpinner = new JSpinner();
	startTimeSpinner.setModel(new SpinnerDateModel());
	startTimeSpinner.setEditor(new DateEditor(startTimeSpinner, "HH:mm"));
	endTimeSpinner.setModel(new SpinnerDateModel());
	endTimeSpinner.setEditor(new DateEditor(endTimeSpinner, "HH:mm"));
	timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	timePanel.add(new JLabel("Start"));
	timePanel.add(startTimeSpinner);
	timePanel.add(new JLabel("Slut"));
	timePanel.add(endTimeSpinner);

	final CalendarDatabase database = CalendarDatabase.getInstance();
	calendarSelector = new JComboBox<>(database.getCalendars());

	descriptionArea = new JTextArea();
	descriptionArea.setLineWrap(true);

	reminderPanel = new ReminderPanel(wholeDayBox.isSelected());

	addTimeSelectorListeners();
	addWholeDayListener();
	TimePoint now = TimePoint.getNow();
	setTimeSpan(new TimeSpan(now, now.getNextHour()));
    }

    /**
     * Pre-enters all the values of the given appointment to the fields of the dialog.
     */
    public void setInitialParameters(Appointment appointment){
	setSubject(appointment.getSubject());
	setDescription(appointment.getDescription());
	setDate(appointment.getDate());
	setLocation(appointment.getLocation());
	Calendar calendar = Calendar.getAssociatedCalendar(appointment);
	assert calendar != null: "Internal error, calendar is null";
	setCalendar(calendar);
	boolean wholeDay = appointment instanceof WholeDayAppointment;
	if (!wholeDay) {
	    StandardAppointment stdApp = (StandardAppointment) appointment;
	    setTimeSpan(stdApp.getDuration());
	}
	setWholeDayBox(wholeDay);
	setReminder(appointment.getReminder(), wholeDay);
    }

    public void setReminder(Reminder reminder, boolean wholeDay){
	if (reminder == null){
	    reminderPanel.setRemindBox(false);
	} else {
	    reminderPanel.setRemindBox(true);
	    reminderPanel.setWholeDay(wholeDay);
	    reminderPanel.setRingtone(reminder.getRingtone());
	    reminderPanel.setReminderTime(reminder.getTimeOption());
	}
	reminderPanel.setCorrectPlayButtonState();
    }

    public void setSubject(String subject){
	subjectField.setText(subject);
    }

    public void setDescription(String text){
	descriptionArea.setText(text);
    }

    public void setDate(Date date){
	dateSelector.setDate(date);
    }

    public void setTimeSpan(TimeSpan span){
	TimePoint start = span.getStart();
	TimePoint end = span.getEnd();
	//The JSpinnerDateModel uses java.util.Date, so this as well as java.util.Calendar must be used
	java.util.Calendar cal = java.util.Calendar.getInstance();
	cal.set(1, java.util.Calendar.JANUARY, 1, /*random date, but not random time*/
		start.getHour(), start.getMinute());
	startTimeSpinner.setValue(cal.getTime());
	cal.set(1, java.util.Calendar.JANUARY, 1, /*random date, but not random time*/
		end.getHour(), end.getMinute());
	endTimeSpinner.setValue(cal.getTime());
    }

    public void setCalendar(Calendar calendar){
	calendarSelector.setSelectedItem(calendar);
    }

    public void setWholeDayBox(boolean value){
	wholeDayBox.setSelected(value);
	for (Component component : timePanel.getComponents()) {
	    component.setEnabled(!value);
	}
	reminderPanel.setWholeDay(value);
    }

    public void setLocation(String location){
	locationField.setText(location);
    }

    /**
     * Adds ChangeListeners to the time spinners, so that the user
     * cannot set a start time that precedes the end time.
     */
    private void addTimeSelectorListeners() {
	ChangeListener startListener = new ChangeListener()
	{
	    @Override public void stateChanged(final ChangeEvent e) {
		java.util.Date startValue = (java.util.Date) startTimeSpinner.getValue(); //java.util.Date must to be used
		java.util.Date endValue = (java.util.Date) endTimeSpinner.getValue(); //since the spinners return instances of these
		TimePoint start = TimePoint.extractTimePoint(startValue);
		TimePoint end = TimePoint.extractTimePoint(endValue);
		if (!start.precedesOrEquals(LATEST_START)){
		    setTimeSpan(new TimeSpan(LATEST_START, LATEST_END));
		} else if (start.getDistanceTo(end) < MINIMUM_DURATION){ //If the user tried to enter a start value so that the duration is less than the minimum...
		    TimeSpan span = new TimeSpan(start, start.getIncremented(MINIMUM_DURATION));
		    setTimeSpan(span);
		}
		setCorrectRemindBoxState();
	    }
	};
	ChangeListener endListener = new ChangeListener()
	{
	    @Override public void stateChanged(final ChangeEvent e) {
		java.util.Date startValue = (java.util.Date) startTimeSpinner.getValue(); //java.util.Date must to be used
		java.util.Date endValue = (java.util.Date) endTimeSpinner.getValue(); //since the spinners return instances of these
		TimePoint start = TimePoint.extractTimePoint(startValue);
		TimePoint end = TimePoint.extractTimePoint(endValue);
		if (!end.precedesOrEquals(LATEST_END)){
		    setTimeSpan(new TimeSpan(LATEST_START, LATEST_END));
		} else if (end.precedes(EARLIEST_END)){
		    setTimeSpan(new TimeSpan(new TimePoint(0, 0), EARLIEST_END));
		} else if (start.getDistanceTo(end) < MINIMUM_DURATION){
		    TimeSpan span = new TimeSpan(start, start.getIncremented(MINIMUM_DURATION));
		    setTimeSpan(span);
		}
	    }
	};
	startTimeSpinner.addChangeListener(startListener);
	endTimeSpinner.addChangeListener(endListener);
    }

    private void setCorrectRemindBoxState() {
	TimePoint start = TimePoint.extractTimePoint((java.util.Date) startTimeSpinner.getValue());
	Date date = dateSelector.getSelectedDate();
	Date today = Date.getToday();
	boolean enabled = !(start.precedes(TimePoint.getNow()) && date.equals(today)) || date.precedes(today);
	reminderPanel.setEnabled(enabled);
    }

    /**
     * Adds an actionlistener to the <code>wholeDay</code> checkbox that
     * disables the timeSpinners when the checkbox is selected, and
     * enables them when it is deselected.
     */
    private void addWholeDayListener() {
	ActionListener listener = new ActionListener(){
	    @Override public void actionPerformed(final ActionEvent e) {
		boolean selected = wholeDayBox.isSelected();
		for (Component component : timePanel.getComponents()) {
		    component.setEnabled(!selected);
		}
		reminderPanel.setWholeDay(selected);
	    }
	};
	wholeDayBox.addActionListener(listener);
    }

    public void inputAppointment(int inputType) {
	String title;
	if (inputType == NEW) title = "Ny aktivitet";
	else title = "Ändra aktivitet";
	setCorrectRemindBoxState();
	int option = JOptionPane.showOptionDialog(null, createInputs(), title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				  null, null);
	if (option == JOptionPane.OK_OPTION || inputType == EDIT) { //if the user wants to edit, an identical appointment should be created
	    Date date = dateSelector.getSelectedDate();
	    String subject = subjectField.getText();
	    String description = descriptionArea.getText();
	    String location = locationField.getText();
	    Calendar calendar = (Calendar) calendarSelector.getSelectedItem();
	    if (wholeDayBox.isSelected()) {
	    	calendar.addWholeDayAppointment(new WholeDayAppointment(subject, description, location, date, reminderPanel.getReminder(date, null, subject)));
	    } else {
	    	TimePoint start = TimePoint.extractTimePoint((java.util.Date) startTimeSpinner.getValue()); //JSpinner returns java.util.Date, so this is inescapable.
	    	TimePoint end = TimePoint.extractTimePoint((java.util.Date) endTimeSpinner.getValue());
	    	TimeSpan timeSpan = new TimeSpan(start, end);
	    	calendar.addStandardAppointment(new StandardAppointment(timeSpan, date, subject, location, description, reminderPanel.getReminder(date, timeSpan.getStart(), subject)));
	    }
	}
    }

    private JComponent[] createInputs(){
	JScrollPane descriptionPane = new JScrollPane(descriptionArea);
	descriptionPane.setPreferredSize(new Dimension(descriptionPane.getPreferredSize().width, DESCRIPTION_HEIGHT));
	return new JComponent[] {
		new JLabel("Ämne"), subjectField,
		new JLabel("Plats"), locationField,
		wholeDayBox,
		timePanel,
		new JLabel("Datum"), dateSelector,
		new JLabel("Kalender"), calendarSelector,
		new JLabel("Beskrivning"), descriptionPane,
		reminderPanel
	};
    }
}
