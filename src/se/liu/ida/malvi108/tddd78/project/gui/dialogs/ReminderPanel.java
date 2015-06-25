package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

import se.liu.ida.malvi108.tddd78.project.reminders.Reminder;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Panel for inputing reminders in the <code>AppointmentDialog</code>.
 * Is it's own class to redistribute code from the <code>AppointmentDialog</code> class.
 */
class ReminderPanel extends JPanel
{
    private final static Icon PLAY = new ImageIcon(ReminderPanel.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/play.png"));
    private static final int ONE_WEEK = 7;
    private static final int TWO_WEEKS = 14;
    private static final int FOUR_WEEKS = 28;
    private JComboBox<ReminderTimeOption> timeBox;
    private RingtoneSelector ringtoneSelector;
    private boolean wholeDay;
    private final JCheckBox remindBox;
    private JButton playButton;


    ReminderPanel(boolean wholeDay){
	super(new BorderLayout());
	this.wholeDay = wholeDay;
	remindBox = new JCheckBox("Påminn mig");
	addRemindBoxListener();
	add(remindBox, BorderLayout.NORTH);
	timeBox = null;
	ringtoneSelector = new RingtoneSelector();
	ringtoneSelector.setEnabled(remindBox.isSelected());
	setWholeDay(wholeDay);
	addRingtoneSelector();
	setCorrectPlayButtonState();
    }

    private void setCorrectPlayButtonState() {
	playButton.setEnabled(ringtoneSelector.getSelectedItem() != RingToneOption.NONE &&
				remindBox.isSelected());
    }

    private void addRingtoneSelector() {
	JPanel panel = new JPanel();
	panel.add(ringtoneSelector);
	ringtoneSelector.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		setCorrectPlayButtonState();
	    }
	});
	playButton = new JButton(PLAY);
	playButton.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		AudioClip ringtone = ringtoneSelector.getSelectedRingtone();
		assert ringtone != null : "Internal error: ringtone is null";
		ringtone.play();
	    }
	});
	panel.add(playButton);
	add(panel, BorderLayout.SOUTH);
    }

    void setWholeDay(boolean wholeDay){
	this.wholeDay = wholeDay;
	if (timeBox != null) {
	    remove(timeBox);
	}
	if (wholeDay){
	    timeBox = new JComboBox<>((ReminderTimeOption[]) WholeDayReminderTimeOption.values());
	} else {
	    timeBox = new JComboBox<>((ReminderTimeOption[]) StandardReminderTimeOption.values());
	}
	timeBox.setEnabled(remindBox.isSelected());
	add(timeBox, BorderLayout.CENTER);
	revalidate();
    }

    /**
     * Creates a reminder based on the current setting of the <code>timeBox</code> as
     * well as the given reference time and date.
     * @param refDate The date at which the appointment to be reminded about is scheduled.
     * @param refTime The time at which the appointment to be reminded about is scheduled.
     */
    Reminder getReminder(Date refDate, TimePoint refTime, String title, String text){
	AudioClip ringtone = ringtoneSelector.getSelectedRingtone();
	if (wholeDay){
	    return getWholeDayReminder(refDate, ringtone, title, text);
	} else {
	    return getStandardReminder(refDate, refTime, ringtone, title, text);
	}
    }

    private Reminder getStandardReminder(final Date refDate, final TimePoint refTime, final AudioClip ringtone,
					 final String title, final String text) {
	switch ((StandardReminderTimeOption) timeBox.getSelectedItem()){
	    case ON_TIME:
		return new Reminder(refDate, refTime, ringtone, title, text);
	    case FIVE_MINUTES:
		return createAppropriateReminder(5, refDate, refTime, ringtone, title, text);
	    case TEN_MINUTES:
		return createAppropriateReminder(10, refDate, refTime, ringtone, title, text);
	    case FIFTEEN_MINUTES:
		return createAppropriateReminder(15, refDate, refTime, ringtone, title, text);
	    case THIRTY_MINUTES:
		return createAppropriateReminder(30, refDate, refTime, ringtone, title, text);
	    case ONE_HOUR:
		return createAppropriateReminderHour(1, refDate, refTime, ringtone, title, text);
	    case TWO_HOURS:
		return createAppropriateReminderHour(2, refDate, refTime, ringtone, title, text);
	    case SIX_HOURS:
		return createAppropriateReminderHour(6, refDate, refTime, ringtone, title, text);
	    case ONE_DAY:
		return new Reminder(refDate.getPreviousDay(), refTime, ringtone, title, text);
	    case TWO_DAYS:
		return new Reminder(refDate.getDaysBefore(2), refTime, ringtone, title, text);
	    case ONE_WEEK:
		return new Reminder(refDate.getDaysBefore(ONE_WEEK), refTime, ringtone, title, text);
	    case TWO_WEEKS:
		return new Reminder(refDate.getDaysBefore(TWO_WEEKS), refTime, ringtone, title, text);
	    default:
		return null;
	}
    }

    private Reminder getWholeDayReminder(final Date refDate, final AudioClip ringtone,
					 final String title, final String text) {
	switch ((WholeDayReminderTimeOption) timeBox.getSelectedItem()){
	    case ON_TIME:
		return new Reminder(refDate, null, ringtone, title, text);
	    case ONE_DAY:
		return new Reminder(refDate.getPreviousDay(), null, ringtone, title, text);
	    case TWO_DAYS:
		return new Reminder(refDate.getDaysBefore(2), null, ringtone, title, text);
	    case THREE_DAYS:
		return new Reminder(refDate.getDaysBefore(3), null, ringtone, title, text);
	    case ONE_WEEK:
		return new Reminder(refDate.getDaysBefore(ONE_WEEK), null, ringtone, title, text);
	    case TWO_WEEKS:
		return new Reminder(refDate.getDaysBefore(TWO_WEEKS), null, ringtone, title, text);
	    case FOUR_WEEKS:
		return new Reminder(refDate.getDaysBefore(FOUR_WEEKS), null, ringtone, title, text);
	    default:
		return null;
	}
    }

    private Reminder createAppropriateReminderHour(int decrement, Date refdate, TimePoint refTime, AudioClip ringtone,
						   String title, String text){
	if (refTime.canDecrementHour(decrement)){
	    refdate = refdate.getPreviousDay();
	}
	return new Reminder(refdate, refTime, ringtone, title, text);
    }

    private Reminder createAppropriateReminder(int decrement, Date refDate, TimePoint refTime, AudioClip ringtone,
					       String title, String text){
	//TODO se till så att man inte kan skapa ett datum innan 2007 såhär
	//t ex genom att göra så att man inte kan skapa påminnelser för förflutna aktiviteter
	if (refTime.canDecrement(decrement)){
	    refDate = refDate.getPreviousDay();
	}
	return new Reminder(refDate, refTime, ringtone, title, text);
    }

    private void addRemindBoxListener() {
	remindBox.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		boolean selected = remindBox.isSelected();
		timeBox.setEnabled(selected);
		ringtoneSelector.setEnabled(selected);
		setCorrectPlayButtonState();
	    }
	});
    }
}
