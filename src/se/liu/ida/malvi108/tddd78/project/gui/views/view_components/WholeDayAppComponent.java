package se.liu.ida.malvi108.tddd78.project.gui.views.view_components;

/**
 * Panel that holds the whole day appointments.
 */
public class WholeDayAppComponent extends DayViewComponent
{
    /**
     * The height in pixels of each appointment in the WholeDayAppComponent.
     */
    public final static int APP_HEIGHT = 30;
    private final static boolean PREFERRED_WHOLE_DAY_STATE = true;

    /**
     * This method returns the state of the wholeDayBox in the <code>AppointmentDialog</code> the <code>ViewComponent</code>
     * would preffer when doubleclicked. For instance, if the component only holds <code>WholeDayAppointments</code>, an
     * appropriate state would be <code>true</code>.
     *
     * @return The state the wholeDayBox in the <code>AppointmentDialog</code> that is shown when the <code>ViewComponent is
     * double clicked.</code>
     */
    @Override public boolean getPreferredWholeDayState() {
        return PREFERRED_WHOLE_DAY_STATE; //this is the preferred whole day state
    }
}
