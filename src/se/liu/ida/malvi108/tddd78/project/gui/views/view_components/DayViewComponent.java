package se.liu.ida.malvi108.tddd78.project.gui.views.view_components;

import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations.AppointmentRectangle;

import java.awt.*;

/**
 * General class for <code>ViewComponents</code> in the <code>DayView</code>.
 */
public abstract class DayViewComponent extends ViewComponent
{
    @Override public Appointment getSelectedAppointment() {
	for (Component component : getComponents()) {
	    AppointmentRectangle rect = (AppointmentRectangle) component;
	    if (rect.isSelected()) return rect.getAppointment();
	}
	return null;
    }
}
