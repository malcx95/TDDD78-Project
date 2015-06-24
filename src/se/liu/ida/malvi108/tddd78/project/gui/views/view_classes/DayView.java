package se.liu.ida.malvi108.tddd78.project.gui.views.view_classes;

import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.appointments.Calendar;
import se.liu.ida.malvi108.tddd78.project.appointments.StandardAppointment;
import se.liu.ida.malvi108.tddd78.project.appointments.WholeDayAppointment;
import se.liu.ida.malvi108.tddd78.project.databases.CalendarDatabase;
import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;
import se.liu.ida.malvi108.tddd78.project.gui.dialogs.RemoveDialog;
import se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations.AppointmentIllustration;
import se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations.StandardAppRectangle;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_components.ViewComponent;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_components.StandardAppViewComponent;
import se.liu.ida.malvi108.tddd78.project.gui.views.todo_list.ToDoListPane;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_components.WholeDayAppComponent;
import se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations.WholeDayAppRectangle;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;
import se.liu.ida.malvi108.tddd78.project.time.TimeSpan;
import se.liu.ida.malvi108.tddd78.project.time.Year;
import se.liu.ida.malvi108.tddd78.project.todo_list.ToDoList;
import se.liu.ida.malvi108.tddd78.project.databases.ToDoListDatabase;
import se.liu.ida.malvi108.tddd78.project.todo_list.ToDoListEntry;

import javax.swing.*;

import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * View that displays individual days. Contains a <code>StandardAppViewComponent</code> and a
 * <code>WholeDayAppComponent</code>, as well as a <code>ToDoListPane</code> and <code>AppointmentInfoPanel</code>.
 */
public class DayView extends AbstractView
{
    private final static int WHOLE_DAY_LABEL_SIZE = 11;
    private final static int APP_INFO_HEIGHT = 250;
    private final static int BUTTON_PANEL_HEIGHT = 70;
    private final static double DAY_LABEL_WEIGHT = 0.5;
    private Date date;
    private StandardAppViewComponent stdAppComponent;
    private WholeDayAppComponent wholeDayAppComponent;
    private JLabel dateLabel;
    private JLabel dayLabel;
    private final static String LABEL_FONT = "Helvetica";
    private final static int DAY_FONT_SIZE = 15;
    private AppointmentInfoPanel appointmentInfo;
    private ToDoListPane toDoListPane;
    /**
     * The listener that listens to the mouse entering
     * ViewComponents or AppointmentRectangles.
     */
    private final MouseListener enterListener = new MouseEnterListener();
    private JButton removeToDoListEntriesButton;

    public DayView(Date date){
	setLayout(new BorderLayout());
	this.date = date;
	addViewPanel();
	addSidePanel();
	addViewComponentsToList();
	setCorrectToDoListRemoveState();
    }

