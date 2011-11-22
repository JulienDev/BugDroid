package fr.julienvermet.bugdroid.database;

import java.io.Serializable;

import fr.julienvermet.bugdroid.users.User;

public class Flag implements Serializable{
	
	protected int id;
	protected String name;
	protected String requestee;
	protected User setter;
	protected String status;
	protected int typeId;
	protected int bugId;
	protected int attachmentId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public String getName() {
		return name;
	}
	public User getSetter() {
		return setter;
	}
	public String getRequestee() {
		return requestee;
	}
	public int getBugId() {
		return bugId;
	}
	public int getAttachmentId() {
		return attachmentId;
	}
	public Flag(int id, String name, String requestee, User setter,
			String status, int typeId, int bugId, int attachmentId) {
		super();
		this.id = id;
		this.name = name;
		this.requestee = requestee;
		this.setter = setter;
		this.status = status;
		this.typeId = typeId;
		this.bugId = bugId;
		this.attachmentId = attachmentId;
	}
}