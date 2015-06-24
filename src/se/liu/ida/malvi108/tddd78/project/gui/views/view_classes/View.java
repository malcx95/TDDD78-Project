package se.liu.ida.malvi108.tddd78.project.gui.views.view_classes;

import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.listeners.ViewListener;
import se.liu.ida.malvi108.tddd78.project.time.Date;

/**
 * Interface for views, which display appointments. Implementations of <code>Views</code> should extend <code>AbstactView</code>.
 * @see AbstractView
 */
public interface View
{
    /**
     * Goes to the next unit of time, for example the day view going to the
     * next date or the month view going to the next month.
     */
    void goToNext();


    /**
     * Goes to the previous unit of time, for example the day view going to the
     * previous date or the month view going to the previous month. Any implementation
     * of this method must either use the <code>setDate</code> method to change
     * dates, or manually make sure that the user can't set a date that precedes
     * the <code>Year.STARTING_YEAR</code>, which the <code>setDate</code> method
     * already ensures.
     */
    void goToPrevious();

    /**
     * Sets the date in the view to today.
     */
    void goToToday();

    /**
     * Sets the current date in the view. If the view stores multiple dates,
     * (such as the <code>MonthView</code>) is simply switches to the unit
     * of time where the date appeares. This method must disable any user input
     * that would cause the user to switch to a date that precedes the
     * <code>Year.STARTING_YEAR</code>. For example, if there is a button that
     * navigates to the previous date, this method must disable that button
     * when the earliest possible date is reached.
     *
     * @throws IllegalArgumentException If the year of the date precedes the
     * <code>Year.STARTING_YEAR</code>.
     */
    void setDate(Date date);

    /**
     * Gets the current selected appointment in the view. If no appointments are selected,
     * <code>null</code> is returned.
     */
    Appointment getSelectedAppointment();

    /**
     * Adds a <code>ViewListener</code> to the list of <code>ViewListeners</code>.
     */
    void addViewListener(ViewListener listener);

    /**
     * Deletes the currently selected appointment from it's associated calendar.
     * Does nothing if no appointment is selected.
     */
    void deleteSelectedAppointment();

    /**
     * Reloads all the appointments in the view.
     */
    void reloadAppointments();

    void inputNewAppointment();
}
