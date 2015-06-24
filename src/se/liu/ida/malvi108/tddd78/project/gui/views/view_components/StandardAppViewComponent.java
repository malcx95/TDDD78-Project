package se.liu.ida.malvi108.tddd78.project.gui.views.view_components;

import se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations.StandardAppRectangle;

import java.awt.*;

/**
 * The component that shows the appointments on a given day.
 */
public class StandardAppViewComponent extends DayViewComponent
{
    /**
     * The vertical size of each hour in the DayViewComponent in pixels.
     */
    public final static int HOUR_SIZE = 50;
    private final static int NUMBER_OF_HOURS = 24;
    private final static int TIME_SIZE = 12;
    /**
     * The gap the viewcomponent leaves between the left edge of the component and the lines and StandardAppRectangles.
     */
    public final static int LEFT_LINE_MARGIN = 42;
    /**
     * The gap the viewcomponent leaves between the right edge of the component and the lines and StandardAppRectangles.
     */
    private final static int RIGHT_LINE_MARGIN = 20;
    private final static int MINIMUM_WIDTH = 300;
    private final static boolean PREFERRED_WHOLE_DAY_STATE = false;

    public StandardAppViewComponent() {
	this.setLayout(null);
	this.setPreferredSize(new Dimension(MINIMUM_WIDTH, HOUR_SIZE * NUMBER_OF_HOURS));
    }

    /**
     * Adjusts the positions and the sizes of all the StandardAppRectangles in the current view. Used by the setSize method to
     * adjust the appointments when the frame is resized, since there is no suitable layout manager for this job.
     */
    public void adjustAppointments() {
	Component[] components = getComponents();
	//for (int i = 0; i < components.length; i++)
	for (Component comp : components) {
	    StandardAppRectangle rect = (StandardAppRectangle) comp;
	    // The width of the space in which the appointments are drawn
	    final int viewWidth = getViewWidth();
	    // The width of the appointment is calculated based on how many other appointments it overlaps
	    int appWidth = viewWidth / (rect.getOverlapping() + 1); // 1 is added to the overlapping number
	    rect.setSize(appWidth, rect.getHeight());                // to avoid a zero-division error
	    int currentY = rect.getLocation().y;
	    int newX = LEFT_LINE_MARGIN + (rect.getPlacementOrder() * (viewWidth / (rect.getOverlapping() + 1)));
	    rect.setLocation(newX, currentY);
	}
    }

    @Override protected void paintComponent(final Graphics g) {
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;
	drawLines(g2d);
	drawTimes(g2d);
    }

    private void drawLines(final Graphics2D g2d) {
	g2d.setColor(Color.GRAY);
	for (int i = 1; i < NUMBER_OF_HOURS; i++) {
	    g2d.drawLine(LEFT_LINE_MARGIN, i * HOUR_SIZE, this.getWidth() - RIGHT_LINE_MARGIN, i * HOUR_SIZE);
	}
    }

    private void drawTimes(final Graphics2D g2d) {
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setFont(new Font("Helvetica", Font.BOLD, TIME_SIZE));
	for (int i = 1; i < NUMBER_OF_HOURS; i++) {
	    String time;
	    if (i < 10) time = "0" + i + ":00";
	    else time = i + ":00";
	    g2d.drawString(time, 4, i * HOUR_SIZE + 4);
	}
    }

    /**
     * Gets the width of the space where the StandardAppRectangles are placed, that is the width of the DayViewComponent minus
     * the margins.
     */
    public int getViewWidth() {
	return (this.getWidth() - (LEFT_LINE_MARGIN + RIGHT_LINE_MARGIN));
    }

    @Override public boolean getPreferredWholeDayState() {
	return PREFERRED_WHOLE_DAY_STATE; //this is the preferred whole day state
    }

    @Override public void setSize(final Dimension dimension) {
	super.setSize(dimension);
	/*
	This is to adjust the positions and widths of all appointments when the frame is resized.
	Since the setSize-method is called everytime this happens, it makes sense
	to call this method from here.
	*/
	adjustAppointments();
    }
}