package se.liu.ida.malvi108.tddd78.project.gui.views.todo_list;

import se.liu.ida.malvi108.tddd78.project.listeners.ToDoListListener;
import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.todo_list.ToDoList;
import se.liu.ida.malvi108.tddd78.project.databases.ToDoListDatabase;
import se.liu.ida.malvi108.tddd78.project.todo_list.ToDoListEntry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The pane that holds the <code>ToDoList</code> for the day.
 */
public final class ToDoListPane extends JPanel implements ToDoListListener, ActionListener
{
    /**
     * The font size of each entry in the <code>ToDoListPane</code>.
     */
    public final static int ENTRY_FONT_SIZE = 12;
    private ToDoList todaysList;
    private Date date;

    public ToDoListPane(Date date){
	this.date = date;
	setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	setTodaysList();
	addEntries();
    }

    private void setTodaysList() {
	ToDoListDatabase database = ToDoListDatabase.getInstance();
	if (database.get(date) == null){
	    todaysList = new ToDoList(date);
	    database.addToDoList(todaysList);
	} else {
	    todaysList = database.get(date);
	}
	todaysList.addToDoListListener(this);
    }

    private void addEntries() {
	removeAll();
	if (todaysList.isEmpty()) {
	    JLabel emptyLabel = new JLabel("Inget att göra idag");
	    emptyLabel.setForeground(Color.GRAY);
	    add(emptyLabel);
	} else {
	    for (ToDoListEntry entry : todaysList.getEntries()) {
		ToDoListPaneEntry paneEntry = new ToDoListPaneEntry(entry);
		add(paneEntry);
		paneEntry.setAlignmentX(Component.LEFT_ALIGNMENT);
		paneEntry.addActionListener(this);
	    }
	}
    }

    @Override public void entryAddedOrRemoved() {
	addEntries();
	revalidate();
    }

    /**
     * Invoked when someone marks or unmarks an entry as done.
     */
    @Override public void actionPerformed(final ActionEvent e) {
	final ToDoListPaneEntry entry = (ToDoListPaneEntry) e.getSource();
	if (entry.isSelected()){
	    entry.getEntry().setDone(true);
	    entry.setFont(new Font(MainFrame.GLOBAL_FONT, Font.ITALIC, ENTRY_FONT_SIZE));
	    entry.setForeground(Color.GREEN);
	} else {
	    entry.getEntry().setDone(false);
	    entry.setForeground(Color.BLACK);
	    entry.setFont(new Font(MainFrame.GLOBAL_FONT, Font.BOLD, ENTRY_FONT_SIZE));
	}
    }

    public void setDate(final Date date) {
	this.date = date;
	setTodaysList();
	addEntries();
	revalidate();
	repaint();
    }
}