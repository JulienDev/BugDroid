package fr.julienvermet.bugdroid.database;

import java.util.ArrayList;
import java.util.StringTokenizer;


import fr.julienvermet.bugdroid.bugs.Bug;
import fr.julienvermet.bugdroid.bugs.search.Search;
import fr.julienvermet.bugdroid.users.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Database is the class used to manage the database
 * 
 * @author Julien VERMET
 * @version 1.0
 */
public class Database {

	public static final String DATABASE_NAME = "database.sqlite";

	private static final String TABLE_BUGS = "bugs";
	private static final String TABLE_COMMENTS = "comments";
	private static final String TABLE_ATTACHMENTS = "attachments";
	private static final String TABLE_FLAGS = "flags";
	private static final String TABLE_SEARCHES = "searches";

	private static final String CREATE_TABLE_BUGS = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_BUGS
			+ " (id INTEGER PRIMARY KEY, actualTime FLOAT, alias VARCHAR, assignedToName VARCHAR, assignedToRealName VARCHAR,"
			+ " blocks VARCHAR, ccs VARCHAR, classification VARCHAR, component VARCHAR, creationTime VARCHAR, creatorName VARCHAR, creatorRealName VARCHAR,"
			+ " deadline VARCHAR, dependsOn VARCHAR, dupeOf INTEGER, estimatedTime FLOAT, isCcAccessible BOOL, isCcConfirmed BOOL, isCreatorAccessible BOOL, keywords VARCHAR,"
			+ " lastChangeTime VARCHAR, opSys VARCHAR, platform VARCHAR, priority VARCHAR, product VARCHAR, qaContactName VARCHAR, qaContactRealName VARCHAR,"
			+ " ref VARCHAR, remainingTime VARCHAR, resolution VARCHAR, seeAlso VARCHAR, severity VARCHAR, status VARCHAR, summary VARCHAR, targetMilestone VARCHAR,"
			+ " updateToken VARCHAR, url VARCHAR, version VARCHAR, whiteboard VARCHAR, workTime FLOAT );";

	private static final String CREATE_TABLE_COMMENTS = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_COMMENTS
			+ " (id INTEGER PRIMARY KEY, attachmentId INTEGER, attachmentRef VARCHAR, bugId INTEGER, creatorName VARCHAR, creatorRealName VARCHAR, creationTime VARCHAR,"
			+ " isPrivate BOOL, text TEXT);";

	private static final String CREATE_TABLE_ATTACHMENTS = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_ATTACHMENTS
			+ " (id INTEGER PRIMARY KEY, attacherName VARCHAR, attacherRealName VARCHAR, bugId INTEGER, bugRef VARCHAR, creationTime VARCHAR, data VARCHAR, description VARCHAR, encoding VARCHAR, fileName VARCHAR, isObsolete BOOL,"
			+ " isPatch BOOL, isPrivate BOOL, ref VARCHAR, size INTEGER, updateToken VARCHAR, contentType VARCHAR);";

	private static final String CREATE_TABLE_FLAGS = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_FLAGS
			+ " (id INTEGER PRIMARY KEY, name VARCHAR, requestee VARCHAR, setterName VARCHAR, setterRealName VARCHAR, status VARCHAR, typeId INTEGER, bugId INTEGER, attachmentId INTEGER);";

	/*
	 * private static final String CREATE_TABLE_SEARCHES =
	 * "CREATE TABLE IF NOT EXISTS " + TABLE_SEARCHES +
	 * " (id INTEGER PRIMARY KEY, name VARCHAR, status VARCHAR, product VARCHAR, words VARCHAR, days INTEGER);"
	 * ;
	 */

	private static final String CREATE_TABLE_SEARCHES = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_SEARCHES
			+ " (id INTEGER PRIMARY KEY, name VARCHAR, params TEXT);";

	private static final String LOGTAG = "Database";
	private SQLiteDatabase db;

	private Context ctx;

