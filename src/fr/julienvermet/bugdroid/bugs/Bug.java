package fr.julienvermet.bugdroid.bugs;

import java.io.Serializable;
import java.util.ArrayList;


import fr.julienvermet.bugdroid.database.Attachment;
import fr.julienvermet.bugdroid.database.ChangeSet;
import fr.julienvermet.bugdroid.database.Comment;
import fr.julienvermet.bugdroid.database.Flag;
import fr.julienvermet.bugdroid.database.Group;
import fr.julienvermet.bugdroid.users.User;

import android.database.Observable;

public class Bug implements Serializable {

	protected float actualTime;
	protected String alias;
	protected User assignedTo;
	protected ArrayList<Attachment> attachments; //Table
	protected String blocks;
	protected ArrayList<User> ccs;
	protected String classification;
	protected ArrayList<Comment> comments; //Table
	protected String component;
	protected String creationTime; //TODO Timestamp String, Read Only
	protected User creator;
	protected String deadline; //TODO Datestamp String
	protected String dependsOn;
	protected int dupeOf;
	protected float estimatedTime;
	protected ArrayList<Flag> flags; //Table
	protected ArrayList<Group> groups; //Table
	protected ArrayList<ChangeSet> history; //Table
	protected int id;
	protected Boolean isCcAccessible;
	protected Boolean isConfirmed;
	protected Boolean isCreatorAccessible;
	protected String[] keywords;
	protected String lastChangeTime;
	protected String opSys;
	protected String platform;
	protected String priority;
	protected String product;
	protected User qaContact;
	protected String ref;
	protected float remainingTime;
	protected String resolution;
	protected String seeAlso;
	protected String severity;
	protected String status;
	protected String summary;
	protected String targetMilestone;
	protected String updateToken;
	protected String url;
	protected String version;
	protected String whiteboard;
	protected float workTime;
	
	public Bug(String alias, User assignedTo,
			ArrayList<Attachment> attachments, String blocks,
			ArrayList<User> ccs, String classification,
			ArrayList<Comment> comments, String component, String creationTime,
			User creator, String deadline, String dependsOn, int dupeOf,
			float estimatedTime, ArrayList<Flag> flags,
			ArrayList<Group> groups, ArrayList<ChangeSet> history, int id,
			Boolean isCcAccessible, Boolean isConfirmed,
			Boolean isCreatorAccessible, String[] keywords,
			String lastChangeTime, String opSys, String platform,
			String priority, String product, User qaContact, String ref,
			float remainingTime, String resolution, String seeAlso,
			String severity, String status, String summary,
			String targetMilestone, String updateToken, String url,
			String version, String whiteboard, float workTime) {
		super();
		this.alias = alias;
		this.assignedTo = assignedTo;
		this.attachments = attachments;
		this.blocks = blocks;
		this.ccs = ccs;
		this.classification = classification;
		this.comments = comments;
		this.component = component;
		this.creationTime = creationTime;
		this.creator = creator;
		this.deadline = deadline;
		this.dependsOn = dependsOn;
		this.dupeOf = dupeOf;
		this.estimatedTime = estimatedTime;
		this.flags = flags;
		this.groups = groups;
		this.history = history;
		this.id = id;
		this.isCcAccessible = isCcAccessible;
		this.isConfirmed = isConfirmed;
		this.isCreatorAccessible = isCreatorAccessible;
		this.keywords = keywords;
		this.lastChangeTime = lastChangeTime;
		this.opSys = opSys;
		this.platform = platform;
		this.priority = priority;
		this.product = product;
		this.qaContact = qaContact;
		this.ref = ref;
		this.remainingTime = remainingTime;
		this.resolution = resolution;
		this.seeAlso = seeAlso;
		this.severity = severity;
		this.status = status;
		this.summary = summary;
		this.targetMilestone = targetMilestone;
		this.updateToken = updateToken;
		this.url = url;
		this.version = version;
		this.whiteboard = whiteboard;
		this.workTime = workTime;
	}
	
