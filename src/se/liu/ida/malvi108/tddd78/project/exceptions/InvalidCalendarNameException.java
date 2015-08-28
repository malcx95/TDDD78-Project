package se.liu.ida.malvi108.tddd78.project.exceptions;

/**
 * Thrown when a calendar name is too long or too short.
 */
public class InvalidCalendarNameException extends Exception
{
    private final String name;
    private final int type;
    /**
     * Sent as the type parameter when the name is too long.
     */
    public final static int NAME_TOO_LONG = 1;
    /**
     * Sent as the type parameter when the name is too short.
     */
    public final static int NAME_TOO_SHORT = 2;

    /**
     * Creates an <code>InvalidCalendarNameException</code>.
     * @param name The name of the invalid calendar name.
     * @param type What was wrong with the name: InvalidCalendarNameException.NAME_TOO_LONG
     *             if it's too long or NAME_TOO_SHORT if it's too short.
     */
    public InvalidCalendarNameException(String name, int type){
	this.name = name;
	this.type = type;
    }

    public int getType() {
	return type;
    }

    public String getName(){
	return name;
    }
}
