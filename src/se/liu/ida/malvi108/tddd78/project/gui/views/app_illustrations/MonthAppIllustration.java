package se.liu.ida.malvi108.tddd78.project.gui.views.app_illustrations;

import se.liu.ida.malvi108.tddd78.project.appointments.Appointment;
import se.liu.ida.malvi108.tddd78.project.appointments.StandardAppointment;
import se.liu.ida.malvi108.tddd78.project.gui.miscellaneous.GraphicCircle;
import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * The illustration of an appointment in the MonthView
 */
public class MonthAppIllustration extends AppointmentIllustration
{
    /**
     * The height of each MonthAppIllustration.
     */
    public static final int MONTH_APP_HEIGHT = 20;
    private GraphicCircle circle;
    private JLabel timeLabel;
    private final static int FONT_SIZE = 12;

    public MonthAppIllustration(final Appointment appointment, final Color color) {
	super(appointment, color);
	if (appointment instanceof StandardAppointment){
	    timeLabel = new JLabel(((StandardAppointment) appointment).getDuration().getStart().toString());
	} else {
	    timeLabel = new JLabel("Heldag");
	}
	Font font = new Font(MainFrame.GLOBAL_FONT, Font.PLAIN, FONT_SIZE);
	timeLabel.setFont(font);
	timeLabel.setForeground(Color.GRAY);
	subjectLabel.setFont(font);
	subjectLabel.setText(' ' + subject + ' '); //so that the subject is separated from the time and the circle
	setLayout(new BorderLayout());
	circle = new GraphicCircle(color, 8);
	circle.setPositionY(1);
	add(circle, BorderLayout.LINE_START);
	add(subjectLabel, BorderLayout.CENTER);
	add(timeLabel, BorderLayout.LINE_END);
	Dimension size = new Dimension(getPreferredSize().width, MONTH_APP_HEIGHT);
	setMaximumSize(size);
	setPreferredSize(size);
	subjectLabel.setMinimumSize(new Dimension(0, 0));
    }

    @Override public void setSelected(final boolean selected) {
	super.setSelected(selected);
	if (selected){
	    timeLabel.setForeground(Color.WHITE);
	    subjectLabel.setForeground(Color.WHITE);
	    circle.setForeground(Color.WHITE);
	} else {
	    timeLabel.setForeground(Color.GRAY);
	    subjectLabel.setForeground(Color.BLACK);
	    circle.setForeground(color);
	}
	repaint();
    }

    @Override protected void paintComponent(final Graphics g) {
	super.paintComponent(g);
	if (selected){
	    final Graphics2D g2d = (Graphics2D) g;
	    g2d.setColor(color);
	    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, LESS_TRANSPARENT));
	    g2d.fillRect(0, 0, getWidth(), getHeight());
	}
    }
}
