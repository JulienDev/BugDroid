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

package fr.julienvermet.bugdroid.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Bugs;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Bugs.Columns;

public class Bug implements Serializable {

    private static final long serialVersionUID = 2541841490838164840L;
    public int _id;
    public int instances_id;
    public int accounts_id;
    public boolean isBookmarked;
    @SerializedName("assigned_to")
    public User assignedTo;
    @SerializedName("attachments")
    public List<Attachment> attachments = new ArrayList<Attachment>();
    @SerializedName("blocks")
    public List<Integer> blocks = new ArrayList<Integer>();
    @SerializedName("cc")
    public List<Cc> cc = new ArrayList<Cc>();
    @SerializedName("classification")
    public String classification;
    @SerializedName("comments")
    public List<Comment> comments = new ArrayList<Comment>();
    @SerializedName("component")
    public String component;
    @SerializedName("creation_time")
    public String creationTime;
    @SerializedName("creator")
    public User creator;
    @SerializedName("depends_on")
    public List<Integer> dependsOn = new ArrayList<Integer>();
    @SerializedName("flags")
    public List<Flag> flags = new ArrayList<Flag>();
    @SerializedName("history")
    public List<History> history = new ArrayList<History>();
    @SerializedName("id")
    public int bugId;
    @SerializedName("is_cc_accessible")
    public boolean isCcAccessible;
    @SerializedName("is_confirmed")
    public boolean isConfirmed;
    @SerializedName("is_creator_accessible")
    public boolean isCreatorAccessible;
    @SerializedName("keywords")
    public List<String> keywords = new ArrayList<String>();
    @SerializedName("last_change_time")
    public String lastChangeTime;
    @SerializedName("op_sys")
    public String opSys;
    @SerializedName("platform")
    public String platform;
    @SerializedName("priority")
    public String priority;
    @SerializedName("product")
    public String product;
    @SerializedName("qa_contact")
    public User qaContact;
    @SerializedName("ref")
    public String ref;
    @SerializedName("resolution")
    public String resolution;
    @SerializedName("severity")
    public String severity;
    @SerializedName("status")
    public String status;
    @SerializedName("summary")
    public String summary;
    @SerializedName("target_milestone")
    public String targetMilestone;
    @SerializedName("version")
    public String version;
    @SerializedName("whiteboard")
    public String whiteboard;

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(Columns.BUG_ID.getName(), bugId);
        values.put(Columns.INSTANCES_ID.getName(), instances_id);
        values.put(Columns.ACCOUNTS_ID.getName(), accounts_id);
        values.put(Columns.ASSIGNED_TO_NAME.getName(), assignedTo.name);
        values.put(Columns.ASSIGNED_TO_REAL_NAME.getName(), assignedTo.realName);
        values.put(Columns.CLASSIFICATION.getName(), classification);
        values.put(Columns.COMPONENT.getName(), component);
        values.put(Columns.CREATION_TIME.getName(), creationTime);
        values.put(Columns.CREATOR_NAME.getName(), creator.name);
        values.put(Columns.CREATOR_REAL_NAME.getName(), creator.realName);
        values.put(Columns.LAST_CHANGE_TIME.getName(), lastChangeTime);
        values.put(Columns.OP_SYS.getName(), opSys);
        values.put(Columns.PLATFORM.getName(), platform);
        values.put(Columns.PRIORITY.getName(), priority);
        values.put(Columns.PRODUCT.getName(), product);
        values.put(Columns.QA_CONTACT_NAME.getName(), qaContact.name);
        values.put(Columns.QA_CONTACT_REAL_NAME.getName(), assignedTo.realName);
        values.put(Columns.REF.getName(), ref);
        values.put(Columns.RESOLUTION.getName(), resolution);
        values.put(Columns.SEVERITY.getName(), severity);
        values.put(Columns.STATUS.getName(), status);
        values.put(Columns.SUMMARY.getName(), summary);
        values.put(Columns.TARGET_MILESTONE.getName(), targetMilestone);
        values.put(Columns.VERSION.getName(), version);
        values.put(Columns.WHITEBOARD.getName(), whiteboard);
        return values;
    }

    public static Bug toBug(Cursor cursor) {
        Bug bug = new Bug();
        bug._id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.ID.getName()));
        bug.bugId = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.BUG_ID.getName()));
        bug.isBookmarked = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.BOOKMARK.getName())) == 1;
        bug.instances_id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.INSTANCES_ID.getName()));
        bug.accounts_id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.ACCOUNTS_ID.getName()));
        bug.assignedTo = new User();
        bug.assignedTo.name = cursor.getString(cursor.getColumnIndexOrThrow(Columns.ASSIGNED_TO_NAME.getName()));
        bug.assignedTo.realName = cursor.getString(cursor.getColumnIndexOrThrow(Columns.ASSIGNED_TO_REAL_NAME.getName()));
        bug.attachments = Attachment.getAttachmentsForBug(bug._id);
        bug.classification = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CLASSIFICATION.getName()));
        bug.cc = Cc.getCcsForBug(bug._id);
        bug.component = cursor.getString(cursor.getColumnIndexOrThrow(Columns.COMPONENT.getName()));
        bug.comments = Comment.getCommentsForBug(bug._id);
        bug.creationTime = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CREATION_TIME.getName()));
        bug.creator = new User();
        bug.creator.name = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CREATOR_NAME.getName()));
        bug.creator.realName = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CREATOR_REAL_NAME.getName()));
        bug.lastChangeTime = cursor.getString(cursor.getColumnIndexOrThrow(Columns.LAST_CHANGE_TIME.getName()));
        bug.opSys = cursor.getString(cursor.getColumnIndexOrThrow(Columns.OP_SYS.getName()));
        bug.platform = cursor.getString(cursor.getColumnIndexOrThrow(Columns.PLATFORM.getName()));
        bug.priority = cursor.getString(cursor.getColumnIndexOrThrow(Columns.PRIORITY.getName()));
        bug.product = cursor.getString(cursor.getColumnIndexOrThrow(Columns.PRODUCT.getName()));
        bug.qaContact = new User();
        bug.qaContact.name = cursor.getString(cursor.getColumnIndexOrThrow(Columns.QA_CONTACT_NAME.getName()));
        bug.qaContact.realName = cursor.getString(cursor.getColumnIndexOrThrow(Columns.QA_CONTACT_REAL_NAME.getName()));
        bug.ref = cursor.getString(cursor.getColumnIndexOrThrow(Columns.REF.getName()));
        bug.resolution = cursor.getString(cursor.getColumnIndexOrThrow(Columns.RESOLUTION.getName()));
        bug.severity = cursor.getString(cursor.getColumnIndexOrThrow(Columns.SEVERITY.getName()));
        bug.status = cursor.getString(cursor.getColumnIndexOrThrow(Columns.STATUS.getName()));
        bug.summary = cursor.getString(cursor.getColumnIndexOrThrow(Columns.SUMMARY.getName()));
        bug.targetMilestone = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TARGET_MILESTONE.getName()));
        bug.version = cursor.getString(cursor.getColumnIndexOrThrow(Columns.VERSION.getName()));
        bug.whiteboard = cursor.getString(cursor.getColumnIndexOrThrow(Columns.WHITEBOARD.getName()));
        return bug;
    }

    @Override
    public String toString() {

        Context context = BugDroidApplication.mContext;
        Resources res = context.getResources();

        StringBuffer string = new StringBuffer();
        string.append(res.getString(R.string.bug_title) + ":" + summary + "\n");
        string.append(res.getString(R.string.bug_status) + ":" + status + "\n");
        string.append(res.getString(R.string.bug_resolution) + ":" + resolution + "\n");
        string.append(res.getString(R.string.bug_product) + ":" + product + "\n");
        string.append(res.getString(R.string.bug_component) + ":" + component + "\n");
        string.append(res.getString(R.string.bug_version) + ":" + version + "\n");
        string.append(res.getString(R.string.bug_platform) + ":" + platform + "\n");
        string.append(res.getString(R.string.bug_importance) + ":" + severity + "\n");
        string.append(res.getString(R.string.bug_assigned_to) + ":" + assignedTo.toString() + "\n");
        string.append(res.getString(R.string.bug_qa_contact) + ":" + qaContact.toString() + "\n");
        string.append(res.getString(R.string.bug_target_mil) + ":" + targetMilestone);

        return string.toString();
    }

    public static void bookmark(int _id, boolean newState) {
        Context context = BugDroidApplication.mContext;

        Uri uri = Uri.withAppendedPath(Bugs.CONTENT_URI, String.valueOf(_id));
        ContentValues values = new ContentValues();
        values.put(Columns.BOOKMARK.getName(), newState);
        context.getContentResolver().update(uri, values, null, null);
    }
}
