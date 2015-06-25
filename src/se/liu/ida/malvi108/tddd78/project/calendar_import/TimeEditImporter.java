package se.liu.ida.malvi108.tddd78.project.calendar_import;

import se.liu.ida.malvi108.tddd78.project.appointments.Calendar;
import se.liu.ida.malvi108.tddd78.project.databases.CalendarDatabase;
import se.liu.ida.malvi108.tddd78.project.appointments.StandardAppointment;
import se.liu.ida.malvi108.tddd78.project.exceptions.FileCorruptedException;
import se.liu.ida.malvi108.tddd78.project.exceptions.InvalidCalendarNameException;
import se.liu.ida.malvi108.tddd78.project.miscellaneous.Utilities;
import se.liu.ida.malvi108.tddd78.project.time.Date;
import se.liu.ida.malvi108.tddd78.project.time.TimePoint;
import se.liu.ida.malvi108.tddd78.project.time.TimeSpan;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility class for importing your schedule from TimeEdit. Interprets the content
 * of the TimeEdit configFile and creates calendars and appointments
 * accordingly. Each course gets it's own calendar which is assigned a random color.
 */
public final class TimeEditImporter
{
    private final static int COURSE_BEGIN_INDEX = 14;
    private final static int COURSE_END_INDEX = 20;
    private final static int END_TIME_BEGIN_INDEX = 8;
    private final static int END_TIME_END_INDEX = 13;
    private final static int START_TIME_BEGIN_INDEX = 0;
    private final static int START_TIME_END_INDEX = 5;

    private TimeEditImporter(){}

    /**
     * Imports a TimeEdit schedule and adds it to the database. For this to work, the schedule file must be a
     * .txt file and created by the swedish or english version of TimeEdit. Only activites associated with a course
     * are imported, the rest are ignored.
     * @param file The file containing the schedule to import.
     * @throws FileCorruptedException If the file is of an unsupported format or it can't be interpreted for other reasons.
     * @throws IOException If an IO error occurs.
     * @throws FileNotFoundException If the given file couldn't be found.
     */
    public static void importFromTimeEdit(File file) throws FileCorruptedException, IOException, FileNotFoundException
    {
	try (BufferedReader reader = new BufferedReader(
			new InputStreamReader(
				new FileInputStream(file), "UTF-8"))) {
	    reader.readLine(); //skip the first line
	    reader.readLine(); //skip the second line
	    List<String> restLines = getRestLines(reader);
	    createAppointments(restLines);
	    CalendarDatabase database = CalendarDatabase.getInstance();
	    database.notifyListeners();
	    database.tryToSaveChanges();
	} catch (EOFException | UnsupportedEncodingException | NumberFormatException | StringIndexOutOfBoundsException ex) {
	    //All these exceptions suggest that something is wrong with the file
	    throw new FileCorruptedException("TimeEdit file is corrupt", ex);
	}
    }

    private static void createAppointments(final Collection<String> restLines) throws FileCorruptedException{
	if (!restLines.isEmpty()) {
	    Date date = null; //line representing the date
	    for (String line : restLines) {
		if (isDate(line)) {
		    date = interpretDate(line);
		} else if (isAppointment(line)){
		    interpretAndAddAppointment(line, date, CalendarDatabase.getInstance());
		} else {
		    throw new FileCorruptedException("Line is neither appointment nor date");
		}
	    }
	}
    }

    private static void interpretAndAddAppointment(final String line, final Date date, CalendarDatabase database)
    throws FileCorruptedException{
	TimeSpan duration = extractTimeSpan(line);
	String course = extractCourseCode(line);
	String location = getStatement(line, 1);
	String subject = course + ": " + getStatement(line, 2) + ", " + getStatement(line, 3);
	String description = extractDescription(line);
	StandardAppointment appointment = new StandardAppointment(duration, date, subject, location, description, null);
	try {
	    if (isCourseCode(course)) {
		Calendar courseCalendar;
		if (!database.contains(course)){
		    courseCalendar = new Calendar(course, Utilities.generateRandomColor());
		    database.silentlyAddCalendar(courseCalendar);
		} else {
		    courseCalendar = database.get(course);
		    assert courseCalendar != null: "Internal error, courseCalendar is null";
		}
		courseCalendar.silentlyAddStandardAppointment(appointment);
	    }
	} catch (InvalidCalendarNameException ex){
	    throw new FileCorruptedException("File corrupted, course name invalid", ex);
	}
    }

    private static String extractDescription(final String line) {
	StringBuilder builder = new StringBuilder();
	//For-loop starts at 2 so that the time, course code and location is left out.
	for (int i = 2; i < getNumberOfStatements(line); i++) {
	    String statement = getStatement(line, i);
	    if (!containsOnlySpaces(statement)){
		builder.append(statement);
		builder.append(", ");
	    }
	    else {
		break;
	    }
	}
	try {
	    builder.delete(builder.length() - 2, builder.length());
	} catch (StringIndexOutOfBoundsException ignore){
	    return "";
	}
	return builder.toString();
    }

