package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

import se.liu.ida.malvi108.tddd78.project.appointments.Calendar;
import se.liu.ida.malvi108.tddd78.project.databases.CalendarDatabase;
import se.liu.ida.malvi108.tddd78.project.exceptions.CalendarAlreadyExistsException;
import se.liu.ida.malvi108.tddd78.project.exceptions.InvalidCalendarNameException;
import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;

import javax.swing.*;
import java.awt.Color;

/**
 * Class for inputing calendars through a dialog box
 */
public final class CalendarDialog
{
    private String name;
    private Color initialColor;
    private Calendar oldCalendar;

    /**
     * Sent as a parameter to the <code>inputCalendar</code> method when a caller wants to input a new calendar.
     */
    public final static int NEW = 1;
    /**
     * Sent as a parameter to the <code>inputCalendar</code> method when a caller wants to change an existing calendar.
     */
    public final static int EDIT = 2;

    public CalendarDialog() {
	name = null;
	initialColor = null;
	oldCalendar = null;
    }

    /**
     * Sets the inital selected color and name in the dialog to those of the given calendar.
     */
    public void setInitialParameters(Calendar calendar) {
	initialColor = calendar.getColor();
	name = calendar.getName();
	oldCalendar = calendar;
    }

    /**
     * Shows a dialog for the user to create a new calendar, or edit an existing one.
     * @param type <code>CalendarDialog.NEW</code> if you want to create a new one,
     *             <code>CalendarDialog.EDIT</code> if you want to edit an existing calendar.
     */
    public void inputCalendar(int type){
	JTextField calendarName = new JTextField();
	JComboBox<ColorChoice> colorChooser = new JComboBox<>(ColorChoice.values());
	String title;
	if (type == EDIT) {
	    colorChooser.setSelectedItem(ColorChoice.CUSTOM);
	    calendarName.setText(name);
	    title = "Redigera kalender";
	} else if (type == NEW) {
	    title = "Ny kalender";
	    ColorChoice initialChoice = (ColorChoice) colorChooser.getSelectedItem();
	    initialColor = initialChoice.getColor();
	} else {
	    throw new IllegalArgumentException("Type must be either CalendarDialog.NEW or CalendarDialog.EDIT");
	}
	ColorChoicePanel colorChoicePanel  = new ColorChoicePanel(colorChooser, initialColor);
	final JComponent[] inputs = getInputs(calendarName, colorChoicePanel);
	int option = JOptionPane.showOptionDialog(null, inputs,
						  title, JOptionPane.OK_CANCEL_OPTION,
						  JOptionPane.PLAIN_MESSAGE, null, null, null);
	interpretInputs(type, calendarName.getText(), colorChoicePanel.getSelectedColor(), option);
    }

    private void interpretInputs(int type, String calendarName, Color color, int option) {
	try{
	    if (option == JOptionPane.OK_OPTION) {
		createAndAddCalendarToDatabase(type, calendarName, color);
	    }
	} catch (InvalidCalendarNameException ex){
	    handleInvalidName(ex.getName(), ex.getType());
	    inputCalendar(type);
	} catch (CalendarAlreadyExistsException ex){
	    JOptionPane.showMessageDialog(null, "Kalender med namnet \"" + ex.getExistingCalendar().getName()
						+ " finns redan!", "Kalender finns redan", JOptionPane.ERROR_MESSAGE, MainFrame.PLAN_IT_ERROR);
	    inputCalendar(type);
	}
    }

    private JComponent[] getInputs(final JTextField calendarName, final ColorChoicePanel colorChoicePanel) {
	return new JComponent[] {
		    new JLabel("Namn"),
		    calendarName,
		    new JLabel("Färg"),
		    colorChoicePanel
	};
    }

    /**
     * Tries to create a calendar from the user's input and adds it to the database. Throws
     * <code>CalendarAlreadyExistsException</code> or <code>InvalidCalendarNameException</code>
     * if this fails.
     *
     * @throws CalendarAlreadyExistsException If a calendar with the given name already exists.
     * @throws InvalidCalendarNameException If a calendar name is too long or too short.
     */
    private void createAndAddCalendarToDatabase(int type, String name, Color color)
	    throws CalendarAlreadyExistsException, InvalidCalendarNameException {
	CalendarDatabase database = CalendarDatabase.getInstance();
	if (type == NEW){
	    database.addCalendar(new Calendar(name, color));
	} else {
	    assert oldCalendar != null: "Internal error, oldCalendar is null";
	    //If the database already contains the new name and is not equal to the name of the
	    //old calendar, it means that the name was changed to a name that one of the other
	    //calendars have, which is an illegal action.
	    if (database.contains(name) && !name.equals(oldCalendar.getName())){
		throw new CalendarAlreadyExistsException("Calendar name already exists", database.get(name));
	    }
	    oldCalendar.setName(name);
	    oldCalendar.setColor(color);
	}
	CalendarDatabase.getInstance().tryToSaveChanges();
    }

    private void handleInvalidName(String name, int type){
	String error;
	if (type == InvalidCalendarNameException.NAME_TOO_LONG){
	    error = "långt";
	} else {
	    error = "kort";
	}
	JOptionPane.showMessageDialog(null, "Namnet \"" + name + "\" är för " + error + "!", "För " + error + " namn",
				      JOptionPane.ERROR_MESSAGE, MainFrame.PLAN_IT_ERROR);
    }

    public enum ColorChoice{
	/**
	 * Red
	 */
	RED,
	/**
	 * Blue
	 */
	BLUE,
	/**
	 * Gray
	 */
	GRAY,
	/**
	 * Green
	 */
	GREEN,
	/**
	 * Orange
	 */
	ORANGE,
	/**
	 * Black
	 */
	BLACK,
	/**
	 * Custom choice
	 */
	CUSTOM;

	@Override public String toString() {
	    switch (this) {
		case RED:
		    return "Röd";
		case BLUE:
		    return "Blå";
		case GRAY:
		    return "Grå";
		case GREEN:
		    return "Grön";
		case ORANGE:
		    return "Orange";
		case BLACK:
		    return "Svart";
		case CUSTOM:
		    return "Anpassad...";
		default:
		    return null;
	    }
	}

	/**
	 * Gets the corresponding color. If CUSTOM, return null.
	 */
	public Color getColor(){
	    switch (this) {
		case RED:
		    return Color.RED;
		case BLUE:
		    return Color.BLUE;
		case GRAY:
		    return Color.GRAY;
		case GREEN:
		    return Color.GREEN;
		case ORANGE:
		    return Color.ORANGE;
		case BLACK:
		    return Color.BLACK;
		case CUSTOM:
		    return null;
		default:
		    return null;
	    }
	}
    }
}
