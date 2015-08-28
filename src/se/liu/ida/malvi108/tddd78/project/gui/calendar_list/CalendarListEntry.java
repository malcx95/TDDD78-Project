package se.liu.ida.malvi108.tddd78.project.gui.calendar_list;

import se.liu.ida.malvi108.tddd78.project.appointments.Calendar;
import se.liu.ida.malvi108.tddd78.project.gui.miscellaneous.GraphicCircle;
import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The graphical representation of a calendar in the <code>CalendarListPanel</code>.
 * Contains the calendar it's representing, a checkbox for enabling or disabling the calendar
 * as well as a <code>GraphicCircle</code> for displaying the color.
 *
 * @see CalendarListPanel
 */
public class CalendarListEntry extends JComponent implements ActionListener
{
    private final static int TEXT_SIZE = 14;
    private final JCheckBox checkBox;
    private Calendar calendar;

    /**
     * Creates a <code>CalendarListEntry</code>
     * @param calendar The calendar to create an entry for.
     */
    public CalendarListEntry(Calendar calendar){
	this.calendar = calendar;
	this.setLayout(new BorderLayout());
	checkBox = new JCheckBox(calendar.getName());
	checkBox.setFont(new Font(MainFrame.GLOBAL_FONT, Font.PLAIN, TEXT_SIZE));
	final GraphicCircle circle = new GraphicCircle(calendar.getColor(), 10);
	circle.setPositionY(1);
	this.add(circle, BorderLayout.WEST);
	this.add(checkBox, BorderLayout.CENTER);
	checkBox.setVerticalAlignment(SwingConstants.CENTER);
	checkBox.addActionListener(this);
	this.setMaximumSize(new Dimension(getPreferredSize().width, checkBox.getPreferredSize().height));
	if (calendar.isEnabled()){
	    checkBox.setSelected(true);
	} else {
	    checkBox.setSelected(false);
	}
    }

    public Calendar getCalendar() {
	return calendar;
    }

    /**
     * Invoked when someone clicks on the checkbox.
     */
    @Override public void actionPerformed(final ActionEvent e) {
	calendar.setEnabled(checkBox.isSelected());
    }
}
