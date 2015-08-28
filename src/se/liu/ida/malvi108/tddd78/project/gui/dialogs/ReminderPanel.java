package se.liu.ida.malvi108.tddd78.project.gui.dialogs;

import se.liu.ida.malvi108.tddd78.project.reminders.Reminder;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;

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
    private static final int FIVE_MINUTES = 5;
    private static final int TEN_MINUTES = 10;
    private static final int FIFTEEN_MINUTES = 15;
    private static final int THIRTY_MINUTES = 30;
    private JComboBox<ReminderTimeOption> timeBox;
    private JComboBox<Ringtone> ringtoneSelector;
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
	ringtoneSelector = new JComboBox<>(Ringtone.values());
	setWholeDay(wholeDay);
	addRingtoneSelector();
	ringtoneSelector.setEnabled(remindBox.isSelected() && remindBox.isEnabled());
	setCorrectPlayButtonState();
    }

    public void setCorrectPlayButtonState() {
	playButton.setEnabled(ringtoneSelector.getSelectedItem() != Ringtone.NONE && remindBox.isSelected() && remindBox.isEnabled());
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
		Ringtone ringtone = (Ringtone) ringtoneSelector.getSelectedItem();
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
	timeBox.setEnabled(remindBox.isSelected() && remindBox.isEnabled());
	add(timeBox, BorderLayout.CENTER);
	revalidate();
    }

    /**
     * Creates a reminder based on the current setting of the <code>timeBox</code> as
     * well as the given reference time and date. Returns <code>null</code> if the remindbox is
     * deselected
     * @param refDate The date at which the appointment to be reminded about is scheduled.
     * @param refTime The time at which the appointment to be reminded about is scheduled.
     */
    Reminder getReminder(Date refDate, TimePoint refTime, String subject){
	if (remindBox.isSelected()) {
	    Ringtone ringtone = (Ringtone) ringtoneSelector.getSelectedItem();
	    if (wholeDay) {
		return getWholeDayReminder(refDate, ringtone, subject);
	    } else {
		return getStandardReminder(refDate, refTime, ringtone, subject);
	    }
	} else {
	    return null;
	}
    }

    private Reminder getStandardReminder(final Date refDate, final TimePoint refTime, final Ringtone ringtone,
					 final String subject) {
	StandardReminderTimeOption option = (StandardReminderTimeOption) timeBox.getSelectedItem();
	switch (option){
	    case ON_TIME:
		return new Reminder(refDate, refTime, ringtone, subject, refTime + "<i> - nu </i>", option);
	    case FIVE_MINUTES:
		return createAppropriateReminder(FIVE_MINUTES, refDate, refTime, ringtone, subject, option);
	    case TEN_MINUTES:
		return createAppropriateReminder(TEN_MINUTES, refDate, refTime, ringtone, subject, option);
	    case FIFTEEN_MINUTES:
		return createAppropriateReminder(FIFTEEN_MINUTES, refDate, refTime, ringtone, subject, option);
	    case THIRTY_MINUTES:
		return createAppropriateReminder(THIRTY_MINUTES, refDate, refTime, ringtone, subject, option);
	    case ONE_HOUR:
		return createAppropriateReminderHour(1, refDate, refTime, ringtone, subject, option);
	    case TWO_HOURS:
		return createAppropriateReminderHour(2, refDate, refTime, ringtone, subject, option);
	    case SIX_HOURS:
		return createAppropriateReminderHour(6, refDate, refTime, ringtone, subject, option);
	    case ONE_DAY:
		return new Reminder(refDate.getPreviousDay(), refTime, ringtone, subject, "I morgon kl " + refTime, option);
	    case TWO_DAYS:
		return new Reminder(refDate.getDaysBefore(2), refTime, ringtone, subject, refDate + ", kl " + refTime + " - <i> om två dagar </i>", option);
	    case ONE_WEEK:
		return new Reminder(refDate.getDaysBefore(ONE_WEEK), refTime, ringtone, subject, refDate + ", kl " + refTime + " - <i> om en vecka </i>", option);
	    case TWO_WEEKS:
		return new Reminder(refDate.getDaysBefore(TWO_WEEKS), refTime, ringtone, subject, refDate + ", kl " + refTime + " - <i> om två veckor </i>", option);
	    default:
		return null;
	}
    }

    private Reminder getWholeDayReminder(final Date refDate, final Ringtone ringtone,
					 final String title) {
	WholeDayReminderTimeOption option = (WholeDayReminderTimeOption) timeBox.getSelectedItem();
	switch (option){
	    case ON_TIME:
		return new Reminder(refDate, null, ringtone, title, refDate + " - <i> idag </i>", option);
	    case ONE_DAY:
		return new Reminder(refDate.getPreviousDay(), null, ringtone, title, refDate + " - <i> i morgon </i>", option);
	    case TWO_DAYS:
		return new Reminder(refDate.getDaysBefore(2), null, ringtone, title, refDate + " - <i> om två dagar </i>", option);
	    case THREE_DAYS:
		return new Reminder(refDate.getDaysBefore(3), null, ringtone, title, refDate + " - <i> om tre dagar </i>", option);
	    case ONE_WEEK:
		return new Reminder(refDate.getDaysBefore(ONE_WEEK), null, ringtone, title, refDate + " - <i> om en vecka </i>", option);
	    case TWO_WEEKS:
		return new Reminder(refDate.getDaysBefore(TWO_WEEKS), null, ringtone, title, refDate + " - <i> om två veckor </i>", option);
	    case FOUR_WEEKS:
		return new Reminder(refDate.getDaysBefore(FOUR_WEEKS), null, ringtone, title, refDate + " - <i> om fyra veckor </i>", option);
	    default:
		return null;
	}
    }

    private Reminder createAppropriateReminderHour(int decrement, Date refdate, TimePoint refTime, Ringtone ringtone,
						   String subject, StandardReminderTimeOption option){
	if (refTime.canDecrementHour(decrement)){
	    refdate = refdate.getPreviousDay();
	}
	String hourText;
	if (decrement == 1){
	    hourText = " timme";
	} else {
	    hourText = " timmar";
	}
	return new Reminder(refdate, refTime.getDecrementedHours(decrement), ringtone, subject, refTime + " - <i> om " + decrement + hourText + "</i>", option);
    }

    private Reminder createAppropriateReminder(int decrement, Date refDate, TimePoint refTime, Ringtone ringtone,
					       String subject, StandardReminderTimeOption option){
	if (refTime.canDecrement(decrement)){
	    refDate = refDate.getPreviousDay();
	}
	TimePoint dec = refTime.getDecremented(decrement);
	System.out.println(dec);
	return new Reminder(refDate, refTime.getDecremented(decrement), ringtone, subject, refTime + " - <i> om " + decrement + " minuter</i>", option);
    }

    private void addRemindBoxListener() {
	remindBox.addActionListener(new ActionListener()
	{
	    @Override public void actionPerformed(final ActionEvent e) {
		boolean selected = remindBox.isSelected() && remindBox.isEnabled();
		timeBox.setEnabled(selected);
		ringtoneSelector.setEnabled(selected);
		setCorrectPlayButtonState();
	    }
	});
    }

    public void setRemindBox(boolean selected){
	remindBox.setSelected(selected);
	timeBox.setEnabled(selected);
	ringtoneSelector.setEnabled(selected);
    }

    public void setRingtone(Ringtone ringtone){
	if (ringtone == null) {
	    ringtoneSelector.setSelectedItem(Ringtone.NONE);
	} else {
	    ringtoneSelector.setSelectedItem(ringtone);
	}
    }

    @Override public void setEnabled(final boolean enabled) {
	super.setEnabled(enabled);
	remindBox.setEnabled(enabled);
	ringtoneSelector.setEnabled(enabled && remindBox.isSelected());
	timeBox.setEnabled(enabled && remindBox.isSelected());
    }

    public void setReminderTime(ReminderTimeOption option){
	timeBox.setSelectedItem(option);
    }
}