	public Bug(User assignedTo, int id, String opSys, String priority,
			String resolution, String severity, String status, String summary) {
		super();
		this.assignedTo = assignedTo;
		this.id = id;
		this.opSys = opSys;
		this.priority = priority;
		this.resolution = resolution;
		this.severity = severity;
		this.status = status;
		this.summary = summary;
	}

	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public User getAssignedTo() {
		return assignedTo;
	}
	public void setAssigned_to(User assignedTo) {
		this.assignedTo = assignedTo;
	}
	public ArrayList<Attachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(ArrayList<Attachment> attachments) {
		this.attachments = attachments;
	}
	public String getBlocks() {
		return blocks;
	}
	public void setBlocks(String blocks) {
		this.blocks = blocks;
	}
	public ArrayList<User> getCcs() {
		return ccs;
	}
	public void setCcs(ArrayList<User> ccs) {
		this.ccs = ccs;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public ArrayList<Comment> getComments() {
		return comments;
	}
	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public String getDeadline() {
		return deadline;
	}
	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}
	public String getDependsOn() {
		return dependsOn;
	}
	public void setDependsOn(String dependsOn) {
		this.dependsOn = dependsOn;
	}
	public int getDupeOf() {
		return dupeOf;
	}
	public void setDupeOf(int dupeOf) {
		this.dupeOf = dupeOf;
	}
	public float getEstimatedTime() {
		return estimatedTime;
	}
	public void setEstimated_time(float estimatedTime) {
		this.estimatedTime = estimatedTime;
	}
	public ArrayList<Flag> getFlags() {
		return flags;
	}
	public void setFlags(ArrayList<Flag> flags) {
		this.flags = flags;
	}
	public ArrayList<Group> getGroups() {
		return groups;
	}
	public void setGroups(ArrayList<Group> groups) {
		this.groups = groups;
	}
	public Boolean getIsCcAccessible() {
		return isCcAccessible;
	}
	public void setIsCcAccessible(Boolean isCcAccessible) {
		this.isCcAccessible = isCcAccessible;
	}
	public Boolean getIsCreatorAccessible() {
		return isCreatorAccessible;
	}
	public void setIsCreatorAccessible(Boolean isCreatorAccessible) {
		this.isCreatorAccessible = isCreatorAccessible;
	}
	public String[] getKeywords() {
		return keywords;
	}
	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}
	public String getOpSys() {
		return opSys;
	}
	public void setOpSys(String opSys) {
		this.opSys = opSys;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public User getQaContact() {
		return qaContact;
	}
	public void setQaContact(User qaContact) {
		this.qaContact = qaContact;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getSeeAlso() {
		return seeAlso;
	}
	public void setSeeAlso(String seeAlso) {
		this.seeAlso = seeAlso;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getTargetMilestone() {
		return targetMilestone;
	}
	public void setTargetMilestone(String targetMilestone) {
		this.targetMilestone = targetMilestone;
	}
	public String getUpdateToken() {
		return updateToken;
	}
	public void setUpdateToken(String updateToken) {
		this.updateToken = updateToken;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getWhiteboard() {
		return whiteboard;
	}
	public void setWhiteboard(String whiteboard) {
		this.whiteboard = whiteboard;
	}
	public float getWorkTime() {
		return workTime;
	}
	public void setWorkTime(float workTime) {
		this.workTime = workTime;
	}
	public float getActualTime() {
		return actualTime;
	}
	public String getCreationTime() {
		return creationTime;
	}
	public User getCreator() {
		return creator;
	}
	public ArrayList<ChangeSet> getHistory() {
		return history;
	}
	public int getId() {
		return id;
	}
	public Boolean getIsConfirmed() {
		return isConfirmed;
	}
	public String getLastChangeTime() {
		return lastChangeTime;
	}
	public String getRef() {
		return ref;
	}
	public float getRemainingTime() {
		return remainingTime;
	}
}