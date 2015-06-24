package se.liu.ida.malvi108.tddd78.project.gui.views.view_components;

import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.gui.miscellaneous.ClickType;
import se.liu.ida.malvi108.tddd78.project.listeners.ViewComponentListener;

import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Component that holds <code>AppointmentsIllustrations</code>.
 */
public abstract class ViewComponent extends JPanel
{
    protected List<ViewComponentListener> listeners;

    protected ViewComponent(){
        listeners = new ArrayList<>();
        addMouseListener(new ClickListener());
    }

    /**
     * Gets the selected appointment in the <code>ViewComponent</code>.
     * @return The selected appointment, null if no appointments are selected.
     */
    public abstract Appointment getSelectedAppointment();

    /**
     * This method returns the state of the wholeDayBox in the <code>AppointmentDialog</code>
     * the <code>ViewComponent</code> would preffer when doubleclicked. For instance, if
     * the component only holds <code>WholeDayAppointments</code>, an appropriate state would
     * be <code>true</code>.
     * @return The state the wholeDayBox in the <code>AppointmentDialog</code> that is shown when
     * the <code>ViewComponent is double clicked.</code>
     */
    public abstract boolean getPreferredWholeDayState();

    void notifyListeners(ClickType type){
        if (type == ClickType.CLICKED){
            for (ViewComponentListener listener : listeners) {
                listener.viewComponentClicked();
            }
        } else if (type == ClickType.DOUBLE_CLICKED){
            for (ViewComponentListener listener : listeners) {
                listener.viewComponentDoubleClicked(this);
            }
        }
    }

    public void addViewComponentListener(ViewComponentListener listener){
        listeners.add(listener);
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
