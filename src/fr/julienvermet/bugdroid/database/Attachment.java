package fr.julienvermet.bugdroid.database;

import java.io.Serializable;
import java.util.ArrayList;

import fr.julienvermet.bugdroid.users.User;

public class Attachment implements Serializable {
	protected User attacher;
	protected int bugId;
	protected String bugRef;
	protected ArrayList<Comment> comments;
	protected String creationTime; //TODO : Timestamp
	protected String data;
	protected String description;
	protected String encoding;
	protected String fileName;
	protected ArrayList<Flag> flags;
	protected int id;
	protected Boolean isObsolete;
	protected Boolean isPatch;
	protected Boolean isPrivate;
	protected String ref;
	protected int size;
	protected String updateToken;
	protected String contentType;
	
	
	public ArrayList<Comment> getComments() {
		return comments;
	}
	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public ArrayList<Flag> getFlags() {
		return flags;
	}
	public void setFlags(ArrayList<Flag> flags) {
		this.flags = flags;
	}
	public Boolean getIsObsolete() {
		return isObsolete;
	}
	public void setIsObsolete(Boolean isObsolete) {
		this.isObsolete = isObsolete;
	}
	public Boolean getIsPatch() {
		return isPatch;
	}
	public void setIsPatch(Boolean isPatch) {
		this.isPatch = isPatch;
	}
	public Boolean getIsPrivate() {
		return isPrivate;
	}
	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public User getAttacher() {
		return attacher;
	}
	public int getBugId() {
		return bugId;
	}
	public String getBugRef() {
		return bugRef;
	}
	public String getCreationTime() {
		return creationTime;
	}
	public int getId() {
		return id;
	}
	public String getRef() {
		return ref;
	}
	public String getUpdateToken() {
		return updateToken;
	}
	public void setUpdateToken(String updateToken) {
		this.updateToken = updateToken;
	}
	public String getContentType() {
		return contentType;
	}
	public Attachment(User attacher, int bugId, String bugRef,
			ArrayList<Comment> comments, String creationTime, String data,
			String description, String encoding, String fileName,
			ArrayList<Flag> flags, int id, Boolean isObsolete, Boolean isPatch,
			Boolean isPrivate, String ref, int size, String updateToken,
			String contentType) {
		super();
		this.attacher = attacher;
		this.bugId = bugId;
		this.bugRef = bugRef;
		this.comments = comments;
		this.creationTime = creationTime;
		this.data = data;
		this.description = description;
		this.encoding = encoding;
		this.fileName = fileName;
		this.flags = flags;
		this.id = id;
		this.isObsolete = isObsolete;
		this.isPatch = isPatch;
		this.isPrivate = isPrivate;
		this.ref = ref;
		this.size = size;
		this.updateToken = updateToken;
		this.contentType = contentType;
	}
	public Attachment(User attacher, int bugId, String bugRef,
			String creationTime, String data, String description,
			String encoding, String fileName, ArrayList<Flag> flags, int id,
			Boolean isObsolete, Boolean isPatch, Boolean isPrivate, String ref,
			int size, String updateToken, String contentType) {
		super();
		this.attacher = attacher;
		this.bugId = bugId;
		this.bugRef = bugRef;
		this.creationTime = creationTime;
		this.data = data;
		this.description = description;
		this.encoding = encoding;
		this.fileName = fileName;
		this.flags = flags;
		this.id = id;
		this.isObsolete = isObsolete;
		this.isPatch = isPatch;
		this.isPrivate = isPrivate;
		this.ref = ref;
		this.size = size;
		this.updateToken = updateToken;
		this.contentType = contentType;
	}
	
	@Override
	public String toString() {
		return "Attachment [attacher=" + attacher + ", bugId=" + bugId
				+ ", bugRef=" + bugRef + ", comments=" + comments
				+ ", creationTime=" + creationTime
				+ ", description=" + description + ", encoding=" + encoding
				+ ", fileName=" + fileName + ", flags=" + flags + ", id=" + id
				+ ", isObsolete=" + isObsolete + ", isPatch=" + isPatch
				+ ", isPrivate=" + isPrivate + ", ref=" + ref + ", size="
				+ size + ", updateToken=" + updateToken + ", contentType="
				+ contentType + "]";
	}
	
	
}