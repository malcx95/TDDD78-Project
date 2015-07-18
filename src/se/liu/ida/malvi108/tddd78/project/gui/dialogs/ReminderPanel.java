package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

import se.liu.ida.malvi108.tddd78.project.reminders.Reminder;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;

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
    Reminder getReminder(Date refDate, TimePoint refTime, String subject){
	AudioClip ringtone = ringtoneSelector.getSelectedRingtone();
	if (wholeDay){
	    return getWholeDayReminder(refDate, ringtone, subject);
	} else {
	    return getStandardReminder(refDate, refTime, ringtone, subject);
	}
    }

    private Reminder getStandardReminder(final Date refDate, final TimePoint refTime, final AudioClip ringtone,
					 final String subject) {
	switch ((StandardReminderTimeOption) timeBox.getSelectedItem()){
	    case ON_TIME:
		return new Reminder(refDate, refTime, ringtone, subject, refTime + "<i> - nu</i>");
	    case FIVE_MINUTES:
		return createAppropriateReminder(5, refDate, refTime, ringtone, subject);
	    case TEN_MINUTES:
		return createAppropriateReminder(10, refDate, refTime, ringtone, subject);
	    case FIFTEEN_MINUTES:
		return createAppropriateReminder(15, refDate, refTime, ringtone, subject);
	    case THIRTY_MINUTES:
		return createAppropriateReminder(30, refDate, refTime, ringtone, subject);
	    case ONE_HOUR:
		return createAppropriateReminderHour(1, refDate, refTime, ringtone, subject);
	    case TWO_HOURS:
		return createAppropriateReminderHour(2, refDate, refTime, ringtone, subject);
	    case SIX_HOURS:
		return createAppropriateReminderHour(6, refDate, refTime, ringtone, subject);
	    case ONE_DAY:
		return new Reminder(refDate.getPreviousDay(), refTime, ringtone, subject, "I morgon kl " + refTime);
	    case TWO_DAYS:
		return new Reminder(refDate.getDaysBefore(2), refTime, ringtone, subject, refDate + ", kl " + refTime + " - <i> om två dagar</i>");
	    case ONE_WEEK:
		return new Reminder(refDate.getDaysBefore(ONE_WEEK), refTime, ringtone, subject, refDate + ", kl " + refTime + " - <i> om en vecka</i>");
	    case TWO_WEEKS:
		return new Reminder(refDate.getDaysBefore(TWO_WEEKS), refTime, ringtone, subject, refDate + ", kl " + refTime + " - <i> om två veckor</i>");
	    default:
		return null;
	}
    }

    private Reminder getWholeDayReminder(final Date refDate, final AudioClip ringtone,
					 final String title) {
	switch ((WholeDayReminderTimeOption) timeBox.getSelectedItem()){
	    case ON_TIME:
		return new Reminder(refDate, null, ringtone, title, refDate + " - <i> idag</i>");
	    case ONE_DAY:
		return new Reminder(refDate.getPreviousDay(), null, ringtone, title, refDate + " - <i> i morgon</i>");
	    case TWO_DAYS:
		return new Reminder(refDate.getDaysBefore(2), null, ringtone, title, refDate + " - <i> om två dagar</i>");
	    case THREE_DAYS:
		return new Reminder(refDate.getDaysBefore(3), null, ringtone, title, refDate + " - <i> om tre dagar</i>");
	    case ONE_WEEK:
		return new Reminder(refDate.getDaysBefore(ONE_WEEK), null, ringtone, title, refDate + " - <i> om en vecka</i>");
	    case TWO_WEEKS:
		return new Reminder(refDate.getDaysBefore(TWO_WEEKS), null, ringtone, title, refDate + " - <i> om två veckor</i>");
	    case FOUR_WEEKS:
		return new Reminder(refDate.getDaysBefore(FOUR_WEEKS), null, ringtone, title, refDate + " - <i> om fyra veckor</i>");
	    default:
		return null;
	}
    }

    private Reminder createAppropriateReminderHour(int decrement, Date refdate, TimePoint refTime, AudioClip ringtone,
						   String subject){
	if (refTime.canDecrementHour(decrement)){
	    refdate = refdate.getPreviousDay();
	}
	return new Reminder(refdate, refTime, ringtone, subject, refTime + " - <i> om " + decrement + " timmar</i>");
    }

    private Reminder createAppropriateReminder(int decrement, Date refDate, TimePoint refTime, AudioClip ringtone,
					       String subject){
	//TODO se till så att man inte kan skapa ett datum innan 2007 såhär
	//t ex genom att göra så att man inte kan skapa påminnelser för förflutna aktiviteter
	if (refTime.canDecrement(decrement)){
	    refDate = refDate.getPreviousDay();
	}
	return new Reminder(refDate, refTime.getDecremented(decrement), ringtone, subject, refTime + " - <i> om " + decrement + " minuter</i>");
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