	/**
	 * Constructor of the class
	 * 
	 * @param ctx
	 *            The Context of the activity
	 */
	public Database(Context ctx) {
		this.ctx = ctx;

		try {
			open();
			// Create tables if not exists
			db.execSQL(CREATE_TABLE_BUGS);
			db.execSQL(CREATE_TABLE_COMMENTS);
			db.execSQL(CREATE_TABLE_ATTACHMENTS);
			db.execSQL(CREATE_TABLE_FLAGS);
			db.execSQL(CREATE_TABLE_SEARCHES);
			close();
		} catch (SQLException e) {
			Log.e("DB", e.toString());
		}
	}

	/**
	 * Open the database if exists or create it if it doesn't
	 */
	public void open() {
		db = ctx.openOrCreateDatabase(DATABASE_NAME, 0, null);
	}

	/**
	 * Close the database
	 */
	public void close() {
		db.close();
	}

	public boolean insertBug(Bug bug) {

		if (isBugSaved(bug.getId()))
			return false;

		ContentValues values = new ContentValues();
		values.put("id", bug.getId());
		values.put("actualTime", bug.getActualTime());
		values.put("alias", bug.getAlias());
		values.put("assignedToName", bug.getAssignedTo().getName());
		values.put("assignedToRealName", bug.getAssignedTo().getRealName());
		values.put("blocks", bug.getBlocks().toString());

		String ccs = "";
		for (int i = 0; i < bug.getCcs().size(); i++) {
			User user = bug.getCcs().get(i);

			ccs += user.getName() + "||";
		}
		values.put("ccs", ccs);

		values.put("classification", bug.getClassification());
		values.put("component", bug.getComponent());
		values.put("creationTime", bug.getCreationTime());
		values.put("creatorName", bug.getCreator().getName());
		values.put("creatorRealName", bug.getCreator().getRealName());
		values.put("deadline", bug.getDeadline());
		values.put("dependsOn", bug.getDependsOn().toString());
		values.put("dupeOf", bug.getDupeOf());
		values.put("estimatedTime", bug.getEstimatedTime());
		values.put("isCcAccessible", bug.getIsCcAccessible());
		values.put("isCcConfirmed", bug.getIsConfirmed());
		values.put("isCreatorAccessible", bug.getIsCreatorAccessible());

		String[] keywords = {};

		values.put("keywords", bug.getKeywords().toString());
		values.put("lastChangeTime", bug.getLastChangeTime());
		values.put("opSys", bug.getOpSys());
		values.put("platform", bug.getPlatform());
		values.put("priority", bug.getPriority());
		values.put("product", bug.getProduct());
		values.put("qaContactName", bug.getQaContact().getName());
		values.put("qaContactRealName", bug.getQaContact().getRealName());
		values.put("ref", bug.getRef());
		values.put("remainingTime", bug.getRemainingTime());
		values.put("resolution", bug.getResolution());
		values.put("seeAlso", bug.getSeeAlso().toString());
		values.put("severity", bug.getSeverity());
		values.put("status", bug.getStatus());
		values.put("summary", bug.getSummary());
		values.put("targetMilestone", bug.getTargetMilestone());
		values.put("updateToken", bug.getUpdateToken());
		values.put("url", bug.getUrl());
		values.put("version", bug.getVersion());
		values.put("whiteboard", bug.getWhiteboard());
		values.put("workTime", bug.getWorkTime());

		if (bug.getComments() != null) {
			for (int i = 0; i < bug.getComments().size(); i++) {
				insertComment(bug.getComments().get(i));
			}
		}

		if (bug.getAttachments() != null) {
			for (int i = 0; i < bug.getAttachments().size(); i++) {
				insertAttachment(bug.getAttachments().get(i));
			}
		}

		open();

		if (db.insert(TABLE_BUGS, null, values) > 0) {
			close();
			return true;
		} else {
			close();
			return false;
		}
	}

