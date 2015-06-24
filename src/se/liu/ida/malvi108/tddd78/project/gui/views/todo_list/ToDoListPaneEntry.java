package se.liu.ida.malvi108.tddd78.project.gui.views.todo_list;

import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;
import se.liu.ida.malvi108.tddd78.project.todo_list.ToDoListEntry;

import javax.swing.*;
import java.awt.*;

/**
 * Checkbox representing an entry in the <code>ToDoListPane</code>.
 * Contains a <code>ToDoListEntry</code>.
 */
public final class ToDoListPaneEntry extends JCheckBox
{
    private ToDoListEntry entry;

    public ToDoListPaneEntry(ToDoListEntry entry){
	super(entry.getSubject());
	this.entry = entry;
        if (entry.isDone()){
            setSelected(true);
            setFont(new Font(MainFrame.GLOBAL_FONT, Font.ITALIC, ToDoListPane.ENTRY_FONT_SIZE));
            setForeground(Color.GREEN);
        } else {
            setSelected(false);
            setForeground(Color.BLACK);
            setFont(new Font(MainFrame.GLOBAL_FONT, Font.BOLD, ToDoListPane.ENTRY_FONT_SIZE));
        }
    }

    public ToDoListEntry getEntry() {
	return entry;
    }
}
