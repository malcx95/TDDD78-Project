package se.liu.ida.malvi108.tddd78.project.listeners;

import java.io.Serializable;

/**
 * Generic interface for all DatabaseListeners.
 */
public interface CalendarDatabaseListener extends Serializable
{
    /**
     * Called when a calendar is added or removed to the database.
     */
    void databaseChanged();
}