	public boolean isBugSaved(int bug_id) {

		open();

		long count = DatabaseUtils.longForQuery(db,
				"SELECT COUNT(id) AS RCount FROM bugs WHERE id='" + bug_id
						+ "'", null);

		close();

		if (count > 0)
			return true;

		return false;
	}

	public boolean isCommentSaved(int comment_id) {
		open();
		long count = DatabaseUtils.longForQuery(db,
				"SELECT COUNT(id) AS RCount FROM " + TABLE_COMMENTS
						+ " WHERE id='" + comment_id + "'", null);
		close();

		if (count > 0)
			return true;

		return false;
	}

	public boolean isAttachmentSaved(int attachment_id) {
		open();
		long count = DatabaseUtils.longForQuery(db,
				"SELECT COUNT(id) AS RCount FROM " + TABLE_ATTACHMENTS
						+ " WHERE id='" + attachment_id + "'", null);
		close();

		if (count > 0)
			return true;

		return false;
	}

	public boolean updateBug(Bug bug) {

		ContentValues values = new ContentValues();
		values.put("id", bug.getId());
		values.put("actualTime", bug.getActualTime());
		values.put("alias", bug.getAlias());
		values.put("assignedToName", bug.getAssignedTo().getName());
		values.put("assignedToRealName", bug.getAssignedTo().getRealName());
		values.put("blocks", bug.getBlocks().toString());

		String ccs = "";
		for (int i = 0; i < bug.getCcs().size(); i++) {
			User user = bug.getCcs().get(i);

			ccs += user.getName() + "||";
		}
		values.put("ccs", ccs);

		values.put("classification", bug.getClassification());
		values.put("component", bug.getComponent());
		values.put("creationTime", bug.getCreationTime());
		values.put("creatorName", bug.getCreator().getName());
		values.put("creatorRealName", bug.getCreator().getRealName());
		values.put("deadline", bug.getDeadline());
		values.put("dependsOn", bug.getDependsOn().toString());
		values.put("dupeOf", bug.getDupeOf());
		values.put("estimatedTime", bug.getEstimatedTime());
		values.put("isCcAccessible", bug.getIsCcAccessible());
		values.put("isCcConfirmed", bug.getIsConfirmed());
		values.put("isCreatorAccessible", bug.getIsCreatorAccessible());
		values.put("keywords", bug.getKeywords().toString());
		values.put("lastChangeTime", bug.getLastChangeTime());
		values.put("opSys", bug.getOpSys());
		values.put("platform", bug.getPlatform());
		values.put("priority", bug.getPriority());
		values.put("product", bug.getProduct());
		values.put("qaContactName", bug.getQaContact().getName());
		values.put("qaContactRealName", bug.getQaContact().getRealName());
		values.put("ref", bug.getRef());
		values.put("remainingTime", bug.getRemainingTime());
		values.put("resolution", bug.getResolution());
		values.put("seeAlso", bug.getSeeAlso().toString());
		values.put("severity", bug.getSeverity());
		values.put("status", bug.getStatus());
		values.put("summary", bug.getSummary());
		values.put("targetMilestone", bug.getTargetMilestone());
		values.put("updateToken", bug.getUpdateToken());
		values.put("url", bug.getUrl());
		values.put("version", bug.getVersion());
		values.put("whiteboard", bug.getWhiteboard());
		values.put("workTime", bug.getWorkTime());

		updateComments(bug.getId(), bug.getComments());

		deleteFlagsBug(bug.getId());
		deleteAttachmentsBug(bug.getId());
		if (bug.getAttachments() != null) {
			for (int i = 0; i < bug.getAttachments().size(); i++) {
				insertAttachment(bug.getAttachments().get(i));
			}
		}

		open();

		if (db.update(TABLE_BUGS, values, "id=" + bug.getId(), null) > 0) {
			close();
			return true;
		} else {
			close();
			return false;
		}
	}
	
