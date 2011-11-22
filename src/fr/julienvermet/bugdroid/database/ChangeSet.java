package fr.julienvermet.bugdroid.database;

import java.io.Serializable;
import java.util.ArrayList;

import fr.julienvermet.bugdroid.users.User;

public class ChangeSet implements Serializable{

	protected User changer;
	protected ArrayList<Change> changes;
	protected String changeTime; //TODO : Timestamp
	
	public User getChanger() {
		return changer;
	}
	public void setChanger(User changer) {
		this.changer = changer;
	}
	public ArrayList<Change> getChanges() {
		return changes;
	}
	public void setChanges(ArrayList<Change> changes) {
		this.changes = changes;
	}
	public String getChangeTime() {
		return changeTime;
	}
	public void setChangeTime(String changeTime) {
		this.changeTime = changeTime;
	}
}