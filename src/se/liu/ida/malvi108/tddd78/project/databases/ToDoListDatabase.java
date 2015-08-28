package se.liu.ida.malvi108.tddd78.project.databases;

import se.liu.ida.malvi108.tddd78.project.exceptions.FileCorruptedException;
import se.liu.ida.malvi108.tddd78.project.listeners.DatabaseSaveErrorListener;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.todo_list.ToDoList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.logging.Level;

/**
 * The database that stores the <code>ToDoLists</code>.
 * @see Database
 * @see CalendarDatabase
 */
public final class ToDoListDatabase extends Database<ToDoList>
{
    private final static ToDoListDatabase INSTANCE = new ToDoListDatabase();

    public static ToDoListDatabase getInstance(){
	return INSTANCE;
    }

    private ToDoListDatabase(){
        super("todolist_database.dat");
    }

    /**
     * Gets the number of <code>ToDoLists</code> in the database that contain at least one entry.
     */
    private int getNumberOfNonEmptyLists(){
        int result = 0;
        for (ToDoList toDoList : elements) {
            if (!toDoList.isEmpty()) result++;
        }
        return result;
    }

    /**
     * Saves the non-empty <code>ToDoLists</code> to the database file. Writes in this format:
     * - Number of non-empty lists
     * - All non-empty lists.
     * If the save fails, it notifies the it's <code>DatabaseSaveErrorListeners</code>.
     */
    public void tryToSaveChanges(){
        try (ObjectOutput out = new ObjectOutputStream(new FileOutputStream(databaseFile))){
            int numberOfLists = getNumberOfNonEmptyLists();
            out.writeObject(numberOfLists);
            for (ToDoList toDoList : elements) {
                if (!toDoList.isEmpty()){
                    out.writeObject(toDoList);
                }
            }
            LOGGER.log(Level.INFO, "ToDoListDatabase sucessfully saved.");
        } catch (FileNotFoundException ignored){
            //If the save file is not found, probably because someone tampered with it while the application was running,
            //a new file is created.
            LOGGER.log(Level.WARNING, "ToDoListDatabase file not found, trying to create new file...");
            try {
                createDatabaseFile();
                tryToSaveChanges();
                LOGGER.log(Level.INFO, "New ToDoListDatabase file created.");
            } catch (IOException ex){
                LOGGER.log(Level.SEVERE, "Couldn't create new ToDoListDatabase file!");
                notifyErrorListeners(ex);
            }
        } catch (IOException ex){
            LOGGER.log(Level.SEVERE, "Couldn't save the ToDoListDatabase! Error: " + ex.getMessage());
            notifyErrorListeners(ex);
       	}
    }

    public void loadDatabase() throws FileNotFoundException, IOException, FileCorruptedException
    {
        try (ObjectInput in = new ObjectInputStream(new FileInputStream(databaseFile))) {
            int numberOfLists = (int) in.readObject();
            for (int i = 0; i < numberOfLists; i++) {
                elements.add((ToDoList) in.readObject());
            }
            LOGGER.log(Level.INFO, "ToDoListDatabase successfully loaded.");
        } catch (ClassNotFoundException | StreamCorruptedException ex){
            throw new FileCorruptedException("ToDoListDatabase file corrupted", ex);
        } catch (FileNotFoundException ex){
            LOGGER.log(Level.WARNING, "ToDoListDatabase file not found, creating new file...");
            createDatabaseFile();
            LOGGER.log(Level.INFO, "New ToDoListDatabase file sucessfully created.");
            throw ex;
        }
    }

    private void notifyErrorListeners(final IOException ex) {
        for (DatabaseSaveErrorListener errorListener : errorListeners) {
            errorListener.toDoListDatabaseSaveFailed(ex);
        }
    }

    /**
     * Gets the <code>ToDoList</code> for the given date. Returns <code>null</code> if there
     * is no <code>ToDoList</code> on the date.
     */
    public ToDoList get(Date date){
	for (ToDoList list: elements){
	    if (list.getDate().equals(date)) return list;
	}
	return null;
    }

    public void addToDoList(ToDoList list){
        elements.add(list);
        LOGGER.log(Level.INFO, "Empty ToDoList added for date: " + list.getDate());
    }
}