	public void updateComments(int bug_id, ArrayList<Comment> comments)
	{
		deleteCommentsBug(bug_id);
		if (comments != null) {
			for (int i = 0; i < comments.size(); i++) {
				insertComment(comments.get(i));
			}
		}
	}

	public Bug getBug(int bug_id) {

		if (!isBugSaved(bug_id))
			return null;

		open();

		Cursor c = db.query(TABLE_BUGS, new String[] { "alias",
				"assignedToName", "assignedToRealName", "blocks", "ccs",
				"classification", "component", "creationTime", "creatorName",
				"creatorRealName", "deadline", "dependsOn", "dupeOf",
				"estimatedTime", "isCcAccessible", "isCcConfirmed",
				"isCreatorAccessible", "keywords", "lastChangeTime", "opSys",
				"platform", "priority", "product", "qaContactName",
				"qaContactRealName", "ref", "remainingTime", "resolution",
				"seeAlso", "severity", "status", "summary", "targetMilestone",
				"updateToken", "url", "version", "whiteboard", "workTime" },
				"id=" + bug_id, null, null, null, null);

		c.moveToFirst();

		int id = bug_id;
		String alias = c.getString(0);

		User assignedTo = new User(c.getString(1), c.getString(2));

		ArrayList<Attachment> attachments = getAttachments(bug_id);

		String blocks = c.getString(3);

		ArrayList<User> ccs = null;
		String ccsList = c.getString(4);
		Log.d("ccsList", "ccsList:" + ccsList);
		StringTokenizer st = new StringTokenizer(ccsList, "||");
		if (st.countTokens() > 0) {
			ccs = new ArrayList<User>();

			while (st.hasMoreTokens()) {
				String name = st.nextToken();
				Log.d("st.nextToken()", "st.nextToken():" + name);

				User user = new User(name);
				ccs.add(user);
			}
		}

		String classification = c.getString(5);
		ArrayList<Comment> comments = getComments(bug_id);
		String component = c.getString(6);
		String creationTime = c.getString(7);

		User creator = new User(c.getString(8), c.getString(9));

		String deadline = c.getString(10);
		String dependsOn = c.getString(11);
		int dupeOf = c.getInt(12);
		float estimatedTime = c.getInt(13);
		ArrayList<Flag> flags = null; // TODO:Retrieve flags
		ArrayList<Group> groups = null; // TODO:Retrieve groups
		ArrayList<ChangeSet> history = null; // TODO:Retrieve history

		boolean isCcAccessible;
		if (c.getString(14) == "1")
			isCcAccessible = true;
		else
			isCcAccessible = false;

		boolean isConfirmed;
		if (c.getString(15) == "1")
			isConfirmed = true;
		else
			isConfirmed = false;

		boolean isCreatorAccessible;
		if (c.getString(16) == "1")
			isCreatorAccessible = true;
		else
			isCreatorAccessible = false;

		String[] keywords = {}; // TODO:Retrieve keywords (17)
		String lastChangeTime = c.getString(18);
		String opSys = c.getString(19);
		String platform = c.getString(20);
		String priority = c.getString(21);
		String product = c.getString(22);

		User qaContact = new User(c.getString(23), c.getString(24));

		String ref = c.getString(25);
		float remainingTime = c.getInt(26);
		String resolution = c.getString(27);
		String seeAlso = c.getString(28);
		String severity = c.getString(29);
		String status = c.getString(30);
		String summary = c.getString(31);
		String targetMilestone = c.getString(32);
		String updateToken = c.getString(33);
		String url = c.getString(34);
		String version = c.getString(35);
		String whiteboard = c.getString(36);
		float workTime = c.getInt(37);

		Bug bug = new Bug(alias, assignedTo, attachments, blocks, ccs,
				classification, comments, component, creationTime, creator,
				deadline, dependsOn, dupeOf, estimatedTime, flags, groups,
				history, id, isCcAccessible, isConfirmed, isCreatorAccessible,
				keywords, lastChangeTime, opSys, platform, priority, product,
				qaContact, ref, remainingTime, resolution, seeAlso, severity,
				status, summary, targetMilestone, updateToken, url, version,
				whiteboard, workTime);

		c.close();
		close();

		return bug;
	}

