package se.liu.ida.malvi108.tddd78.project.todo_list;

import se.liu.ida.malvi108.tddd78.project.databases.ToDoListDatabase;

import java.io.Serializable;

/**
 * An entry in a ToDoList
 */
public class ToDoListEntry implements Serializable
{
    private String subject;
    private boolean done;

    public ToDoListEntry(String subject){
	this.subject = subject;
	done = false;
    }

    public void setDone(final boolean done){
        this.done = done;
        ToDoListDatabase.getInstance().tryToSaveChanges();
    }

    public boolean isDone(){
	return done;
    }

    public String getSubject(){
	return subject;
    }

    @Override public String toString() {
        return subject;
    }
}
