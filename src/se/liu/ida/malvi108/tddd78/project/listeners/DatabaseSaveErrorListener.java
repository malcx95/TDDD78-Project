package se.liu.ida.malvi108.tddd78.project.listeners;

import se.liu.ida.malvi108.tddd78.project.databases.Database;

import java.io.IOException;

/**
 * Listens to a <code>Database</code> and is notified when an <code>IOException</code> occured while saving.
 * This is used as an alternative to catching the <code>IOException</code>, since not all classes
 * that call the <code>saveChanges</code> method in the <code>Database</code> are interested whether the save
 * failed or not. Any class that would be interested need only to implement this interface.
 *
 * @see Database
 */
public interface DatabaseSaveErrorListener
{
    /**
     * Invoked when the <code>CalendarDatabase</code> is unable to save.
     *
     * @param ex The exception that caused the save to fail.
     */
    void calendarDatabaseSaveFailed(IOException ex);


    /**
     * Invoked when the <code>ToDoListDatabase</code> is unable to save.
     *
     * @param ex The exception that caused the save to fail.
     */
    void toDoListDatabaseSaveFailed(IOException ex);
}