	public ArrayList<Bug> getSavedBugs() {
		ArrayList<Bug> bugs = new ArrayList<Bug>();
		try {

			open();

			Cursor c = db.query(TABLE_BUGS, new String[] { "id", "alias",
					"assignedToName", "assignedToRealName", "blocks", "ccs",
					"classification", "component", "creationTime",
					"creatorName", "creatorRealName", "deadline", "dependsOn",
					"dupeOf", "estimatedTime", "isCcAccessible",
					"isCcConfirmed", "isCreatorAccessible", "keywords",
					"lastChangeTime", "opSys", "platform", "priority",
					"product", "qaContactName", "qaContactRealName", "ref",
					"remainingTime", "resolution", "seeAlso", "severity",
					"status", "summary", "targetMilestone", "updateToken",
					"url", "version", "whiteboard", "workTime" }, null, null,
					null, null, null);

			int numRows = c.getCount();

			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {

				int bug_id = c.getInt(0);
				String alias = c.getString(1);

				User assignedTo = new User(c.getString(2), c.getString(3));

				ArrayList<Attachment> attachments = getAttachments(bug_id);

				String blocks = c.getString(4);

				ArrayList<User> ccs = null;
				String ccsList = c.getString(5);
				Log.d("ccsList", "ccsList:" + ccsList);
				StringTokenizer st = new StringTokenizer(ccsList, "||");
				if (st.countTokens() > 0) {
					ccs = new ArrayList<User>();

					while (st.hasMoreTokens()) {
						String name = st.nextToken();
						Log.d("st.nextToken()", "st.nextToken():" + name);

						User user = new User(name);
						ccs.add(user);
					}
				}

				String classification = c.getString(6);
				ArrayList<Comment> comments = getComments(bug_id);
				String component = c.getString(7);
				String creationTime = c.getString(8);

				User creator = new User(c.getString(9), c.getString(10));

				String deadline = c.getString(11);
				String dependsOn = c.getString(12);
				int dupeOf = c.getInt(13);
				float estimatedTime = c.getInt(14);
				ArrayList<Flag> flags = null; // TODO:Retrieve flags
				ArrayList<Group> groups = null; // TODO:Retrieve groups
				ArrayList<ChangeSet> history = null; // TODO:Retrieve history

				boolean isCcAccessible;
				if (c.getString(15) == "1")
					isCcAccessible = true;
				else
					isCcAccessible = false;

				boolean isConfirmed;
				if (c.getString(16) == "1")
					isConfirmed = true;
				else
					isConfirmed = false;

				boolean isCreatorAccessible;
				if (c.getString(17) == "1")
					isCreatorAccessible = true;
				else
					isCreatorAccessible = false;

				String[] keywords = {}; // TODO:Retrieve keywords (17)
				String lastChangeTime = c.getString(19);
				String opSys = c.getString(20);
				String platform = c.getString(21);
				String priority = c.getString(22);
				String product = c.getString(23);

				User qaContact = new User(c.getString(24), c.getString(25));

				String ref = c.getString(26);
				float remainingTime = c.getInt(27);
				String resolution = c.getString(28);
				String seeAlso = c.getString(29);
				String severity = c.getString(30);
				String status = c.getString(31);
				String summary = c.getString(32);
				String targetMilestone = c.getString(33);
				String updateToken = c.getString(34);
				String url = c.getString(35);
				String version = c.getString(36);
				String whiteboard = c.getString(37);
				float workTime = c.getInt(38);

				Bug bug = new Bug(alias, assignedTo, attachments, blocks, ccs,
						classification, comments, component, creationTime,
						creator, deadline, dependsOn, dupeOf, estimatedTime,
						flags, groups, history, bug_id, isCcAccessible,
						isConfirmed, isCreatorAccessible, keywords,
						lastChangeTime, opSys, platform, priority, product,
						qaContact, ref, remainingTime, resolution, seeAlso,
						severity, status, summary, targetMilestone,
						updateToken, url, version, whiteboard, workTime);

				bugs.add(bug);
				c.moveToNext();
			}
			c.close();

			close();

		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		}
		return bugs;
	}

