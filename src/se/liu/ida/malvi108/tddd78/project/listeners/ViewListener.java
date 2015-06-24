package se.liu.ida.malvi108.tddd78.project.listeners;

/**
 * Listens to a particular <code>View</code>, and is notified when an appointment is selected.
 */
public interface ViewListener
{
    /**
     * Invoked when someone selects or deselects an appointment in a view.
     */
    void appointmentSelectedOrDeselected();
    //TODO dela in i selected och deselected
}
