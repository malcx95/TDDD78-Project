package se.liu.ida.malvi108.tddd78.project.reminders;

import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;
import se.liu.ida.malvi108.tddd78.project.miscellaneous.Utilities;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;


import javax.swing.*;
import java.applet.AudioClip;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Reminder
{

    private final static Icon REMINDER_ICON = new ImageIcon(Reminder.class.getResource("/se/liu/ida/malvi108/tddd78/project/images/reminder.png"));
    private Timer timer;

    /**
     * Creates a reminder with the given parameters. The timepoint can be null, in which case it is set to 9:00AM.
     * If the ringtone is null, no ringtone is played when the user is reminded. The date must not be null.
     *
     * @param date The date at which the reminder should be shown.
     * @param time The time of the day at which the reminder should be shown.
     * @param ringtone The ringtone played by the reminder.
     */
    public Reminder(Date date, TimePoint time, AudioClip ringtone, String subject, String relTime){
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null!");
        }
        if (time == null){
            time = new TimePoint(9, 0);
        }
        timer = new Timer();
        createTimerTask(date, time, ringtone, subject, relTime);
    }

    private void createTimerTask(Date date, TimePoint time, final AudioClip ringtone, final String subject, final String relTime) {
        Calendar calendar = Calendar.getInstance(); //java.util.Calendar and Date need to be used as the java.util.Timer require this.
        calendar.set(date.getYear(), date.getMonth() - 1, //this works since Calendar.JANUARY is 0, FEBRUARY is 1 and so forth
                     date.getDay(), time.getHour(), time.getMinute(), 0);
        TimerTask task = new TimerTask()
        {
            @Override public void run() {
                ringtone.play();
                String reminderText = "<t1 style=\"font-size:200%\"><b>" + subject + "</b></t1><br><br>" + relTime;
                JOptionPane.showMessageDialog(null, Utilities.toHTML(reminderText), "Påminnelse: " + subject, JOptionPane.PLAIN_MESSAGE, REMINDER_ICON);
            }
        };
        timer.schedule(task, calendar.getTime());
    }

    public void cancel(){
        timer.cancel();
    }

    /*public void addReminder(Reminder reminder){

    }*/
}
