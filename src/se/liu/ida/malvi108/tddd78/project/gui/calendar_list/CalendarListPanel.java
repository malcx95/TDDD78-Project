package se.liu.ida.malvi108.tddd78.project.gui.calendar_list;

import se.liu.ida.malvi108.tddd78.project.gui.dialogs.CalendarDialog;
import se.liu.ida.malvi108.tddd78.project.listeners.CalendarPropertyListener;
import se.liu.ida.malvi108.tddd78.project.listeners.CalendarDatabaseListener;
import se.liu.ida.malvi108.tddd78.project.appointments.Calendar;
import se.liu.ida.malvi108.tddd78.project.databases.CalendarDatabase;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * The panel containing the list of calendars.
 *
 * @see CalendarListEntry
 */
public class CalendarListPanel extends JPanel implements CalendarDatabaseListener, CalendarPropertyListener
{
    private CalendarDatabase database;
    private JButton removeButton;

    public CalendarListPanel(){
	database = CalendarDatabase.getInstance();
	setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	removeButton = new JButton("Ta bort...");
	if (database.isEmpty()){
	    removeButton.setEnabled(false);
	} else {
	    removeButton.setEnabled(true);
	}
	removeButton.addActionListener(new RemoveCalendarAction());
	listenToAllCalendars();
	addCalendarsToView();
	setBackground(Color.WHITE);
    }

    private void listenToAllCalendars() {
	for (Calendar calendar : CalendarDatabase.getInstance().getCalendars()) {
	    calendar.addPropertyListener(this);
	}
    }

    private void addCalendarsToView(){
	this.removeAll();
	Calendar[] calendars = database.getCalendars();
	for (Calendar calendar : calendars){
	    CalendarListEntry entry = new CalendarListEntry(calendar);
	    add(entry);
	    entry.setAlignmentX(Component.LEFT_ALIGNMENT);
	    entry.addMouseListener(new DoubleClickListener());
	}
	add(removeButton);
    }

    public void databaseChanged() {
	listenToAllCalendars();
	addCalendarsToView();
	repaint();
	revalidate();
	if (database.isEmpty()){
	    removeButton.setEnabled(false);
	} else {
	    removeButton.setEnabled(true);
	}
    }

    /**
     * Invoked when a property (name or color) or a calendar is changed.
     */
    @Override public void calendarPropertyChanged() {
	addCalendarsToView();
	repaint();
	revalidate();
    }

    private class DoubleClickListener extends MouseAdapter
    {
	@Override public void mousePressed(final MouseEvent e) {
    	    super.mousePressed(e);
    	    if (e.getClickCount() == 2){
		Object source = e.getSource();
		assert source instanceof CalendarListEntry: "Internal error: Double clicked component not CalendarListEntry";
		CalendarListEntry entry = (CalendarListEntry) e.getSource();
		Calendar calendar = entry.getCalendar();
		CalendarDialog dialog = new CalendarDialog();
		dialog.setInitialParameters(calendar);
		dialog.inputCalendar(CalendarDialog.EDIT);
	    }
	}
    }
}