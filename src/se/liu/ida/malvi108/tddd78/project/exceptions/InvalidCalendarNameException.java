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
