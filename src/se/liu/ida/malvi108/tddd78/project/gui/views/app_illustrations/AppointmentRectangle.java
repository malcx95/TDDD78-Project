package se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations;

import java.awt.*;

import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;

/**
 * General class for illustrations of appointments in the form of rectangles
 */
public class AppointmentRectangle extends AppointmentIllustration
{
    protected final static int DEFAULT_WIDTH = 100;
    protected float transparency;
    protected int width;
    protected Color darkerColor;

    protected AppointmentRectangle(Appointment appointment, Color color){
	super(appointment, color);
	this.width = DEFAULT_WIDTH;
	setLayout(new FlowLayout(FlowLayout.LEADING));
	darkerColor = color.darker();
	transparency = TRANSPARENT;
    }

    /**
     * Places the required labels onto the rectangle.
     */
    protected void placeLabels() {
	subjectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
	subjectLabel.setForeground(darkerColor);
	subjectLabel.setFont(new Font(MainFrame.GLOBAL_FONT, Font.BOLD, TEXT_SIZE));
	this.add(subjectLabel);
    }


    @Override public void setSelected(final boolean selected) {
	super.setSelected(selected);
	if (selected){
	    subjectLabel.setForeground(Color.WHITE);
	    transparency = LESS_TRANSPARENT;
	} else {
	    subjectLabel.setForeground(darkerColor);
	    transparency = TRANSPARENT;
	}
	repaint();
    }

    @Override public void setForeground(final Color fg) {
	super.setForeground(fg);
	darkerColor = color.darker();
    }

    @Override protected void paintComponent(final Graphics g) {
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g2d.setColor(color);
	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
	g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
    }
}
