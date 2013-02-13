/*
* Copyright (C) 2013 Julien Vermet
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package fr.julienvermet.bugdroid.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import fr.julienvermet.bugdroid.model.Account;
import fr.julienvermet.bugdroid.model.Attachment;
import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.model.Cc;
import fr.julienvermet.bugdroid.model.Comment;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Attachments;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Bugs;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Ccs;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Comments;
import fr.julienvermet.bugdroid.util.NetworkUtils;

public class BugIntentService extends IntentService {

    public static final String BUG = "bug";
    private static final String QUERY = "query";
    private static final String INSTANCES_ID = "instances_id";
    private static final String ACCOUNTS_ID = "accounts_id";
    private static final String BUG_ID = "bugId";
    public static final String FROM_CACHE = "fromCache";
    public static final String MESSENGER = "messenger";

    // TODO : Limit fields?
    private static final String BUG_SUFFIX = "bug/%d?include_fields=_all"; 
    private static final String ACCOUNT_SUFFIX = "&username=%s&password=%s";

    public BugIntentService() {
        super(BugIntentService.class.getSimpleName());
    }

    public static Intent getIntent(Context context, Instance instance, int bugId, boolean fromCache) {
        Intent intent = new Intent(context, BugIntentService.class);
        String accountSuffix = "";
        Account account = instance.account;
        if (account != null) {
            intent.putExtra(ACCOUNTS_ID, account._id);
            accountSuffix = String.format(ACCOUNT_SUFFIX, account.username, account.password);
        }

        String bugSuffix = String.format(BUG_SUFFIX, bugId);
        String query = instance.url.concat(bugSuffix).concat(accountSuffix);

        intent.putExtra(QUERY, query);
        intent.putExtra(INSTANCES_ID, instance._id);
        intent.putExtra(BUG_ID, bugId);
        intent.putExtra(FROM_CACHE, fromCache);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();

        Bundle bundle = intent.getExtras();
        String query = bundle.getString(QUERY);
        int instances_id = bundle.getInt(INSTANCES_ID);
        int accounts_id = bundle.getInt(ACCOUNTS_ID, -1);
        int bugId = bundle.getInt(BUG_ID);
        boolean fromCache = bundle.getBoolean(FROM_CACHE);

        try {
            String selection = Bugs.Columns.INSTANCES_ID.getName() + "=" + instances_id
                + " AND " + Bugs.Columns.ACCOUNTS_ID.getName() + "=" + accounts_id
                + " AND " + Bugs.Columns.BUG_ID.getName() + "=" + bugId;
            Cursor cursor = context.getContentResolver().query(Bugs.CONTENT_URI, Bugs.PROJECTION, selection, null, null);
            Bug bugFromCache = null;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                bugFromCache = Bug.toBug(cursor);
                cursor.close();
            }

            if (fromCache && bugFromCache != null) {   
                sendResult(intent, bugFromCache, true);
                return;
            }

            //Get bug from web and parse it
            String jsonString = NetworkUtils.readJson(query).result;
            Gson gson = new Gson();
            Bug bugFromWeb = gson.fromJson(jsonString, Bug.class);
            if (bugFromWeb != null) {
                bugFromWeb.instances_id = instances_id;
                bugFromWeb.accounts_id = accounts_id;
                
                int bugs_id;
                if (bugFromCache != null) {
                    bugs_id = bugFromCache._id;
                    bugFromWeb.isBookmarked = bugFromCache.isBookmarked;

                    // Delete old data for bug
                    String selectionComments = Comments.Columns.BUGS_ID + "=" + bugs_id;
                    context.getContentResolver().delete(Comments.CONTENT_URI, selectionComments, null);
                    String selectionAttachments = Attachments.Columns.BUGS_ID + "=" + bugs_id;
                    context.getContentResolver().delete(Attachments.CONTENT_URI, selectionAttachments, null);
                    String selectionCcs = Ccs.Columns.BUGS_ID + "=" + bugs_id;
                    context.getContentResolver().delete(Ccs.CONTENT_URI, selectionCcs, null);

                    Uri uri = Uri.withAppendedPath(Bugs.CONTENT_URI, String.valueOf(bugs_id));
                    context.getContentResolver().update(uri, bugFromCache.toContentValues(), null, null);
                } else {
                    Uri uriInserted = context.getContentResolver().insert(Bugs.CONTENT_URI, bugFromWeb.toContentValues());
                    bugs_id = Integer.parseInt(uriInserted.getLastPathSegment());
                    bugFromWeb._id = bugs_id;
                }

                //Insert new data
                context.getContentResolver().bulkInsert(Comments.CONTENT_URI, Comment.toContentValues(bugFromWeb.comments, bugs_id));
                context.getContentResolver().bulkInsert(Attachments.CONTENT_URI, Attachment.toContentValues(bugFromWeb.attachments, bugs_id));
                context.getContentResolver().bulkInsert(Ccs.CONTENT_URI, Cc.toContentValues(bugFromWeb.cc, bugs_id));
            }
 
            //Send bug to activity
            sendResult(intent, bugFromWeb, false);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void sendResult(Intent intent, Bug bug, boolean fromCache) {
        Bundle extras = intent.getExtras();
        Messenger messenger = (Messenger) extras.get(MESSENGER);
        if (messenger != null) {
            Message msg = Message.obtain();
            Bundle data = new Bundle();
            data.putBoolean(FROM_CACHE, fromCache);
            data.putSerializable(BUG, bug);
            msg.setData(data);
            try {
                messenger.send(msg);
            } catch (android.os.RemoteException e1) {
                Log.w(getClass().getName(), "Exception sending message", e1);
            }
        }
    }
}