	/*
	 * public boolean insertSearch(Search search) {
	 * 
	 * ContentValues values = new ContentValues(); values.put("name",
	 * search.getName()); values.put("status", search.getStatus());
	 * values.put("product", search.getProduct()); values.put("words",
	 * search.getWords()); values.put("days", search.getDays());
	 * 
	 * open();
	 * 
	 * if (db.insert(TABLE_SEARCHES, null, values) > 0) { close(); return true;
	 * } else { close(); return false; } }
	 */

	public boolean insertSearch(Search search) {

		ContentValues values = new ContentValues();
		values.put("name", search.getName());
		values.put("params", search.getParams());
		/*
		 * values.put("status", search.getStatus()); values.put("product",
		 * search.getProduct()); values.put("words", search.getWords());
		 * values.put("days", search.getDays());
		 */

		open();

		if (db.insert(TABLE_SEARCHES, null, values) > 0) {
			close();
			return true;
		} else {
			close();
			return false;
		}
	}

	public ArrayList<Search> getSavedSearches() {
		ArrayList<Search> searches = new ArrayList<Search>();
		try {

			open();

			/*
			 * Cursor c = db.query(TABLE_SEARCHES, new String[] { "id", "name",
			 * "status", "product", "words", "days"}, null, null, null, null,
			 * null);
			 */

			Cursor c = db.query(TABLE_SEARCHES, new String[] { "id", "name",
					"params" }, null, null, null, null, null);

			int numRows = c.getCount();

			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {

				int id = c.getInt(0);
				String name = c.getString(1);
				String params = c.getString(2);

				Search search = new Search(id, name, params);

				/*
				 * String status = c.getString(2); String product =
				 * c.getString(3); String words = c.getString(4); int days =
				 * c.getInt(5);
				 * 
				 * Search search = new Search(id, name, status, product, words,
				 * days);
				 */

				searches.add(search);
				c.moveToNext();
			}
			c.close();

			close();

		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		}
		return searches;
	}

	public boolean insertComment(Comment comment) {

		if (isCommentSaved(comment.getId()))
			return false;

		ContentValues values = new ContentValues();
		values.put("id", comment.getId());
		values.put("attachmentId", comment.getAttachmentId());
		values.put("attachmentRef", comment.getAttachmentRef());
		values.put("bugId", comment.getBugId());
		values.put("creatorName", comment.getCreator().getName());
		values.put("creatorRealName", comment.getCreator().getRealName());
		values.put("creationTime", comment.getCreationTime());
		values.put("isPrivate", comment.getIsPrivate());
		values.put("text", comment.getText());

		open();

		if (db.insert(TABLE_COMMENTS, null, values) > 0) {
			close();
			return true;
		} else {
			close();
			return false;
		}
	}

	public ArrayList<Comment> getComments(int bug_id) {
		ArrayList<Comment> comments = new ArrayList<Comment>();
		try {

			Cursor c = db.query(TABLE_COMMENTS, new String[] { "id",
					"attachmentId", "attachmentRef", "bugId", "creatorName",
					"creatorRealName", "creationTime", "isPrivate", "text" },
					"bugId=" + bug_id, null, null, null, null);

			int numRows = c.getCount();

			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {

				int id = c.getInt(0);
				int attachmentId = c.getInt(1);
				String attachmentRef = c.getString(2);
				int bugId = c.getInt(3);

				User creator = new User(c.getString(4), c.getString(5));

				String creationTime = c.getString(6);

				boolean isPrivate;
				if (c.getString(7) == "1")
					isPrivate = true;
				else
					isPrivate = false;

				String text = c.getString(8);

				Comment comment = new Comment(attachmentId, attachmentRef,
						bugId, creator, creationTime, id, isPrivate, text);

				comments.add(comment);
				c.moveToNext();
			}
			c.close();

		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		}
		return comments;
	}

