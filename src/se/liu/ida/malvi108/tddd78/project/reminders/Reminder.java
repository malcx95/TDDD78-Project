package se.liu.ida.malvi108.tddd78.project.reminders;

import se.liu.ida.malvi108.tddd78.project.gui.dialogs.ReminderTimeOption;
import se.liu.ida.malvi108.tddd78.project.gui.dialogs.Ringtone;
import se.liu.ida.malvi108.tddd78.project.listeners.ReminderListener;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;


import javax.swing.*;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A class for reminding the user of scheduled appointments. Once a <code>Reminder</code> has been created
 * it will bring up a message dialog and play a ringtone that corresponds to the given <code>Ringtone</code>.
 * <code>Reminders</code> can not be changed once they've been created, to change them you have to invoke the <code>cancel()</code>-method
 * and create a new <code>Reminder</code>.
 */
public class Reminder
{

    /*
    För att spara påminnelserna på fil, gör dem först transient i Appointment-klassen.
    Sedan lägg till ett fält boolean remind eller dylikt och se till att den serialiseras
    med aktiviteten. Spara en sträng med all information som krävs för att återskapa påminnelsen
    med Reminderns toString-metod (nej, serialisera bara alla fält), och återskapa den i samband
    med att appointmenten återskapas när filen läses.
     */


    private final static Icon REMINDER_ICON = new ImageIcon(Reminder.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/reminder.png"));
    private Timer timer;
    private Date date;
    private TimePoint time;
    private Ringtone ringtone;
    private String subject;
    private String relTime;
    private ReminderTimeOption timeOption;
    private ReminderListener listener;

    /**
     * Creates a reminder with the given parameters. The <code>time</code> can be null, in which case it is set to 9:00AM.
     * The date and ringtone must not be null, if no ringtone is to be played, use <code>Ringtone.NONE</code>. Does nothing
     * if the given <code>time</code> has already passed.
     *
     * @param date The date at which the reminder should be shown.
     * @param time The time of the day at which the reminder should be shown.
     * @param ringtone The ringtone played by the reminder.
     * @param subject The subject of the appointment the reminder is reminding about.
     * @param relTime A string containing information about when the scheduled appointment is due, for example
     *                "in five minutes" or "December 12:th".
     */
    public Reminder(Date date, TimePoint time, Ringtone ringtone, String subject,
                    String relTime, ReminderTimeOption timeOption){
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null!");
        }
        if (time == null){
            time = new TimePoint(9, 0);
        }
        this.date = date;
        this.time = time;
        this.ringtone = ringtone;
        this.subject = subject;
        this.relTime = relTime;
        this.timeOption = timeOption;
        this.listener = null;
        timer = new Timer();
        createTimerTask(date, time, ringtone, subject, relTime);
    }

    public void setReminderListener(ReminderListener reminderListener){
        this.listener = reminderListener;
    }

    private void createTimerTask(Date date, TimePoint time, final Ringtone ringtone, final String subject, final String relTime) {
        Calendar calendar = Calendar.getInstance(); //java.util.Calendar and Date need to be used as the java.util.Timer require this.
        calendar.set(date.getYear(), date.getMonth() - 1, //this works since Calendar.JANUARY is 0, FEBRUARY is 1 and so forth
                     date.getDay(), time.getHour(), time.getMinute(), 0);
        TimerTask task = new TimerTask()
        {
            @Override public void run() {
                ringtone.play();
                String reminderText = "<html><t1 style=\"font-size:200%\"><b>" + subject + "</b></t1><br><br>" + relTime;
                JOptionPane.showMessageDialog(null, reminderText, "Påminnelse - " + subject, JOptionPane.PLAIN_MESSAGE,
                                              REMINDER_ICON);
                if (listener != null) {
                    listener.reminderFired();
                }
            }
        };
        timer.schedule(task, calendar.getTime());

    }

    /**
     * Cancels the <code>Reminder</code>.
     */
    public void cancel(){
        timer.cancel();
    }

    public Date getDate() {
        return date;
    }

    public TimePoint getTime() {
        return time;
    }

    public Ringtone getRingtone() {
        return ringtone;
    }

    public String getSubject() {
        return subject;
    }

    public String getRelTime() {
        return relTime;
    }

    public ReminderTimeOption getTimeOption() {
        return timeOption;
    }

    /*private void playRingtone(RingToneOption ringtone){
	switch (ringtone){
	    case BLIP:
		BLIP.play();
                break;
	    case PLING:
		PLING.play();
                break;
	    case WHISTLE:
                WHISTLE.play();
                break;
	    case TIME_HAS_COME:
		TIME_HAS_COME.play();
                break;
            case NONE:
	    default:
                break;
	}
    }*/
}
