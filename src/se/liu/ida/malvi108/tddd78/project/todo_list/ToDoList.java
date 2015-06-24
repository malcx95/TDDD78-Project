package se.liu.ida.malvi108.tddd78.project.todo_list;

import se.liu.ida.malvi108.tddd78.project.databases.ToDoListDatabase;
import se.liu.ida.malvi108.tddd78.project.listeners.ToDoListListener;
import se.liu.ida.malvi108.tddd78.project.time.Date;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores a list of things to do on a particular day, which is a
 * way for the user to plan their day other than through appointments.
 */
public class ToDoList implements Serializable
{
    private Date date;
    private List<ToDoListEntry> entries;
    private List<ToDoListListener> listeners;
    private final static Logger LOGGER = Logger.getLogger(ToDoList.class.getName());

    public ToDoList(Date date){
	this.date = date;
        entries = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public void addToDoListListener(ToDoListListener listener){
        listeners.add(listener);
    }

    public void addEntry(ToDoListEntry entry){
        entries.add(entry);
        notifyListeners();
        LOGGER.log(Level.INFO, "Entry \"" + entry + "\" added to ToDoList at " + date);
        ToDoListDatabase.getInstance().tryToSaveChanges();
    }

    public Iterable<ToDoListEntry> getEntries(){
        return entries;
    }

    public ToDoListEntry[] getEntriesArray(){
        ToDoListEntry[] result = new ToDoListEntry[entries.size()];
        for (int i = 0; i < result.length; i++){
            result[i] = entries.get(i);
        }
        return result;
    }

    public Date getDate() {
	return date;
    }

    private void notifyListeners(){
        for (ToDoListListener listener : listeners) {
            listener.entryAddedOrRemoved();
        }
    }

    public boolean isEmpty(){
        return entries.isEmpty();
    }

    public void removeEntries(Iterable<ToDoListEntry> entries){
        for (ToDoListEntry entry : entries) {
            this.entries.remove(entry);
        }
        notifyListeners();
        LOGGER.log(Level.INFO, "Entries " + entries + " removed from ToDoList at " + date);
        ToDoListDatabase.getInstance().tryToSaveChanges();
    }
}
