package se.liu.ida.malvi108.tddd78.project.gui.views.view_classes;

import java.awt.*;
import se.liu.ida.malvi108.tddd78.project.appointments.Calendar;
import se.liu.ida.malvi108.tddd78.project.databases.CalendarDatabase;
import se.liu.ida.malvi108.tddd78.project.appointments.StandardAppointment;
import se.liu.ida.malvi108.tddd78.project.appointments.WholeDayAppointment;
import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;
import se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations.MonthAppIllustration;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_components.MonthCellType;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_components.MonthViewCell;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_components.ViewComponent;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.Day;
import se.liu.ida.malvi108.tddd78.project.time.Month;
import se.liu.ida.malvi108.tddd78.project.time.MonthName;
import se.liu.ida.malvi108.tddd78.project.time.WeekDay;
import se.liu.ida.malvi108.tddd78.project.time.Year;
import javax.swing.*;

/**
 * A view where you can see all your appointments in a particular month.
 */
public class MonthView extends AbstractView
{
    private Month month;
    private int year;
    private final static int NUMBER_OF_CELLS = 42;
    private final static int WEEK_DAY_FONT_SIZE = 14;
    private JPanel viewPanel;
    private JLabel dateLabel;

    public MonthView(Date date){
	setLayout(new BorderLayout());
	year = date.getYear();
	month = Month.getMonth(date.getMonth(), year);
	addUpperPanel();
	addView();
	addAppointments();
	addViewComponentsToList();
    }

    private void addUpperPanel() {
	JPanel upperPanel = new JPanel(new BorderLayout());
	dateLabel = new JLabel();
	dateLabel.setText(month.getName() + ", " + Integer.toString(year));
	dateLabel.setFont(new Font(MainFrame.GLOBAL_FONT, Font.PLAIN, DATE_FONT_SIZE));
	upperPanel.add(dateLabel, BorderLayout.WEST);
	upperPanel.add(navigationPanel, BorderLayout.EAST);
	navigationPanel.setAlignmentY(SwingConstants.CENTER);
	addWeekdayPanel(upperPanel);
	add(upperPanel, BorderLayout.NORTH);
    }

    private void addWeekdayPanel(final JPanel upperPanel) {
	JPanel weekdayPanel = new JPanel(new GridLayout(0, 7));
	WeekDay[] weekDays = WeekDay.values();
	for (WeekDay weekDay : weekDays) {
	    JLabel weekDayLabel = new JLabel(weekDay.toString());
	    weekDayLabel.setFont(new Font(MainFrame.GLOBAL_FONT, Font.PLAIN, WEEK_DAY_FONT_SIZE));
	    weekdayPanel.add(weekDayLabel);
	}
	upperPanel.add(weekdayPanel, BorderLayout.SOUTH);
    }

    private void addAppointments() {
	CalendarDatabase database = CalendarDatabase.getInstance();
	for (Component component : viewPanel.getComponents()) {
	    MonthViewCell cell = (MonthViewCell) component;
	    if (cell.getType().equals(MonthCellType.THIS_MONTH)){
		addThisDaysAppointments(database, cell);
	    }
	}
    }

    private void addThisDaysAppointments(final CalendarDatabase database, MonthViewCell cell) {
	cell.removeAllAppointments();
	Date today = new Date(cell.getDayNumber(), month.getMonthNumber(), year);
	for (Calendar calendar : database.getEnabledCalendars()) {
	    for (WholeDayAppointment appointment : calendar.getWholeDayAppointments(today)) {
		MonthAppIllustration app = new MonthAppIllustration(appointment, calendar.getColor());
		cell.addAppointment(app);
		app.addAppIllustrationListener(this);
	    }
	    for (StandardAppointment appointment : calendar.getStandardAppointments(today)) {
		MonthAppIllustration app = new MonthAppIllustration(appointment, calendar.getColor());
		cell.addAppointment(app);
		app.addAppIllustrationListener(this);
	    }
	}
	cell.sort();
    }

    private void addView() {
	viewPanel = new JPanel(new GridLayout(6, 7));
	addDays();
	add(viewPanel, BorderLayout.CENTER);
    }

    private void addDays() {
	viewPanel.removeAll();
	WeekDay firstDay = month.getFirstDay().getWeekDay();
	int addedCells = 0;
	if (!(year == Year.STARTING_YEAR && month.getName().equals(MonthName.JANUARY))){
	    addedCells = addPreviousMonthDays(firstDay, addedCells);
	}
	addedCells = addThisMonthDays(addedCells);
	addNextMonthDays(addedCells);
    }

    /**
     * Adds the days of the next month.
     */
    private void addNextMonthDays(final int addedCells) {
	int day = 1;
	int monthNumber;
	Year correctYear;
	if (month.getName().equals(MonthName.DECEMBER)){
	    monthNumber = 1;
	    correctYear = new Year(year + 1);
	} else {
	    monthNumber = month.getMonthNumber() + 1;
	    correctYear = new Year(year);
	}
	for (int i = addedCells; i < NUMBER_OF_CELLS; i++) {
	    MonthViewCell cell = new MonthViewCell(correctYear.getDay(monthNumber, day), MonthCellType.OUTSIDE, false);
	    viewPanel.add(cell);
	    cell.addViewComponentListener(this);
	    day++;
	}
    }

