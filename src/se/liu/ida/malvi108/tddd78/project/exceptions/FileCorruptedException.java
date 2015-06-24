package se.liu.ida.malvi108.tddd78.project.exceptions;

/**
 * Thrown when an imported file or saved database file is corrupted and can't be read.
 */
public class FileCorruptedException extends Exception
{
    public FileCorruptedException(String message){
        super(message);
    }

    public FileCorruptedException(String message, Exception cause){
	super(message, cause);
    }
}
