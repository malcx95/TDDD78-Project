package se.liu.ida.malvi108.tddd78.project.databases;

import se.liu.ida.malvi108.tddd78.project.listeners.DatabaseSaveErrorListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for databases
 * @param <T> The type of object stored in the database
 */
public class Database<T>
{
    protected List<T> elements;
    /**
     * The file name of the saved database.
     */
    protected String databaseFile;
    protected List<DatabaseSaveErrorListener> errorListeners;
    protected final static Logger LOGGER = Logger.getLogger(Database.class.getName());
    private final static File SAVE_DIRECTORY = new File(System.getProperty("user.home") + "/.planitdata");

    protected Database(String databaseFile) {
        this.databaseFile = SAVE_DIRECTORY + "/" + databaseFile;
        createSaveDirectory();
	elements = new ArrayList<>();
        errorListeners = new ArrayList<>();
    }

    /**
     *
     */
    private void createSaveDirectory() {
        boolean saved = SAVE_DIRECTORY.mkdir();
        if (saved){
            LOGGER.log(Level.INFO, "Database save directory created: " + SAVE_DIRECTORY);
        } else {
            LOGGER.log(Level.INFO, "Database save directory found.");
        }
    }

    public boolean isEmpty(){
	return elements.isEmpty();
    }

    public void createDatabaseDirectory() throws IOException {
	File file = new File(databaseFile);
        createSaveDirectory();
	boolean created = file.createNewFile();
        if (created){
            LOGGER.log(Level.INFO, "Database file successfully created: " + databaseFile);
        } else {
            LOGGER.log(Level.INFO, "Database file exists, no file was created.");
        }
    }

    public void addSaveErrorListener(DatabaseSaveErrorListener listener){
        errorListeners.add(listener);
    }
}