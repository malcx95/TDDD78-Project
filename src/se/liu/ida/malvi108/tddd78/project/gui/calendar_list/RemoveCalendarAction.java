package se.liu.ida.malvi108.tddd78.project.gui.calendar_list;

import se.liu.ida.malvi108.tddd78.project.appointments.Calendar;
import se.liu.ida.malvi108.tddd78.project.databases.CalendarDatabase;
import se.liu.ida.malvi108.tddd78.project.gui.dialogs.RemoveDialog;
import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Action for showing a <code>RemoveDialog</code> for removing calendars. The user selects which calendars to remove,
 * the GUI asks the user if they're sure they want to remove the calendars, and then removes the selected calendars from the database.
 *
 * @see RemoveDialog
 */
public class RemoveCalendarAction extends AbstractAction
{
    @Override public void actionPerformed(final ActionEvent e) {
	CalendarDatabase database = CalendarDatabase.getInstance();
	Calendar[] calendars = database.getCalendars();
	RemoveDialog<Calendar> dialog = new RemoveDialog<>(calendars);
	List<Calendar> calendarsToRemove = dialog.showRemoveDialog();
	if (calendarsToRemove != null){
	    int option;
	    //Ask the user if they're sure they want to delete.
	    //Split into cases of deleting one or several in order
	    //to display an appropriate message.
	    if (calendarsToRemove.size() == 1) {
		option = JOptionPane.showConfirmDialog(null, "<html><body>Är du säker på att du vill " +
							     "ta bort denna kalender? <br>" +
							     "Detta raderar även alla kalenderns aktiviteter.</body></html>",
						       "Radering", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
						       MainFrame.PLAN_IT_WARNING);
	    } else {
		option = JOptionPane.showConfirmDialog(null, "<html><body>Är du säker på att du vill " +
							     "ta bort dessa kalendrar? <br>" +
							     "Detta raderar även alla kalendrarnas aktiviteter.</body></html>",
						       "Radering", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, MainFrame.PLAN_IT_WARNING);
	    }
	    if (option == JOptionPane.OK_OPTION) {
		database.removeCalendars(calendarsToRemove);
		database.tryToSaveChanges();
	    }
	}
    }
}

