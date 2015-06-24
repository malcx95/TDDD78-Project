package se.liu.ida.malvi108.tddd78.project.listeners;

import se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations.AppointmentIllustration;

/**
 * Listens to <code>AppointmentIllustrations</code> being clicked or double clicked.
 */
public interface AppIllustrationListener
{
    /**
     * Invoked when an <code>AppointmentIllustration</code> is clicked.
     * @param source The <code>AppointmentIllustration</code> that was clicked.
     */
    void appointmentClicked(AppointmentIllustration source);

    /**
     * Invoked when an <code>AppointmentIllustration</code> is double-clicked.
     * @param source The <code>AppointmentIllustration</code> that was double-clicked.
     */
    void appointmentDoubleClicked(AppointmentIllustration source);
}