	public boolean insertAttachment(Attachment attachment) {

		if (attachment.getFlags() != null) {
			for (int i = 0; i < attachment.getFlags().size(); i++) {
				insertFlag(attachment.getFlags().get(i));
			}
		}

		if (isAttachmentSaved(attachment.getId()))
			return false;

		ContentValues values = new ContentValues();
		values.put("id", attachment.getId());
		values.put("attacherName", attachment.getAttacher().getName());
		values.put("attacherRealName", attachment.getAttacher().getRealName());
		values.put("bugId", attachment.getBugId());
		values.put("bugRef", attachment.getBugRef());
		values.put("creationTime", attachment.getCreationTime());
		values.put("data", attachment.getData());
		values.put("description", attachment.getDescription());
		values.put("encoding", attachment.getEncoding());
		values.put("fileName", attachment.getFileName());
		values.put("isObsolete", attachment.getIsObsolete());
		values.put("isPatch", attachment.getIsPatch());
		values.put("isPrivate", attachment.getIsPrivate());
		values.put("ref", attachment.getRef());
		values.put("size", attachment.getSize());
		values.put("updateToken", attachment.getUpdateToken());
		values.put("contentType", attachment.getContentType());

		open();

		if (db.insert(TABLE_ATTACHMENTS, null, values) > 0) {
			close();
			return true;
		} else {
			close();
			return false;
		}
	}

	public ArrayList<Attachment> getAttachments(int bug_id) {
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();
		try {

			Cursor c = db.query(TABLE_ATTACHMENTS, new String[] { "id",
					"attacherName", "attacherRealName", "bugRef",
					"creationTime", "data", "description", "encoding",
					"fileName", "isObsolete", "isPatch", "isPrivate", "ref",
					"size", "updateToken", "contentType" }, "bugId=" + bug_id,
					null, null, null, null);

			int numRows = c.getCount();

			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {

				int id = c.getInt(0);
				User attacher = new User(c.getString(1), c.getString(2));
				String bugRef = c.getString(3);
				String creationTime = c.getString(4);
				String data = c.getString(5);
				String description = c.getString(6);
				String encoding = c.getString(7);
				String fileName = c.getString(8);

				boolean isObsolete;
				if (c.getString(9) == "1")
					isObsolete = true;
				else
					isObsolete = false;

				boolean isPatch;
				if (c.getString(10) == "1")
					isPatch = true;
				else
					isPatch = false;

				boolean isPrivate;
				if (c.getString(11) == "1")
					isPrivate = true;
				else
					isPrivate = false;

				String ref = c.getString(12);
				int size = c.getInt(13);
				String updateToken = c.getString(14);
				String contentType = c.getString(15);

				ArrayList<Comment> comments = null;
				ArrayList<Flag> flags = getFlagsAttachment(id);

				Attachment attachment = new Attachment(attacher, bug_id,
						bugRef, comments, creationTime, data, description,
						encoding, fileName, flags, id, isObsolete, isPatch,
						isPrivate, ref, size, updateToken, contentType);

				attachments.add(attachment);
				c.moveToNext();
			}
			c.close();

		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		}
		return attachments;
	}

