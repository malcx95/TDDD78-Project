package se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations;

import se.liu.ida.malvi108.tddd78.project.appointments.StandardAppointment;
import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;
import se.liu.ida.malvi108.tddd78.project.gui.views.view_components.StandardAppViewComponent;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.AlphaComposite;

/**
 * Class for the graphic rectangles used to represent a standard appointment in the day- and week view
 */
public class StandardAppRectangle extends AppointmentRectangle
{
    private JLabel timeLabel;
    private final static float STROKE_WIDTH = 2.0F;
    /**
     * If the rectangle overlaps with another, the placementOrder determines the order in which
     * it should be placed in the day- and week view. A placement order of 0 means it should
     * be placed at the first position from the left, 1 means second and so on.
     */
    private int placementOrder;
    /**
     * Number of other rectangles it overlaps with in the view. Assigned by the View-classes.
     */
    private int overlapping;
    private final static int MINUMUM_HEIGHT = 20;

    public StandardAppRectangle(StandardAppointment appointment, final Color color){
	super(appointment, color);
	int height = getCorrectHeight(appointment);
	final String time = appointment.getDuration().toString();
	timeLabel = new JLabel(time);
	placeLabels();
	this.setSize(width, height);
    }

    private int getCorrectHeight(final StandardAppointment appointment) {
	int height = (int) (appointment.getDuration().getDurationInHours() * StandardAppViewComponent.HOUR_SIZE);
	if (height < MINUMUM_HEIGHT){
	    return MINUMUM_HEIGHT;
	} else {
	    return height;
	}
    }

    @Override protected void placeLabels() {
	timeLabel.setFont(new Font(MainFrame.GLOBAL_FONT, Font.PLAIN, TEXT_SIZE));
	timeLabel.setForeground(darkerColor);
	timeLabel.setAlignmentX(LEFT_ALIGNMENT);
	this.add(timeLabel);
	super.placeLabels();
    }

    @Override protected void paintComponent(final Graphics g) {
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setColor(darkerColor);
	g2d.setStroke(new BasicStroke(STROKE_WIDTH));
	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, OPAQUE));
	g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
    }

    @Override public void setForeground(final Color fg) {
	super.setForeground(fg);
	timeLabel.setForeground(fg.darker());
	subjectLabel.setForeground(fg.darker());
	repaint();
    }

    public int getPlacementOrder() {
	return placementOrder;
    }

    public int getOverlapping() {
	return overlapping;
    }

    public void setPlacementOrder(final int placementOrder) {
	this.placementOrder = placementOrder;
    }

    public void setOverlapping(final int overlapping) {
	this.overlapping = overlapping;
    }

    @Override public void setSize(int width, int height) {
	this.width = width;
	int newHeight;
	if (height < MINUMUM_HEIGHT){
	    newHeight = MINUMUM_HEIGHT;
	} else {
	    newHeight = height;
	}
	super.setSize(width, newHeight);
	revalidate();
    }

    @Override public void setSelected(final boolean selected) {
	super.setSelected(selected);
	if (selected){
	    timeLabel.setForeground(Color.WHITE);
	} else {
	    timeLabel.setForeground(darkerColor);
	}
    }
}