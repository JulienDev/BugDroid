package fr.julienvermet.bugdroid.users;

import java.io.Serializable;

public class User implements Serializable{

	protected String email;
	protected int id;
	protected String name;
	protected String realName;
	protected String ref;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
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
	public String getRealName() {
		return realName;
	}
	public String getRef() {
		return ref;
	}
	public User(String name, String realName) {
		super();
		this.name = name;
		this.realName = realName;
	}
	public User(String name) {
		super();
		this.name = name;
	}
	
}