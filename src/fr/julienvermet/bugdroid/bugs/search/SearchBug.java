package fr.julienvermet.bugdroid.bugs.search;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.julienvermet.bugdroid.bugs.Bug;
import fr.julienvermet.bugdroid.database.Attachment;
import fr.julienvermet.bugdroid.database.ChangeSet;
import fr.julienvermet.bugdroid.database.Comment;
import fr.julienvermet.bugdroid.database.Database;
import fr.julienvermet.bugdroid.database.Flag;
import fr.julienvermet.bugdroid.database.Group;
import fr.julienvermet.bugdroid.tools.Json;
import fr.julienvermet.bugdroid.tools.Tools;
import fr.julienvermet.bugdroid.users.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;

public class SearchBug {
	
	private static final String PREFS_NAME = "login";

	public static Bug SearchBugById(int bug_id, Context ctx, boolean update) {

		Log.d("update", "update:"+ update);
		if (!update)
		{
			Database db = new Database(ctx);

			if (db.isBugSaved(bug_id))
				return db.getBug(bug_id);
		}

		Bug bug = null;
		try {
			String request = "https://api-dev.bugzilla.mozilla.org/latest/bug/"
				+ bug_id
				+ "?include_fields=_all";
			
			SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
			String username = settings.getString("username", "");
			String password = settings.getString("password", "");
			
			if (!username.equals("") && !password.equals(""))
				request += "&username=" + username + "&password=" + password;

			// Downlaod json file
			JSONObject jobj = Json.getJSon(new URL(request));

			if (jobj == null)
				return null;
				
			String alias = "";
			if (jobj.has("alias"))
				alias = jobj.getString("alias");

			User assignedTo;
			JSONObject jobjAssignee = jobj.getJSONObject("assigned_to");
			if (jobjAssignee.has("real_name"))
				assignedTo = new User(jobjAssignee.getString("name"),
						jobjAssignee.getString("real_name"));
			else
				assignedTo = new User(jobjAssignee.getString("name"));

			ArrayList<Attachment> attachments = null;
			if (jobj.has("attachments")) {
				attachments = new ArrayList<Attachment>();
				JSONArray jarrayAttachments = jobj.getJSONArray("attachments");
				for (int i = 0; i < jarrayAttachments.length(); i++) {
					JSONObject jsonAttachment = jarrayAttachments.getJSONObject(i);

					int attachmentId = jsonAttachment.getInt("id");
					JSONObject jobjAttacher = jsonAttachment.getJSONObject("attacher");
					User attacher = new User(jobjAttacher.getString("name"));
					String bugRef = jsonAttachment.getString("bug_ref");
					String contentType = jsonAttachment.getString("content_type");
					String creationTime = jsonAttachment.getString("creation_time");
					String data = "";
					if (jsonAttachment.has("data"))
						data = jsonAttachment.getString("data");
					String description = jsonAttachment.getString("description");
					String encoding = "";
					if (jsonAttachment.has("encoding"))
						encoding = jsonAttachment.getString("encoding");
					String fileName = jsonAttachment.getString("file_name");

					ArrayList<Flag> flags = null;
					if (jsonAttachment.has("flags")) {
						flags = new ArrayList<Flag>();
						JSONArray jarrayFlags = jsonAttachment.getJSONArray("flags");
						for (int j=0; j<jarrayFlags.length(); j++) {

							Log.d("FLAG", "FLAG");

							JSONObject jsonFlag = jarrayFlags.getJSONObject(j);

							int flagId = jsonFlag.getInt("id");

							String name = jsonFlag.getString("name");

							String requestee = "";
							if (jsonFlag.has("requestee"))
								requestee = jsonFlag.getString("requestee");

							JSONObject jobjSetter = jsonFlag.getJSONObject("setter");
							User setter = new User(jobjSetter.getString("name"));

							String status = jsonFlag.getString("status");
							int typeId = jsonFlag.getInt("id");

							Flag flag = new Flag(flagId, name, requestee, setter,
									status, typeId, bug_id, attachmentId);

							flags.add(flag);
						}
					}

					Boolean isObsolete = Boolean.valueOf(jsonAttachment.getString("is_obsolete"));
					Boolean isPatch = Boolean.valueOf(jsonAttachment.getString("is_patch"));
					Boolean isPrivate = Boolean.valueOf(jsonAttachment.getString("is_private"));
					String lastChangeTime = jsonAttachment.getString("last_change_time");
					String ref = jsonAttachment.getString("ref");
					int size = jsonAttachment.getInt("size");
					String updateToken = "";
					if (jsonAttachment.has("update_token"))
						updateToken = jsonAttachment.getString("update_token");

					Attachment attachment = new Attachment(attacher, bug_id,
							bugRef, creationTime, data, description, encoding,
							fileName, flags, attachmentId, isObsolete, isPatch,
							isPrivate, bugRef, size, updateToken, contentType);

					attachments.add(attachment);
				}
			}

			String blocks = "";
			/*
			 * JSONArray jarrayBlocks = jobj.getJSONArray("blocks"); for(int
			 * i=0; i<jarrayBlocks.length(); i++) { JSONObject jsonBlock =
			 * jarrayBlocks.getJSONObject(i);
			 * 
			 * jsonBlock.getString("name");
			 * 
			 * }
			 */

			ArrayList<User> ccs = null;
			if (jobj.has("cc"))
			{
				ccs = new ArrayList<User>();

				JSONArray jarrayCcs = jobj.getJSONArray("cc");
				for (int i = 0; i < jarrayCcs.length(); i++) {
					JSONObject jsonCc = jarrayCcs.getJSONObject(i);

					User cc = new User(jsonCc.getString("name"));

					ccs.add(cc);
				}
			}

			String classification = jobj.getString("classification");

			ArrayList<Comment> comments = null;
			if (jobj.has("comments"))
			{
				comments = new ArrayList<Comment>();

				JSONArray jarrayComments = jobj.getJSONArray("comments");
				for (int i = 0; i < jarrayComments.length(); i++) {
					JSONObject jsonComment = jarrayComments.getJSONObject(i);

					int attachmentId = 0;
					if (jsonComment.has("attachment_id"))
						attachmentId = jsonComment.getInt("attachment_id");

					String attachmentRef = "";
					if (jsonComment.has("attachment_ref"))
						attachmentRef = jsonComment.getString("attachment_ref");

					JSONObject jobjCreator = jsonComment.getJSONObject("creator");
					User creator = new User(jobjCreator.getString("name"), jobjCreator.getString("real_name"));

					String creationTime = jsonComment.getString("creation_time");
					int id = jsonComment.getInt("id");
					Boolean isPrivate = Boolean.parseBoolean(jsonComment.getString("is_private"));
					String text = jsonComment.getString("text");

					Comment comment = new Comment(attachmentId, attachmentRef, bug_id, creator, creationTime, id, isPrivate, text);

					comments.add(comment);
				}
			}

			String component = jobj.getString("component");
			String creationTime = jobj.getString("creation_time");

			User creator;
			JSONObject jobjCreator = jobj.getJSONObject("assigned_to");
			if (jobjCreator.has("real_name"))
				creator = new User(jobjCreator.getString("name"),
						jobjAssignee.getString("real_name"));
			else
				creator = new User(jobjCreator.getString("name"));

			String deadline = "";
			String dependsOn = "";

			int dupeOf = 0;
			if (jobj.has("dupe_of"))
				dupeOf = jobj.getInt("dupe_of");

			float estimatedTime = 0;
			if (jobj.has("estimated_time"))
				estimatedTime = jobj.getInt("estimated_time");

			ArrayList<Flag> flags = null;
			if (jobj.has("flags")) {
				flags = new ArrayList<Flag>();
				JSONArray jarrayFlags = jobj.getJSONArray("flags");
				for (int i = 0; i < jarrayFlags.length(); i++) {
					JSONObject jsonFlag = jarrayFlags.getJSONObject(i);

					int flagId = jsonFlag.getInt("id");

					String name = jsonFlag.getString("name");
					
					String requestee = "";
					if (jsonFlag.has("requestee"))
					requestee = jsonFlag.getString("requestee");

					JSONObject jobjSetter = jsonFlag.getJSONObject("setter");
					User setter = new User(jobjSetter.getString("name"));

					String status = jsonFlag.getString("status");
					int typeId = jsonFlag.getInt("id");

					Flag flag = new Flag(flagId, name, requestee, setter, status, typeId, bug_id, 0);

					flags.add(flag);
				}
			}

			ArrayList<Group> groups = new ArrayList<Group>();
			ArrayList<ChangeSet> history = new ArrayList<ChangeSet>();

			Boolean isCcAccessible = Boolean.parseBoolean(jobj.getString("is_cc_accessible"));
			Boolean isConfirmed = Boolean.parseBoolean(jobj.getString("is_confirmed"));
			Boolean isCreatorAccessible = Boolean.parseBoolean(jobj.getString("is_creator_accessible"));

			String[] keywords = {};

			if (jobj.has("keywords")) {
				JSONArray jarrayKeywords = jobj.getJSONArray("keywords");
				Log.d("jarrayKeywords", "jarrayKeywords" + jarrayKeywords);
				// for(int i=0; i<jarrayKeywords.length(); i++) {
				// JSONObject jsonKeyword = jarrayCcs.getJSONObject(i);
				//
				// keywords[i] = jsonKeyword.getString("");
				// }
			}

			String lastChangeTime = jobj.getString("last_change_time");
			String opSys = jobj.getString("op_sys");
			String platform = jobj.getString("platform");
			String priority = jobj.getString("priority");
			String product = jobj.getString("product");

			JSONObject jobjQaContact = jobj.getJSONObject("qa_contact");
			User qaContact = new User(jobjCreator.getString("name"));

			String ref = jobj.getString("ref");
			float remainingTime = 0;

			String resolution = "";
			if (jobj.has("resolution"))
				resolution = jobj.getString("resolution");

			String seeAlso = "";

			String severity = jobj.getString("severity");
			String status = jobj.getString("status");
			String summary = jobj.getString("summary");
			String targetMilestone = jobj.getString("target_milestone");
			String updateToken = "";

			String url = "";	
			if (jobj.has("url"))
				url = jobj.getString("url");

			String version = jobj.getString("version");

			String whiteboard = "";	
			if (jobj.has("whiteboard"))
				whiteboard = jobj.getString("whiteboard");

			float workTime = 0;
			if (jobj.has("work_time"))
				workTime = jobj.getLong("work_time");

			bug = new Bug(alias, assignedTo, attachments, blocks, ccs,
					classification, comments, component, creationTime, creator,
					deadline, dependsOn, dupeOf, estimatedTime, flags, groups,
					history, bug_id, isCcAccessible, isConfirmed,
					isCreatorAccessible, keywords, lastChangeTime, opSys,
					platform, priority, product, qaContact, ref, remainingTime,
					resolution, seeAlso, severity, status, summary,
					targetMilestone, updateToken, url, version, whiteboard,
					workTime);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bug;
	}
	
	public static ArrayList<Bug> SearchBugsByProduct(String product, Context ctx, boolean update) {
		
		ArrayList<Bug> bugs = null;
		
		SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
		int hoursSearch = settings.getInt("hoursSearch", 1);
		
		try {
			String s = "https://api-dev.bugzilla.mozilla.org/latest/bug?product="+ URLEncoder.encode(product) +"&changed_after="+ hoursSearch +"h";

			JSONObject jobj = Json.getJSon(new URL(s));

			if (jobj!=null)
			{
				bugs = new ArrayList<Bug>();
				
				JSONArray jsonArray = jobj.getJSONArray("bugs");

				for(int i=0; i<jsonArray.length(); i++) {
					JSONObject jsono = (JSONObject) jsonArray.get(i);

					User assignedTo;
					JSONObject jobjAssignee = jsono.getJSONObject("assigned_to");
					if (jobjAssignee.has("real_name"))
						assignedTo = new User(jobjAssignee.getString("name"), jobjAssignee.getString("real_name"));
					else
						assignedTo = new User(jobjAssignee.getString("name"));

					int id = jsono.getInt("id");
					String op_sys = jsono.getString("op_sys");
					String priority = jsono.getString("priority");
					String resolution = jsono.getString("resolution");
					String severity = jsono.getString("severity");
					String status = jsono.getString("status");
					String summary = jsono.getString("summary");

					Bug bug = new Bug(assignedTo, id, op_sys, priority, resolution, severity, status, summary);
					bugs.add(bug);
				}
			}
			else
				{
				
				Looper.prepare();
				Tools.showToast(ctx, "Error while retrieving bugs");
				}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return bugs;
	}
	
	public static ArrayList<Bug> SearchBugsByParams(String params, Context ctx) {
		
		ArrayList<Bug> bugs = null;
		
		try {
			String s = "https://api-dev.bugzilla.mozilla.org/latest/bug?" + params;
			
			Log.d("s", "s:"+ s);

			JSONObject jobj = Json.getJSon(new URL(s));

			if (jobj!=null)
			{
				bugs = new ArrayList<Bug>();
				
				JSONArray jsonArray = jobj.getJSONArray("bugs");

				for(int i=0; i<jsonArray.length(); i++) {
					JSONObject jsono = (JSONObject) jsonArray.get(i);

					User assignedTo;
					JSONObject jobjAssignee = jsono.getJSONObject("assigned_to");
					if (jobjAssignee.has("real_name"))
						assignedTo = new User(jobjAssignee.getString("name"), jobjAssignee.getString("real_name"));
					else
						assignedTo = new User(jobjAssignee.getString("name"));

					int id = jsono.getInt("id");
					String op_sys = jsono.getString("op_sys");
					String priority = jsono.getString("priority");
					String resolution = jsono.getString("resolution");
					String severity = jsono.getString("severity");
					String status = jsono.getString("status");
					String summary = jsono.getString("summary");

					Bug bug = new Bug(assignedTo, id, op_sys, priority, resolution, severity, status, summary);
					bugs.add(bug);
				}
			}
			else
				Tools.showToast(ctx, "Error while retrieving bugs");
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return bugs;
	}
}
