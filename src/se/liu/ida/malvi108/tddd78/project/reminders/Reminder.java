package se.liu.ida.malvi108.tddd78.project.reminders;

import se.liu.ida.malvi108.tddd78.project.gui.main_gui.MainFrame;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;

import javax.swing.*;
import java.applet.AudioClip;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Reminder implements Serializable
{

    private Timer timer;

    /**
     * Creates a reminder with the given parameters. The timepoint can be null, in which case it is set to 9:00AM.
     * If the ringtone is null, no ringtone is played when the user is reminded. The date must not be null.
     *
     * @param date The date at which the reminder should be shown.
     * @param time The time of the day at which the reminder should be shown.
     * @param ringtone The ringtone played by
     */
    public Reminder(Date date, TimePoint time, AudioClip ringtone, String title, String text){
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null!");
        }
        if (time == null){
            time = new TimePoint(9, 0);
        }
        timer = new Timer();
        createTimerTask(date, time, ringtone, "title", "text");
    }

    private void createTimerTask(Date date, TimePoint time, final AudioClip ringtone, final String title, final String text) {
        Calendar calendar = Calendar.getInstance(); //java.util.Calendar and Date need to be used as the java.util.Timer require this.
        calendar.set(date.getYear(), date.getMonth() - 1, //this works since Calendar.JANUARY is 0, FEBRUARY is 1 and so forth
                     date.getDay(), time.getHour(), time.getMinute());
        TimerTask task = new TimerTask()
        {
            @Override public void run() {
                ringtone.play();
                JOptionPane.showMessageDialog(null, text, title, JOptionPane.PLAIN_MESSAGE, MainFrame.PLAN_IT_ICON);
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
