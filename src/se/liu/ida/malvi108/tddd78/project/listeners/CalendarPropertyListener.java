package se.liu.ida.malvi108.tddd78.project.listeners;

/**
 * Listens to <code>Calendars</code> and is notified when a calendar's properties
 * (name or color) is changed.
 */
public interface CalendarPropertyListener
{
    /**
     * Invoked when a property (name or color) or a calendar is changed.
     */
    void calendarPropertyChanged();
}