    /**
     * Adds the days of this month to the view. Returns the number of days
     * that was added.
     */
    private int addThisMonthDays(int addedCells) {
	for (Day day : month.getDays()) {
	    int number = day.getNumber();
	    Date date = new Date(number, month.getMonthNumber(), year);
	    MonthViewCell cell;
	    if (date.equals(Date.getToday())){
		cell = new MonthViewCell(day, MonthCellType.THIS_MONTH, true);
	    } else {
		cell = new MonthViewCell(day, MonthCellType.THIS_MONTH, false);
	    }
	    viewPanel.add(cell);
	    cell.addViewComponentListener(this);
	    addedCells++;
	}
	return addedCells;
    }

    /**
     * Adds the days of the previous month to the view. Returns the number of
     * days that was added.
     */
    private int addPreviousMonthDays(final WeekDay firstDay, int addedCells) {
	int monthNumber;
	Year correctYear;
	if (month.getName().equals(MonthName.JANUARY)){
	    monthNumber = Month.DECEMBER_NUMBER;
	    correctYear = new Year(year - 1);
	} else {
	    monthNumber = month.getMonthNumber() - 1;
	    correctYear = new Year(year);
	}
	if (!firstDay.equals(WeekDay.MONDAY)){
	    Month previous = Month.getPreviousMonth(month.getMonthNumber(), year);
	    //the number of days of the previous month
	    int previousDays = previous.getNumberOfDays();
	    //the number of the monday from the previous month
	    int startDay = previousDays - WeekDay.getWeekDayNumber(firstDay) + 2;
	    for (int i = startDay; i <= previousDays; i++) {
		MonthViewCell cell = new MonthViewCell(correctYear.getDay(monthNumber, i),
						       MonthCellType.OUTSIDE, false);
		cell.addViewComponentListener(this);
		viewPanel.add(cell);
		addedCells++;
	    }
	}
	return addedCells;
    }


    @Override public void goToNext() {
	int monthNumber = month.getMonthNumber();
	int year;
	int nextMonth;
	if (monthNumber == Month.DECEMBER_NUMBER){
	    year = this.year + 1;
	    nextMonth = 1;
	} else {
	    year = this.year;
	    nextMonth = monthNumber + 1;
	}
	setDate(new Date(1, nextMonth, year)); //the day number does not matter
    }

    @Override public void goToPrevious() {
	int monthNumber = month.getMonthNumber();
	int year;
	int previousMonth;
	if (monthNumber == 1){
	    year = this.year - 1;
	    previousMonth = Month.DECEMBER_NUMBER;
	} else {
	    year = this.year;
	    previousMonth = monthNumber - 1;
	}
	if (year >= Year.STARTING_YEAR){
	    setDate(new Date(1, previousMonth, year)); //the day number does not matter
	}
    }

    @Override public void goToToday() {
	setDate(Date.getToday());
    }

    @Override public void setDate(final Date date) {
	year = date.getYear();
	assert year >= Year.STARTING_YEAR: "Error: date precedes + " + Integer.toString(Year.STARTING_YEAR);
	month = Month.getMonth(date.getMonth(), year);
	dateLabel.setText(month.getName() + ", " + Integer.toString(year));
	reloadDays();
	reloadAppointments();
	setCorrectPreviousButtonState(date);
    }

    @Override protected void deselectAllApps() {
	for (Component component : viewPanel.getComponents()) {
	    MonthViewCell cell = (MonthViewCell) component;
	    cell.deselectAll();
	}
    }

    /**
     * Returns the date the clicked <code>MonthViewCell</code> represents.
     * @param component The component that was clicked.
     */
    @Override protected Date getAppropriateDate(ViewComponent component) {
	MonthViewCell cell = (MonthViewCell) component;
	return new Date(cell.getDayNumber(), month.getMonthNumber(), year);
    }

    /**
     * Returns a date consisting of the monthnumber of this <code>month</code> field and year field.
     * If today is within this month, it returns today, otherwise the day in the date is set to one.
     */
    @Override protected Date getAppropriateDate() {
	Date today = Date.getToday();
	int day = 1;
	if (today.getYear() == year && today.getMonth() == month.getMonthNumber()){
	    day = today.getDay();
	}
	return new Date(day, month.getMonthNumber(), year);
    }

    /**
     * Adds the <code>ViewComponents</code> to the <code>viewComponents</code> list.
     */
    protected void addViewComponentsToList() {
	viewComponents.clear();
	for (Component component : viewPanel.getComponents()) {
	    viewComponents.add((ViewComponent) component);
	}
    }

    @Override public void reloadAppointments() {
	addAppointments();
	revalidate();
	repaint();
    }

    private void reloadDays(){
	addDays();
	revalidate();
	addViewComponentsToList();
    }
}
