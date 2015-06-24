package se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations;

import se.liu.ida.malvi108.tddd78.project.appointments.WholeDayAppointment;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_components.WholeDayAppComponent;

import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;

/**
 * The illustration of <code>WholeDayAppointments</code>.
 */
public class WholeDayAppRectangle extends AppointmentRectangle
{
    private final static float STROKE_WIDTH = 1.5F;

    public WholeDayAppRectangle(WholeDayAppointment appointment, Color color){
	super(appointment, color);
	this.setSize(width, WholeDayAppComponent.APP_HEIGHT);
	placeLabels();
    }

    @Override protected void paintComponent(final Graphics g) {
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setColor(darkerColor);
	g2d.setStroke(new BasicStroke(STROKE_WIDTH));
	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, OPAQUE));
	g2d.drawRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
    }
}
