package fr.julienvermet.bugdroid.database;

import java.io.Serializable;

import fr.julienvermet.bugdroid.users.User;

public class Group implements Serializable{
	
	protected int id;
	protected String name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}