package se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations;

import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.gui.miscellaneous.ClickType;
import se.liu.ida.malvi108.tddd78.project.listeners.AppIllustrationListener;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic class for all illustrations of appointments.
 */
public class AppointmentIllustration extends JComponent
{
    protected final static int TEXT_SIZE = 14;
    protected final static float OPAQUE = 1.0F;
    protected final static float TRANSPARENT = 0.6F;
    protected final static float LESS_TRANSPARENT = 0.8F;
    protected Color color;
    protected String subject;
    protected Appointment appointment;
    protected boolean selected;
    protected JLabel subjectLabel;
    protected List<AppIllustrationListener> listeners;

    protected AppointmentIllustration(Appointment appointment, Color color){
	this.color = color;
	this.appointment = appointment;
	this.subject = appointment.getSubject();
	subjectLabel = new JLabel(subject);
	this.selected = false;
        listeners = new ArrayList<>();
        addMouseListener(new ClickListener());
    }

    @Override public void setForeground(final Color fg) {
	super.setForeground(fg);
	this.color = fg;
    }

    public void addAppIllustrationListener(AppIllustrationListener listener){
        listeners.add(listener);
    }

    public Appointment getAppointment(){
	return appointment;
    }

    public void setSelected(boolean selected){
	this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    private void notifyListeners(ClickType type){
        if (type == ClickType.CLICKED){
            for (AppIllustrationListener listener : listeners) {
                listener.appointmentClicked(this);
            }
        } else {
            for (AppIllustrationListener listener : listeners) {
                listener.appointmentDoubleClicked(this);
            }
        }
    }

    private class ClickListener extends MouseInputAdapter{
        @Override public void mouseClicked(final MouseEvent e) {
            super.mouseClicked(e);
            if (e.getClickCount() == 2){
                notifyListeners(ClickType.DOUBLE_CLICKED);
            }
        }

        @Override public void mousePressed(final MouseEvent e) {
            super.mousePressed(e);
            if (e.getClickCount() == 1){
                notifyListeners(ClickType.CLICKED);
            }
        }
    }
}
