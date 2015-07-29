package se.liu.ida.malvi108.tddd78.project.gui.miscellaneous;

import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.Month;
import se.liu.ida.malvi108.tddd78.project.time.MonthName;
import se.liu.ida.malvi108.tddd78.project.time.Year;

import javax.swing.*;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * JPanel that holds a three JSpinners for selecting the year, month and day of a date.
 */
public final class DateSelector extends JPanel
{
    private JSpinner yearSpinner;
    private JSpinner monthSpinner;
    private JSpinner daySpinner;
    private static final int MONTH_SELECTOR_WIDTH = 100;
    private static final int MAXIMUM_NUMBER_OF_DAYS = 31;
    private static final int MAX_YEAR = 3000;

    public DateSelector() {
	Date today = Date.getToday();

	yearSpinner = new JSpinner();
	monthSpinner = new JSpinner();
	daySpinner = new JSpinner();

	this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	yearSpinner.setModel(new SpinnerNumberModel(today.getYear(), Year.STARTING_YEAR, MAX_YEAR, 1));
	yearSpinner.setEditor(new NumberEditor(yearSpinner, "#"));
	monthSpinner.setModel(new SpinnerListModel(MonthName.values()));
	monthSpinner.setValue(today.getMonthName());
	monthSpinner.setPreferredSize(new Dimension(MONTH_SELECTOR_WIDTH, monthSpinner.getHeight()));
	daySpinner.setModel(new SpinnerNumberModel(today.getDay(), 1, MAXIMUM_NUMBER_OF_DAYS, 1));
	this.add(yearSpinner);
	this.add(monthSpinner);
	this.add(daySpinner);
	addDateListener();
	setCorrectDate(); //so that the day number can't be a value that is too high for the selected month
    }

    private void setCorrectDate() {
	int monthNumber = MonthName.getMonthNumber((MonthName) monthSpinner.getValue());
	int yearNumber = (int) yearSpinner.getValue();
	int maxDays = Month.getMonthLength(monthNumber, Year.isLeapYear(yearNumber));
	SpinnerNumberModel model = (SpinnerNumberModel) daySpinner.getModel();
	model.setMaximum(maxDays);
	int selectedDay = (int) daySpinner.getValue();
	if (selectedDay > maxDays){
	    model.setValue(maxDays);
	}
    }

    public void setDate(Date date){
	yearSpinner.setValue(date.getYear());
	monthSpinner.setValue(MonthName.values()[date.getMonth() - 1]);
	daySpinner.setValue(date.getDay());
    }

    public Date getSelectedDate(){
	return new Date((int) daySpinner.getValue(),
			MonthName.getMonthNumber((MonthName) monthSpinner.getValue()),
			(int) yearSpinner.getValue());
    }

    private void addDateListener() {
	ChangeListener dateListener = new ChangeListener()
	{
	    @Override public void stateChanged(final ChangeEvent e) {
		setCorrectDate();
	    }
	};
	yearSpinner.addChangeListener(dateListener);
	monthSpinner.addChangeListener(dateListener);
    }
}