    private void addSidePanel() {
	JPanel sidePanel = new JPanel(new BorderLayout());
	sidePanel.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, getHeight()));
	addAppointmentInfoPanel(sidePanel);
	addToDoListPanel(sidePanel);
	sidePanel.add(navigationPanel, BorderLayout.NORTH);
	add(sidePanel, BorderLayout.EAST);
    }

    private void addToDoListPanel(final JPanel sidePanel) {
	JPanel toDoListPanel = new JPanel(new BorderLayout());
	toDoListPanel.setBorder(new TitledBorder(new SoftBevelBorder(BevelBorder.LOWERED), "Att göra idag"));

	toDoListPane = new ToDoListPane(date);
	JScrollPane scrollPane = new JScrollPane(toDoListPane);
	scrollPane.setBorder(null);
	toDoListPanel.add(scrollPane, BorderLayout.CENTER);

	JPanel buttonPanel = new JPanel();
	JButton newEntry = new JButton("Lägg till...");
	addNewEntryListener(newEntry);
	removeToDoListEntriesButton = new JButton("Ta bort...");
	addRemoveEntriesListener(removeToDoListEntriesButton);
	buttonPanel.add(newEntry);
	buttonPanel.add(removeToDoListEntriesButton);
	buttonPanel.setPreferredSize(new Dimension(buttonPanel.getPreferredSize().width, BUTTON_PANEL_HEIGHT));
	toDoListPanel.add(buttonPanel, BorderLayout.SOUTH);

	sidePanel.add(toDoListPanel, BorderLayout.CENTER);
    }

    private void addRemoveEntriesListener(final JButton removeEntries) {
	ActionListener listener = new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		ToDoList todaysList = ToDoListDatabase.getInstance().get(date);
		assert todaysList != null : "Internal error, no ToDoList for today";
		ToDoListEntry[] entries = todaysList.getEntriesArray();
		RemoveDialog<ToDoListEntry> dialog = new RemoveDialog<>(entries);
		List<ToDoListEntry> entriesToRemove = dialog.showRemoveDialog();
		if (entriesToRemove != null) { //if the user didn't click cancel
		    todaysList.removeEntries(entriesToRemove);
		}
		setCorrectToDoListRemoveState();
	    }
	};
	removeEntries.addActionListener(listener);
    }

    private void addNewEntryListener(final JButton newEntry) {
	ActionListener listener = new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		ToDoListDatabase database = ToDoListDatabase.getInstance();
		String name = (String) JOptionPane.showInputDialog(null, "Vad måste du göra?", "Ny \"att-göra\"",
							  JOptionPane.QUESTION_MESSAGE, MainFrame.PLAN_IT_ICON, null, null);
		if (name != null) {
		    ToDoList todaysList = database.get(date);
		    assert todaysList != null: "Internal error, todaysList is null"; //it should never be null, since the ToDoListPane always creates a ToDoList for the day
		    todaysList.addEntry(new ToDoListEntry(name));
		}
		setCorrectToDoListRemoveState();
	    }
	};
	newEntry.addActionListener(listener);
    }

    private void addAppointmentInfoPanel(final JPanel sidePanel) {
	appointmentInfo = new AppointmentInfoPanel();
	appointmentInfo.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, APP_INFO_HEIGHT));
	sidePanel.add(appointmentInfo, BorderLayout.SOUTH);
    }

    private void addViewPanel(){
	JPanel viewPanel = new JPanel(new BorderLayout());
	stdAppComponent = new StandardAppViewComponent();
	stdAppComponent.addViewComponentListener(this);
	stdAppComponent.addMouseListener(enterListener);
	viewPanel.add(new JScrollPane(stdAppComponent), BorderLayout.CENTER);
	this.add(viewPanel, BorderLayout.CENTER);
	addStandardAppointments();
	addUpperPanel(viewPanel);
    }

    private void addUpperPanel(JPanel viewPanel) {
	JPanel upperPanel = new JPanel(new GridBagLayout());
	Insets margin = new Insets(0, 5, 0, 0);

	addDateLabel(upperPanel, margin);
	addDayLabel(upperPanel, margin);
	addWholeDayLabel(upperPanel, margin);
	addWholeDayAppComponent(upperPanel, margin);
	viewPanel.add(upperPanel, BorderLayout.NORTH);
    }

    private void addWholeDayAppComponent(final JPanel upperPanel, final Insets margin) {
	wholeDayAppComponent = new WholeDayAppComponent();
	wholeDayAppComponent.addViewComponentListener(this);
	wholeDayAppComponent.addMouseListener(enterListener);
	wholeDayAppComponent.setLayout(new BoxLayout(wholeDayAppComponent, BoxLayout.PAGE_AXIS));
	JScrollPane wholeDayPane = new JScrollPane(wholeDayAppComponent);
	wholeDayPane.setPreferredSize(new Dimension(wholeDayAppComponent.getPreferredSize().width, WholeDayAppComponent.APP_HEIGHT + 4));
	GridBagConstraints componentConstraints = new GridBagConstraints();
	componentConstraints.fill = GridBagConstraints.HORIZONTAL;
	componentConstraints.weightx = 0.0;
	componentConstraints.gridwidth = 2;
	componentConstraints.gridx = 1;
	componentConstraints.gridy = 2;
	componentConstraints.ipady = WholeDayAppComponent.APP_HEIGHT;
	componentConstraints.insets = margin;
	componentConstraints.anchor = GridBagConstraints.LINE_START;
	upperPanel.add(wholeDayPane, componentConstraints);
	addWholeDayAppointments();
    }

    private void addWholeDayAppointments() {
	wholeDayAppComponent.removeAll();
	for (Calendar calendar : CalendarDatabase.getInstance().getEnabledCalendars()) {
	    for (WholeDayAppointment appointment : calendar.getWholeDayAppointments(date)) {
		WholeDayAppRectangle rect = new WholeDayAppRectangle(appointment, calendar.getColor());
		wholeDayAppComponent.add(rect);
		rect.setAlignmentX(Component.LEFT_ALIGNMENT);
		rect.addAppIllustrationListener(this);
		rect.addMouseListener(enterListener);
	    }
	}
    }

    private void addWholeDayLabel(final JPanel upperPanel, final Insets margin) {
	JLabel wholeDayLabel = new JLabel("Heldag");
	wholeDayLabel.setFont(new Font(MainFrame.GLOBAL_FONT, Font.PLAIN, WHOLE_DAY_LABEL_SIZE));
	GridBagConstraints wholeDayConstraints = new GridBagConstraints();
	wholeDayConstraints.weightx = 0.0;
	wholeDayConstraints.gridwidth = 1;
	wholeDayConstraints.gridx = 0;
	wholeDayConstraints.gridy = 2;
	wholeDayConstraints.ipady = 10;
	wholeDayConstraints.insets = margin;
	wholeDayConstraints.anchor = GridBagConstraints.LINE_START;
	upperPanel.add(wholeDayLabel, wholeDayConstraints);
    }

    private void addDayLabel(final JPanel upperPanel, final Insets margin) {
	dayLabel = new JLabel(date.getWeekday() + ", vecka " + date.getWeekNumber());
	dayLabel.setFont(new Font(LABEL_FONT, Font.PLAIN , DAY_FONT_SIZE));
	GridBagConstraints dayConstraints = new GridBagConstraints();
	dayConstraints.weightx = DAY_LABEL_WEIGHT;
	dayConstraints.gridwidth = 3;
	dayConstraints.gridx = 0;
	dayConstraints.gridy = 1;
	dayConstraints.ipady = 10;
	dayConstraints.insets = margin;
	dayConstraints.anchor = GridBagConstraints.LINE_START;
	upperPanel.add(dayLabel, dayConstraints);
    }

    private void addDateLabel(final JPanel upperPanel, final Insets margin) {
	dateLabel = new JLabel(date.toString());
	dateLabel.setFont(new Font(LABEL_FONT, Font.PLAIN, DATE_FONT_SIZE));
	GridBagConstraints dateConstraints = new GridBagConstraints();
	dateConstraints.weightx = 0.0;
	dateConstraints.ipady = 10;
	dateConstraints.insets = margin;
	dateConstraints.gridwidth = 3;
	dateConstraints.gridx = 0;
	dateConstraints.gridy = 0;
	dateConstraints.anchor = GridBagConstraints.LINE_START;
	upperPanel.add(dateLabel, dateConstraints);
    }

    /**
     * Adds the StandardAppRectangles to the view and sets their inital sizes and positions.
     * The sizes and positions are adjusted by the adjustAppointments method
     * in the DayViewComponent class, using the placement order and overlapping numbers
     * assigned by this method.
     */
    private void addStandardAppointments(){
	stdAppComponent.removeAll(); // the easiest way to update is to remove all and re-add them.
	for (Calendar calendar : CalendarDatabase.getInstance().getEnabledCalendars()) {
	    for (StandardAppointment appointment : calendar.getStandardAppointments(date)) {
		double appPosition = appointment.getDuration().getStartTimeInHours() * StandardAppViewComponent.HOUR_SIZE;
		StandardAppRectangle rect = new StandardAppRectangle(appointment, calendar.getColor());
		stdAppComponent.add(rect);
		rect.addAppIllustrationListener(this);
		rect.addMouseListener(enterListener);
		rect.setLocation(StandardAppViewComponent.LEFT_LINE_MARGIN, (int) appPosition);
	    }
	}
	assignOverlappingNumbers();
	assignPlacementOrders();
    }

    /**
     * Assigns appropriate placement orders for all rectangles in the view. Also corrects
     * their overlapping numbers if they are incorrect.
     */
    private void assignPlacementOrders() {
	//First, it gets all rectangles of the day.
	List<StandardAppRectangle> allRectangles = getAllRectangles();
	//Now it creates a list of checked rectangles, where already checked rectangles are stored.
	Collection<StandardAppRectangle> checkedRectangles = new ArrayList<>();
	for (StandardAppRectangle rectangle: allRectangles) { //For each rectangle in the view...
	    //Get a list of rectangles that overlap with the current appointment.
	    List<StandardAppRectangle> overlappingRectangles = getOverlappingRectangles(checkedRectangles, rectangle);
	    if (checkedRectangles.isEmpty() || overlappingRectangles.isEmpty()){ /* If either no rectangles have been checked or
	    									or no rectangles overlap...*/
		//Assign the placement order 0 to the rectangle, since no other rectangles overlap.
		rectangle.setPlacementOrder(0);
	    }
	    else { //If there are overlapping rectangles...
		//get the highest placament order
		int highestPlacementOrder = getHighestPlacementOrder(overlappingRectangles);
		if (highestPlacementOrder == rectangle.getOverlapping()){
		    /*
		    This is a special scenario where the highest placement order is equal to the number of
		    rectangles it overlaps with. This means the overlapping number of the current rectangle needs
		    to be set to the overlapping number of the rectangle with the highest placement order, and we need
		    to find a vacant placementorder among the rectangles which it overlaps with.
		     */
		    StandardAppRectangle rectWithHighestPO = getRectWithHighestPlacementOrder(overlappingRectangles);
		    rectangle.setPlacementOrder(findVacantPlacementOrder(overlappingRectangles, highestPlacementOrder));
		    rectangle.setOverlapping(rectWithHighestPO.getOverlapping());
		} else {
		    //Assign to the rectangles the placement order of the rectangle with the highest placement order
		    //among the overlapping rectangles + 1.
		    rectangle.setPlacementOrder(1 + highestPlacementOrder);
		}
	    }
	    //Add the checked rectangle to the list of checked rectangles.
	    checkedRectangles.add(rectangle);
	}
    }

    private int findVacantPlacementOrder(final Iterable<StandardAppRectangle> overlappingRectangles, final int highestPlacementOrder)
    {
	for (int order = highestPlacementOrder - 1; order > 0; order--) {
	    if (isVacant(overlappingRectangles, order)){
		return order;
	    }
	}
	return 0;
    }

    private boolean isVacant(final Iterable<StandardAppRectangle> overlappingRectangles, final int order) {
	for (StandardAppRectangle rectangle : overlappingRectangles) {
	    if (rectangle.getPlacementOrder() == order) return false;
	}
	return true;
    }

    /**
     * Gets all the StandardAppRectangles in the viewComponent and returns a sorted list of them.
     */
    private List<StandardAppRectangle> getAllRectangles(){
	Component[] allRectangles = stdAppComponent.getComponents();
	List<StandardAppRectangle> result = new ArrayList<>();
	for (Component rectangle : allRectangles) {
	    result.add((StandardAppRectangle) rectangle);
	}
	Collections.sort(result, new AppointmentIllustrationComparator());
	return result;
    }

    /**
     * Gets the highest placement order from a list of StandardAppRectangles.
     */
    private int getHighestPlacementOrder(Iterable<StandardAppRectangle> rectangles) {
	int result = 0;
	for (StandardAppRectangle rectangle: rectangles) {
	    int temp = rectangle.getPlacementOrder();
	    if (temp > result) result = temp;
	}
	return result;
    }

    /**
     * Gets the highest placement order from a list of StandardAppRectangles.
     */
    private StandardAppRectangle getRectWithHighestPlacementOrder(List<StandardAppRectangle> rectangles) {
	StandardAppRectangle result = rectangles.get(0);
	for (StandardAppRectangle rectangle: rectangles) {
	    if (rectangle.getPlacementOrder() > result.getPlacementOrder()){
		result = rectangle;
	    }
	}
	return result;
    }

    /**
     * Gets a list of StandardAppRectangles that overlap with the given rectangle.
     */
    private List<StandardAppRectangle> getOverlappingRectangles(Iterable<StandardAppRectangle> rectangles,
								StandardAppRectangle comparison){
	List<StandardAppRectangle> result = new ArrayList<>();
	StandardAppointment compApp = (StandardAppointment) comparison.getAppointment();
	for (StandardAppRectangle rectangle : rectangles) {
	    StandardAppointment app = (StandardAppointment) rectangle.getAppointment();
	    if (app.getDuration().overlapsWith(compApp.getDuration())) result.add(rectangle);
	}
	result.remove(comparison);
	return result;
    }

    private void assignOverlappingNumbers(){
	List<StandardAppRectangle> allRectangles = getAllRectangles();
	for (StandardAppRectangle rectangle : allRectangles){
	    assignOverlapping(rectangle);
	}
    }

    private void assignOverlapping(StandardAppRectangle rectangle){
	List<StandardAppointment> allApps = getAllEnabledStandardAppointments(date);
	StandardAppointment appointment = (StandardAppointment) rectangle.getAppointment();
	TimeSpan ts = appointment.getDuration();
	int overlapping = getNumberOfMaximumOverlappings(allApps, ts);
	rectangle.setOverlapping(overlapping);
    }

    /**
     * Gets the maximum number of overlappings for an appointment.
     * @param appointments A set of overlapping appointments
     * @param timeSpan A TimeSpan in which they overlap
     */
    private int getNumberOfMaximumOverlappings(Iterable<StandardAppointment> appointments, TimeSpan timeSpan){
	int start = timeSpan.getStartTimeInMinutes();
	int end = timeSpan.getEndTimeInMinutes();
	int highestOverlappingNumber = 0;
	for (int minute = start; minute < end; minute++) { //checks all minutes within the timespan
	    TimePoint time = TimePoint.minutesToTimePoint(minute);
	    int temp = getOverlappingNumberForTimePoint(time, appointments);
	    if (temp > highestOverlappingNumber) highestOverlappingNumber = temp;
	}
	return highestOverlappingNumber;
    }

    /**
     * Gets the number of overlapping appointments in the given TimePoint.
     */
    private int getOverlappingNumberForTimePoint(TimePoint time, Iterable<StandardAppointment> appointments){
	int overlappingNumber = -1; //so it doesn't count itself
	for (StandardAppointment appointment : appointments) {
	    TimeSpan duration = appointment.getDuration();
	    if (duration.contains(time)){
		overlappingNumber++;
	    }
	}
	return overlappingNumber;
    }

    @Override public void setDate(Date date) {
	if (date.getYear() < Year.STARTING_YEAR){
	    throw new IllegalArgumentException("Date precedes the starting year!");
	}
	this.date = date;
	dateLabel.setText(date.toString());
	dayLabel.setText(date.getWeekday() + ", vecka " + date.getWeekNumber());
	reloadAppointments();
	toDoListPane.setDate(date);
	setCorrectPreviousButtonState(date);
	setCorrectToDoListRemoveState();
    }

    private void setCorrectToDoListRemoveState() {
	ToDoList todaysList = ToDoListDatabase.getInstance().get(date);
	assert todaysList != null: "Internal error, todaysList is null";
	if (todaysList.isEmpty()){
	    removeToDoListEntriesButton.setEnabled(false);
	} else {
	    removeToDoListEntriesButton.setEnabled(true);
	}
    }

    @Override public void goToNext() {
	setDate(date.getNextDay());
    }

    @Override public void goToPrevious() {
	Date previous = date.getPreviousDay();
	if (previous.getYear() >= Year.STARTING_YEAR){
	    setDate(date.getPreviousDay());
	}
    }

    @Override public void goToToday() {
	setDate(Date.getToday());
    }

    @Override protected void deselectAllApps(){
	deselectAllStandardApps();
	deselectAllWholeDayApps();
    }

    @Override protected Date getAppropriateDate(ViewComponent component) {
	return date;
    }

    @Override protected Date getAppropriateDate() {
	return date; //in this case, the field date IS the appropriate date
    }

    private void deselectAllStandardApps() {
	for (Component rect : stdAppComponent.getComponents()){
	    StandardAppRectangle app = (StandardAppRectangle) rect;
	    app.setSelected(false);
	}
    }

    private void deselectAllWholeDayApps() {
	for (Component rect : wholeDayAppComponent.getComponents()){
	    WholeDayAppRectangle app = (WholeDayAppRectangle) rect;
	    app.setSelected(false);
	}
    }

    private class MouseEnterListener extends MouseInputAdapter{
	/**
	 * Invoked when the mouse enters a component.
	 *
	 */
	@Override public void mouseEntered(final MouseEvent e) {
	    super.mouseEntered(e);
	    Object source = e.getSource();
	    if (source instanceof AppointmentIllustration){
  		AppointmentIllustration app = (AppointmentIllustration) source;
		appointmentInfo.setAppointmentInfo(app.getAppointment());
	    } else {
  		appointmentInfo.setAppointmentInfo(null);
	    }
	}
    }

    /**
     * Adds the <code>ViewComponents</code> to the <code>viewComponents</code> list.
     */
    protected void addViewComponentsToList() {
	viewComponents.add(stdAppComponent);
	viewComponents.add(wholeDayAppComponent);
    }

    @Override public void reloadAppointments(){
	addStandardAppointments();
	addWholeDayAppointments();
	stdAppComponent.adjustAppointments();
	wholeDayAppComponent.revalidate();
	stdAppComponent.revalidate();
	wholeDayAppComponent.repaint();
	stdAppComponent.repaint();
	addViewComponentsToList();
    }

    private final class AppointmentInfoPanel extends JPanel{

	private final static int MARGIN = 10;
	private final static int SMALL_FONT_SIZE = 12;
	private final static int LARGE_FONT_SIZE = 14;
	private JLabel time;
	private JTextArea subject;
	private JLabel location;
	private JTextArea description;

	private AppointmentInfoPanel(){
	    super(new FlowLayout(FlowLayout.LEFT));
	    setBackground(Color.WHITE);
	    Font smallFont = new Font(MainFrame.GLOBAL_FONT, Font.PLAIN, SMALL_FONT_SIZE);
	    Font italic = new Font(MainFrame.GLOBAL_FONT, Font.ITALIC, SMALL_FONT_SIZE);
	    Font largeFont = new Font(MainFrame.GLOBAL_FONT, Font.BOLD, LARGE_FONT_SIZE);
	    time = new JLabel();
	    time.setFont(smallFont);
	    subject = new JTextArea();
	    subject.setFont(largeFont);
	    subject.setLineWrap(true);
	    subject.setEditable(false);
	    subject.setSize(new Dimension(SIDE_PANEL_WIDTH - MARGIN, subject.getPreferredSize().height));
	    location = new JLabel();
	    location.setFont(italic);
	    description = new JTextArea();
	    description.setEditable(false);
	    description.setFont(smallFont);
	    description.setLineWrap(true);
	    description.setSize(new Dimension(SIDE_PANEL_WIDTH - MARGIN, description.getPreferredSize().height));
	    add(time);
	    add(subject);
	    add(location);
	    add(description);
	}

	/**
  	* Displays the given appointment on the info-panel. If <code>null</code> is
  	* sent as a parameter, the info-panel displays nothing.
  	*/
	private void setAppointmentInfo(Appointment app){
	    if (app != null) {
		subject.setText(app.getSubject());
		Calendar associatedCalendar = Calendar.getAssociatedCalendar(app);
		assert associatedCalendar != null: "Internal error, calendar is null";
		subject.setForeground(associatedCalendar.getColor().darker());
		location.setText(app.getLocation());
		description.setText(app.getDescription());
		if (app instanceof StandardAppointment){
		    time.setText(((StandardAppointment) app).getDuration().toString());
		} else {
		    time.setText("Hela dagen");
		}
	    } else {
		subject.setText(null);
		location.setText(null);
		time.setText(null);
		description.setText(null);
	    }
 	}
    }
}