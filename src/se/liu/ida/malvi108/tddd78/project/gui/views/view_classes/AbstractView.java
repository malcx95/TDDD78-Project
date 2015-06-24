package se.liu.ida.malvi108.tddd78.project.gui.views.view_classes;

import se.liu.ida.malvi108.tddd78.project.gui.dialogs.AppointmentDialog;
import se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations.AppointmentIllustration;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_components.ViewComponent;
import se.liu.ida.malvi108.tddd78.project.listeners.AppIllustrationListener;
import se.liu.ida.malvi108.tddd78.project.listeners.CalendarListener;
import se.liu.ida.malvi108.tddd78.project.listeners.CalendarPropertyListener;
import se.liu.ida.malvi108.tddd78.project.listeners.CalendarDatabaseListener;
import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.appointments.Calendar;
import se.liu.ida.malvi108.tddd78.project.databases.CalendarDatabase;
import se.liu.ida.malvi108.tddd78.project.appointments.StandardAppointment;
import se.liu.ida.malvi108.tddd78.project.listeners.ViewComponentListener;
import se.liu.ida.malvi108.tddd78.project.listeners.ViewListener;
import se.liu.ida.malvi108.tddd78.project.time.Date;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Abstract class for views. Each view contains one or several <code>ViewComponents</code>, which hold the actual appointments.
 * Every implementation of a view MUST put their <code>ViewComponents</code> in the <code>viewComponents</code> list.
 * All views also contain a <code>navigationPanel</code> for navigating through units of time, and need only to
 * inherit it from this abstract class and place it somewhere.
 * @see DayView
 * @see MonthView
 */
