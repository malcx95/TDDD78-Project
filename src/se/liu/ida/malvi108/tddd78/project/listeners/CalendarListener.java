package se.liu.ida.malvi108.tddd78.project.listeners;

/**
 * Listens to <code>Calendars</code> and is notified when a calendar's
 * content is changed.
 */
public interface CalendarListener
{
    /**
     * Invoked when appointments in the calendar are added, removed or changed, or
     * if the calendar is enabled or disabled.
     */
    void calendarContentChangedOrEnabledToggled();

}
