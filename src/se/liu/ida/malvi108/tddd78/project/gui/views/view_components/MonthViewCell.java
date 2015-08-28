package se.liu.ida.malvi108.tddd78.project.gui.views.view_components;

import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.appointments.StandardAppointment;
import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;
import se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations.MonthAppIllustration;
import se.liu.ida.malvi108.tddd78.project.time.Day;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;
import se.liu.ida.malvi108.tddd78.project.time.WeekDay;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Representation of a day in the MonthView.
 */
public class MonthViewCell extends ViewComponent
{
    private final static int NUMBER_MARGIN = 20;
    private final static int NUMBER_FONT_SIZE = 14;
    private final static int WEEK_NUMBER_FONT_SIZE = 11;
    private final static int LIGHT_GRAY = 210;
    private MonthCellType type;
    private Day day;
    /**
     * The panel where you add the <code>AppointmentIllustrations</code>. Only
     * <code>MonthAppIllustrations</code> is allowed to be added to it.
     */
    private final JPanel viewPane;
    private final static boolean PREFERRED_WHOLE_DAY_STATE = false;

    public MonthViewCell(Day day, MonthCellType type, boolean today) {
	setLayout(new BorderLayout());
	this.type = type;
	this.day = day;
	viewPane = new JPanel();
	viewPane.setLayout(new BoxLayout(viewPane, BoxLayout.PAGE_AXIS));
	addNumberPanel(day, type, today);
	JScrollPane scrollPane = new JScrollPane(viewPane);
	updateViewPanelSize();
	scrollPane.setBorder(null);
	scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
	add(scrollPane, BorderLayout.CENTER);
	viewPane.addMouseListener(getMouseListeners()[0]); //add the same ClickListener as the one that was added to the
							//MonthViewCell to the viewPane, so it too reacts to clicks.
    }

    private void addNumberPanel(final Day day, final MonthCellType type, final boolean today) {
	JPanel numberPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

	JLabel dayNumber = createDayNumberLabel(day, type, today);

	JLabel weekNumber = createWeekNumberLabel(day);

	numberPanel.add(dayNumber);
	numberPanel.add(weekNumber);
	add(numberPanel, BorderLayout.NORTH);
    }

    private JLabel createWeekNumberLabel(final Day day) {
	JLabel weekNumber = new JLabel();
	if (day.getWeekDay().equals(WeekDay.MONDAY)){
	    //if it's monday, show the weeknumber
	    weekNumber.setText("V." + Integer.toString(day.getWeekNumber()));
	weekNumber.setFont(new Font(MainFrame.GLOBAL_FONT, Font.PLAIN, WEEK_NUMBER_FONT_SIZE));
	weekNumber.setForeground(Color.GRAY);
	}
	return weekNumber;
    }

    private JLabel createDayNumberLabel(final Day day, final MonthCellType type, final boolean today) {
	JLabel number = new JLabel(Integer.toString(day.getNumber()));
	if (today) {
	    number.setForeground(Color.RED);
	}
	if (type.equals(MonthCellType.OUTSIDE)) {
	    setBorder(new LineBorder(Color.GRAY));
	    number.setForeground(new Color(LIGHT_GRAY, LIGHT_GRAY, LIGHT_GRAY));
	} else {
	    setBorder(new LineBorder(Color.BLACK));
	}
	number.setFont(new Font(MainFrame.GLOBAL_FONT, Font.BOLD, NUMBER_FONT_SIZE));
	number.setHorizontalAlignment(SwingConstants.LEADING);
	Dimension size = new Dimension(NUMBER_MARGIN, NUMBER_MARGIN);
	number.setMaximumSize(size);
	number.setMinimumSize(size);
	number.setPreferredSize(size);
	return number;
    }

    public int getDayNumber() {
	return day.getNumber();
    }

    /**
     * Sorts all AppIllustrations by their start time. Puts WholeDayAppointments at the top.
     */
    public void sort(){
	Component[] components = viewPane.getComponents();
	List<MonthAppIllustration> stdApps = new ArrayList<>();
	Collection<MonthAppIllustration> wholeDayApps = new ArrayList<>();
	//Sort out StandardAppointments and WholeDayAppointments
	for (Component component : components) {
	    MonthAppIllustration app = (MonthAppIllustration) component;
	    if (app.getAppointment() instanceof StandardAppointment) {
		stdApps.add(app);
	    } else {
		wholeDayApps.add(app);
	    }
	}
	//sort the StandardAppointments by their start time.
	Collections.sort(stdApps, new Comparator<MonthAppIllustration>()
	{
	    @Override public int compare(final MonthAppIllustration o1, final MonthAppIllustration o2) {
		StandardAppointment app1 = (StandardAppointment) o1.getAppointment();
		TimePoint start1 = app1.getDuration().getStart();

		StandardAppointment app2 = (StandardAppointment) o2.getAppointment();
		TimePoint start2 = app2.getDuration().getStart();

		if (start1.precedes(start2)) {
		    return -1;
		} else if (start2.precedes(start1)) {
		    return 1;
		}
		return 0;
	    }
	});
	reAddAppointments(wholeDayApps);
	reAddAppointments(stdApps);
    }

    private void reAddAppointments(final Iterable<MonthAppIllustration> apps) {
	for (MonthAppIllustration appointment : apps) {
	    viewPane.add(appointment);
	}
    }

    public void addAppointment(MonthAppIllustration app){
	viewPane.add(app);
	app.setAlignmentX(LEFT_ALIGNMENT);
	updateViewPanelSize();
    }

    public void removeAllAppointments(){
	viewPane.removeAll();
    }

    private void updateViewPanelSize(){
	int height = viewPane.getComponentCount() * MonthAppIllustration.MONTH_APP_HEIGHT;
	viewPane.setPreferredSize(new Dimension(viewPane.getWidth(), height));
    }

    public void deselectAll(){
	for (Component component : viewPane.getComponents()) {
	    MonthAppIllustration app = (MonthAppIllustration) component;
	    app.setSelected(false);
	}
    }

    /*@Override public synchronized void addMouseListener(final MouseListener l) {
	super.addMouseListener(l);
	viewPane.addMouseListener(l);
    }*/

    private Iterable<MonthAppIllustration> getAppointmentIllustrations(){
	Collection<MonthAppIllustration> result = new ArrayList<>();
	for (Component component : viewPane.getComponents()) {
	    result.add((MonthAppIllustration) component);
	}
	return result;
    }

    /**
     * Gets the selected appointment in the <code>ViewComponent</code>.
     *
     * @return The selected appointment, null if no appointments are selected.
     */
    @Override public Appointment getSelectedAppointment() {
	for (MonthAppIllustration app : getAppointmentIllustrations()) {
	    if (app.isSelected()) return app.getAppointment();
	}
	return null;
    }

    public MonthCellType getType() {
	return type;
    }

    @Override public boolean getPreferredWholeDayState() {
	return PREFERRED_WHOLE_DAY_STATE; // this is the preferred whole day state
    }
}
