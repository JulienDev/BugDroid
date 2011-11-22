package fr.julienvermet.bugdroid.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import fr.julienvermet.bugdroid.database.Comment;
import fr.julienvermet.bugdroid.users.User;

public class Json {

	private static final String PREFS_NAME = "login";

	public static JSONObject getJSon(URL url) {
		try {
			// Read all the text returned by the server		
            URLConnection urlc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
			
			String str;
			StringBuffer jString = new StringBuffer();
			while ((str = in.readLine()) != null) {
				// str is one line of text; readLine() strips the newline character(s)
				jString.append(str);
			}
			try {
				return new JSONObject(jString.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			in.close();
		} catch (MalformedURLException e) {
			Log.i("MalformedURLException", e.getMessage());
		} catch (IOException e) {
			Log.i("IOException", e.getMessage());
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean postComment(String text, int bug_id, Context ctx)
	{
		try {
			SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
			String username = settings.getString("username", "");
			String password = settings.getString("password", "");

			String url = "https://api-dev.bugzilla.mozilla.org/latest/bug/"+ bug_id +"/comment";

			//			String url = "https://api-dev.bugzilla.mozilla.org/test/latest/bug/3284/comment";
			url += "?username=" + username
					+ "&password=" + password;

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpost = new HttpPost(url);

			JSONObject data = new JSONObject();
			data.put("text", text );

			StringEntity se = new StringEntity(data.toString());
			httpost.setEntity(se);
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");

			ResponseHandler responseHandler = new BasicResponseHandler();

			String response = httpclient.execute(httpost, responseHandler);
			Log.d("response","response:"+ response);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean updateStatus(String status, String resolution, int dupe_of, int bug_id , Context ctx)
	{
		try {
			SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
			String username = settings.getString("username", "");
			String password = settings.getString("password", "");

			String url = "https://api-dev.bugzilla.mozilla.org/latest/bug/"+ bug_id +"/comment";

			//			String url = "https://api-dev.bugzilla.mozilla.org/test/latest/bug/"+ bug_id;
			url += "?username=" + username
					+ "&password=" + password;

			String token = getToken(url, ctx);
			if (token == null)
				return false;

			DefaultHttpClient httpclient = new DefaultHttpClient();

			HttpPut httput = new HttpPut(url);

			JSONObject JObjStatus = new JSONObject();
			JObjStatus.put("token", token);
			JObjStatus.put("status", status );
			if (!resolution.equals(""))
				JObjStatus.put("resolution", resolution );
			if (dupe_of != 0)
				JObjStatus.put("dupe_of", dupe_of);

			StringEntity se = new StringEntity( JObjStatus.toString() );

			httput.setEntity( se );
			httput.setHeader("Accept", "application/json");
			httput.setHeader("Content-Type", "application/json");

			ResponseHandler responseHandler = new BasicResponseHandler();

			String response = httpclient.execute(httput, responseHandler);
			Log.d("response","response:"+ response);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static String getToken(String url, Context ctx) {

		try {

			JSONObject jobj = Json.getJSon(new URL(url));

			if (jobj!=null)
			{				
				String token = jobj.getString("update_token");
				return token;
			}
			else
				Tools.showToast(ctx, "Error while retrieving token. Please check your internet connection and your login details");
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static ArrayList<Comment> getComments(int bug_id, Context ctx) {

		ArrayList<Comment> comments = null;

		try {

			String url = "https://api-dev.bugzilla.mozilla.org/latest/bug/"+ bug_id +"/comment";

			//			String url = "https://api-dev.bugzilla.mozilla.org/test/latest/bug/3284/comment";

			SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
			String username = settings.getString("username", "");
			String password = settings.getString("password", "");

			if (!username.equals("") && !password.equals(""))
			{
				url += "?username=" + username
						+ "&password=" + password;
			}

			JSONObject jobj = Json.getJSon(new URL(url));

			if (jobj!=null)
			{				

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
						User creator = new User(jobjCreator.getString("name"));

						String creationTime = jsonComment.getString("creation_time");
						int id = jsonComment.getInt("id");
						Boolean isPrivate = Boolean.parseBoolean(jsonComment.getString("is_private"));
						String text = jsonComment.getString("text");

						Comment comment = new Comment(attachmentId, attachmentRef, bug_id, creator, creationTime, id, isPrivate, text);

						comments.add(comment);
					}
				}
			}
			else
				Tools.showToast(ctx, "Error while retrieving token. Please check your internet connection and your login details");
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return comments;
	}
}