    private static String extractCourseCode(final String line) {
	return line.substring(COURSE_BEGIN_INDEX, COURSE_END_INDEX);
    }

    private static TimeSpan extractTimeSpan(final String line) {
	TimePoint start = extractTimePoint(extractStart(line));
	TimePoint end = extractTimePoint(extractEnd(line));
	return new TimeSpan(start, end);
    }

    private static String extractEnd(final String line) {
	String end = line.substring(END_TIME_BEGIN_INDEX, END_TIME_END_INDEX);
	if (end.equals("00:00")){
	    //this is to avoid creating a TimeSpan with an end that precedes the start.
	    return "23:59";
	} else {
	    return end;
	}
    }

    private static String extractStart(final String line) {
	return line.substring(START_TIME_BEGIN_INDEX, START_TIME_END_INDEX);
    }

    private static TimePoint extractTimePoint(final String line) {
	return new TimePoint(Integer.parseInt(line.substring(0, 2)),
			     Integer.parseInt(line.substring(3, 5)));
    }

    private static boolean isAppointment(final CharSequence line) {
	return line.charAt(2) == ':' && line.charAt(5) == ' ' &&
	       line.charAt(6) == '-' && line.charAt(7) == ' ';
    }



    private static Date interpretDate(final String line) {
	String date = extractDate(line);
	return new Date(extractDay(date), extractMonth(date), extractYear(date));
    }

    private static int extractMonth(final String date) {
	return Integer.parseInt(date.substring(5, 7));
    }

    private static int extractDay(final String date) {
	return Integer.parseInt(date.substring(8, 10));
    }

    private static int extractYear(final String date) {
	return Integer.parseInt(date.substring(0, 4));
    }

    private static boolean isDate(final String line) {
	String date = extractDate(line); //extracts the actual date from the line, so it works independantly
					// of the schedule being in swedish or english.
	return date.charAt(4) == '-' && date.charAt(7) == '-'; //2015-04-19...
    }

    /**
     * Extracts the date ("2015-04-19") from the line. Works independently of whether it was written in swedish or english.
     */
    private static String extractDate(String line){
	return removeSpacesBefore(line.substring(3));
    }

    /**
     * Gets an array of the lines in the file.
     */
    private static List<String> getRestLines(final BufferedReader reader) throws IOException{
	boolean continueAdding = true;
	List<String> result = new ArrayList<>();
	while (continueAdding){
	    String line = reader.readLine();
	    if (line != null){
		result.add(removeSpacesBefore(line));
	    }else {
		continueAdding = false;
	    }
	}
	return result;
    }

    private static boolean isCourseCode(final String statement) {
	String s = removeSpacesBefore(statement);
	return s.length() == 6 && endsWithNumbers(statement);
    }

    private static boolean endsWithNumbers(String s){
	return s.endsWith("0") || s.endsWith("1") || s.endsWith("2") || s.endsWith("3") ||
	       s.endsWith("4") || s.endsWith("5") || s.endsWith("6") || s.endsWith("7") ||
	       s.endsWith("8") || s.endsWith("9");
    }

    /**
     * Removes the first blank space of a string, if there is one.
     */
    private static String removeFirstSpace(String str){
	if (str.charAt(0) == ' ') return str.substring(1);
	return str;
    }

    /**
     * Removes all spaces at the start of a string.
     */
    private static String removeSpacesBefore(String str) {
	if (containsOnlySpaces(str)){
	    return "";
	}
	while (true) {
	    if (str.charAt(0) != ' ') return str;
	    else {
		str = removeFirstSpace(str);
	    }
	}
    }

    private static boolean containsOnlySpaces(final String str) {
	for (char c : str.toCharArray()) {
	    if (c != ' ') return false;
	}
	return true;
    }

    /**
     * Gets the statement (content between each comma) of the given line.
     * @param line The line you want to scan.
     * @param order The order (first statement (order = 0), second statement (order = 1)...).
     */
    private static String getStatement(CharSequence line, int order){
	int start = 0; //the position in the line to start from.
	int commas = 0;
	while (commas < order){
	    if (line.charAt(start) == ','){
		commas++;
	    }
	    start++;
	}
	//now the start value represents the index in the line to start reading from.
	StringBuilder builder = new StringBuilder();
	for (int i = start; i < line.length(); i++) {
	    char c = line.charAt(i);
	    if (c != ',' && c != '\n') builder.append(c);
	    else break; //if it encounters another comma or linebreak
	}
	return removeSpacesBefore(builder.toString());
    }

    private static int getNumberOfStatements(String line){
	int result = 1; // there is always at least one statement on the line
	for (char c : line.toCharArray()) {
	    if (c == ',') result++;
	}
	return result;
    }
}