	public boolean insertFlag(Flag flag) {

		if (isFlagSaved(flag.getId()))
			return false;

		ContentValues values = new ContentValues();
		values.put("id", flag.getId());
		values.put("name", flag.getName());
		values.put("requestee", flag.getRequestee());
		values.put("setterName", flag.getSetter().getName());
		values.put("setterRealName", flag.getSetter().getRealName());
		values.put("status", flag.getStatus());
		values.put("typeId", flag.getTypeId());
		values.put("bugId", flag.getBugId());
		values.put("attachmentId", flag.getAttachmentId());

		open();

		if (db.insert(TABLE_FLAGS, null, values) > 0) {
			close();
			return true;
		} else {
			close();
			return false;
		}
	}

	public ArrayList<Flag> getFlagsBug(int bug_id) {
		ArrayList<Flag> flags = new ArrayList<Flag>();
		try {

			Cursor c = db.query(TABLE_FLAGS, new String[] { "id", "name",
					"requestee", "setterName", "setterRealName", "status",
					"typeId", "attachmentId" }, "bugId=" + bug_id, null, null,
					null, null);

			int numRows = c.getCount();

			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {

				int id = c.getInt(0);
				String name = c.getString(1);
				String requestee = c.getString(2);
				User setter = new User(c.getString(3), c.getString(4));
				String status = c.getString(5);
				int typeId = c.getInt(6);
				int attachmentId = c.getInt(7);

				Flag flag = new Flag(id, name, requestee, setter, status,
						typeId, bug_id, attachmentId);

				flags.add(flag);
				c.moveToNext();
			}
			c.close();

		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		}
		return flags;
	}

	public ArrayList<Flag> getFlagsAttachment(int attachment_id) {
		ArrayList<Flag> flags = new ArrayList<Flag>();
		try {

			Cursor c = db.query(TABLE_FLAGS, new String[] { "id", "name",
					"requestee", "setterName", "setterRealName", "status",
					"typeId", "bugId" }, "attachmentId=" + attachment_id, null,
					null, null, null);

			int numRows = c.getCount();

			c.moveToFirst();
			for (int i = 0; i < numRows; ++i) {

				int id = c.getInt(0);
				String name = c.getString(1);
				String requestee = c.getString(2);
				User setter = new User(c.getString(3), c.getString(4));
				String status = c.getString(5);
				int typeId = c.getInt(6);
				int bugId = c.getInt(7);

				Flag flag = new Flag(id, name, requestee, setter, status,
						typeId, bugId, attachment_id);

				flags.add(flag);
				c.moveToNext();
			}
			c.close();

		} catch (Exception e) {
			Log.e(LOGTAG, e.toString());
		}
		return flags;
	}

	public boolean isFlagSaved(int flag_id) {

		open();

		long count = DatabaseUtils.longForQuery(db,
				"SELECT COUNT(id) AS RCount FROM " + TABLE_FLAGS
						+ " WHERE id='" + flag_id + "'", null);

		close();

		if (count > 0)
			return true;

		return false;
	}

	public boolean deleteSavedSearch(int SavedSearchId) {
		open();
		boolean result = db.delete(TABLE_SEARCHES, "id=" + SavedSearchId, null) > 0;
		close();
		return result;
	}

	public boolean deleteBug(int bugId) {
		open();
		boolean result = db.delete(TABLE_BUGS, "id=" + bugId, null) > 0;
		deleteAttachmentsBug(bugId);
		deleteCommentsBug(bugId);
		deleteFlagsBug(bugId);
		close();
		return result;
	}

	public boolean deleteAttachmentsBug(int bugId) {
		open();
		boolean result = db.delete(TABLE_ATTACHMENTS, "bugId=" + bugId, null) > 0;
		close();
		return result;
	}

	public boolean deleteCommentsBug(int bugId) {
		open();
		boolean result = db.delete(TABLE_COMMENTS, "bugId=" + bugId, null) > 0;
		close();
		return result;
	}

	public boolean deleteFlagsBug(int bugId) {
		open();
		boolean result = db.delete(TABLE_FLAGS, "bugId=" + bugId, null) > 0;
		close();
		return result;
	}
}