public abstract class AbstractView extends JPanel implements View, CalendarDatabaseListener, ViewComponentListener,
	AppIllustrationListener, CalendarListener, CalendarPropertyListener
{
    private final static ImageIcon LEFT_ARROW = new ImageIcon(AbstractView.class.getResource(
	    "/se/liu/ida/malvi108/tddd78/project/images/left_arrow.png"));
    private final static ImageIcon RIGHT_ARROW = new ImageIcon(AbstractView.class.getResource(
    	    "/se/liu/ida/malvi108/tddd78/project/images/right_arrow.png"));
    protected final static int SIDE_PANEL_WIDTH = 200;
    protected final static int NAVIGATION_PANEL_HEIGHT = 70;
    protected final static int PREVIOUS_NEXT_BUTTON_WIDTH = 50;
    protected final static int DATE_FONT_SIZE = 40;
    protected JPanel navigationPanel;
    protected List<ViewListener> listeners;
    //the previousButton in the navigationPanel is a field since it needs to be
    //enabled or disabled dynamically by the views.
    protected JButton previousButton;
    /**
     * The list of different <code>ViewComponents</code> in the view.
     * All subclasses must put their <code>ViewComponents</code> here.
     * If <code>ViewComponents</code> are added to the view over time,
     * they must also be added here. This is to make sure users of views
     * can access the selected appointment in the view.
     */
    protected List<ViewComponent> viewComponents;

    protected AbstractView(){
	createNavigationPanel();
	CalendarDatabase.getInstance().addDatabaseListener(this);
	listenToAllCalendars();
	listeners = new ArrayList<>();
	viewComponents = new ArrayList<>();
    }

    /**
     * Adds this to the lists of calendar and property listeners of all calendars in the database.
     */
    private void listenToAllCalendars() {
	for (Calendar calendar : CalendarDatabase.getInstance().getCalendars()) {
	    calendar.addCalendarListener(this);
	    calendar.addPropertyListener(this);
	}
    }

    private void createNavigationPanel(){
	navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	JButton todayButton = new JButton("Idag");
	JButton nextButton = new JButton(RIGHT_ARROW);
	previousButton = new JButton(LEFT_ARROW);
	navigationPanel.add(previousButton);
	navigationPanel.add(todayButton);
	navigationPanel.add(nextButton);
	previousButton.setPreferredSize(new Dimension(PREVIOUS_NEXT_BUTTON_WIDTH, previousButton.getPreferredSize().height));
	nextButton.setPreferredSize(new Dimension(PREVIOUS_NEXT_BUTTON_WIDTH, nextButton.getPreferredSize().height));
	navigationPanel.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, NAVIGATION_PANEL_HEIGHT));
	nextButton.addActionListener(new GoToNextAction());
	previousButton.addActionListener(new GoToPreviousAction());
	addTodayButtonListener(todayButton);
    }

    /**
     * Gets a list of all <code>StandardAppointments</code> in all calendars on a given date.
     */
    protected List<StandardAppointment> getAllEnabledStandardAppointments(Date date){
	List<StandardAppointment> result = new ArrayList<>();
	for (Calendar calendar : CalendarDatabase.getInstance().getEnabledCalendars()){
	    for (StandardAppointment appointment : calendar.getStandardAppointments(date)){
		result.add(appointment);
	    }
	}
	Collections.sort(result, new AppointmentComparator());
	return result;
    }

    protected void notifyListeners(){
	for (ViewListener listener : listeners) {
	    listener.appointmentSelectedOrDeselected();
	}
    }


    /**
     * Comparator class that compares StandardAppointment by their duration
     */
    private class AppointmentComparator implements Comparator<StandardAppointment>
	{
	@Override public int compare(final StandardAppointment o1, final StandardAppointment o2) {

	    double duration1 = o1.getDuration().getDurationInHours();
	    double duration2 = o2.getDuration().getDurationInHours();

	    if (duration1 < duration2){
		return 1;
	    } else if (duration1 > duration2){
		return -1;
	    }
	    return 0;
	}
    }

    /**
     * Comparator class that compares StandardAppRectangles by their duration.
     */
    public class AppointmentIllustrationComparator implements Comparator<AppointmentIllustration>
    { //is public since the views need to access it from different packages, and protected does not work
	@Override public int compare(final AppointmentIllustration o1, final AppointmentIllustration o2) {

	    StandardAppointment appointment1 = (StandardAppointment) o1.getAppointment();
	    StandardAppointment appointment2 = (StandardAppointment) o2.getAppointment();

	    double duration1 = appointment1.getDuration().getDurationInHours();
	    double duration2 = appointment2.getDuration().getDurationInHours();

	    if (duration1 < duration2){
		return 1;
	    } else if (duration1 > duration2){
		return -1;
	    }
	    return 0;
	}
    }

    /**
     * If a new calendar is added to the database, the view needs to add itself to all the calendars again.
     */
    @Override public void databaseChanged(){
	listenToAllCalendars();
	reloadAppointments();
    }

    /**
     * Invoked when a calendar is changed.
     */
    @Override public void calendarContentChangedOrEnabledToggled() {
	reloadAppointments();
    }

    protected void addTodayButtonListener(final JButton todayButton) {
	ActionListener listener = new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		goToToday();
	    }
	};
	todayButton.addActionListener(listener);
    }

    /**
     * Checks if the date in the <code>setDate</code> method is January 1:st 2007.
     * If so, the previousButton is disabled so the user does not pass this point.
     * Otherwise, it enables the button. Each implementation of the <code>setDate</code>
     * method must use this method.
     */
    protected void setCorrectPreviousButtonState(final Date date) {
	if (date.equals(Date.EARLIEST_ALLOWED_DATE)) {
	    previousButton.setEnabled(false);
	} else {
	    previousButton.setEnabled(true);
	}
    }

    protected abstract void deselectAllApps();

    @Override public void inputNewAppointment() {
	inputNewAppointment(getAppropriateDate(), false);
    }

    private void inputNewAppointment(Date date, boolean wholeDay){
	if (!CalendarDatabase.getInstance().isEmpty()) { //make sure that there are calendars in the database
	    AppointmentDialog appd = new AppointmentDialog();
	    appd.setDate(date);
	    appd.setWholeDayBox(wholeDay);
	    appd.inputAppointment(AppointmentDialog.NEW);
	    reloadAppointments();
	}
    }

    /**
     * Gets the appropriate date based on the <code>ViewComponent</code> that was clicked.
     * This is to set the correct date in the <code>AppointmentDialog</code> when a <code>ViewComponent</code>
     * double clicked.
     * For instance, if a <code>ViewComponent</code> in a month view is clicked, it should
     * return a date representing the day that was clicked.
     * @param component The component that was clicked.
     */
    protected abstract Date getAppropriateDate(ViewComponent component);

    /**
     * Returns the date the <code>View</code> wants to be set in the <code>AppointmentDialog</code>
     * when an outside caller wants to input an appointment, for example the <code>MainFrame</code>
     * through the menus.
     */
    protected abstract Date getAppropriateDate();

    @Override public void addViewListener(final ViewListener listener) {
	listeners.add(listener);
    }

    @Override public void deleteSelectedAppointment() {
	Appointment selectedApp = getSelectedAppointment();
	if (selectedApp != null) {
	    Calendar calendar = Calendar.getAssociatedCalendar(selectedApp);
	    assert calendar != null : "Internal error, calendar is null";
	    calendar.removeAppointment(selectedApp);
	}
	notifyListeners();
    }

    private class GoToNextAction extends AbstractAction{
	@Override public void actionPerformed(final ActionEvent e) {
	    goToNext();
	}
    }

    private class GoToPreviousAction extends AbstractAction{
	@Override public void actionPerformed(final ActionEvent e) {
	    goToPrevious();
	}
    }

    @Override public void calendarPropertyChanged() {
	reloadAppointments();
    }

    @Override public void appointmentClicked(final AppointmentIllustration source) {
	deselectAllApps();
	source.setSelected(true);
	notifyListeners();
    }

    @Override public void appointmentDoubleClicked(final AppointmentIllustration source) {
	AppointmentDialog dialog = new AppointmentDialog();
	Appointment appointment = source.getAppointment();
	dialog.setInitialParameters(appointment);
	dialog.inputAppointment(AppointmentDialog.EDIT);
	Calendar calendar = Calendar.getAssociatedCalendar(appointment);
	assert calendar != null: "Internal error: calendar is null";
	calendar.removeAppointment(appointment);// The calendar can never be null, since an appointment always has
		    					// an associated calendar, and these methods are only called when there
		    					// are appointments in the database, since they are activated when someone
		    					// double-clicks on one of them.
    }

    @Override public void viewComponentClicked() {
	deselectAllApps();
	notifyListeners();
    }

    @Override public void viewComponentDoubleClicked(final ViewComponent source) {
	inputNewAppointment(getAppropriateDate(source), source.getPreferredWholeDayState());
    }

    @Override public Appointment getSelectedAppointment() {
	for (ViewComponent component : viewComponents) {
	    Appointment app = component.getSelectedAppointment();
	    if (app != null) return app;
	}
	return null;
    }
}
