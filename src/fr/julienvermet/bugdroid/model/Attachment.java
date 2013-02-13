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

import com.google.gson.annotations.SerializedName;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Attachments;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Attachments.Columns;

public class Attachment implements Serializable {

    private static final long serialVersionUID = -5952272654204611035L;
    public int _id;
    @SerializedName("attacher")
    public User attacher;
    @SerializedName("bug_id")
    public int bugs_id;
    @SerializedName("bug_ref")
    public String bugRef;
    @SerializedName("content_type")
    public String contentType;
    @SerializedName("creation_time")
    public String creationTime;
//    @SerializedName("data")
//    public String data;
    @SerializedName("description")
    public String description;
    @SerializedName("encoding")
    public String encoding;
    @SerializedName("file_name")
    public String fileName;
    @SerializedName("flags")
    public List<Flag> flags = new ArrayList<Flag>();
    @SerializedName("id")
    public int attachmentId;
    @SerializedName("is_obsolete")
    public boolean isObsolete;
    @SerializedName("is_patch")
    public boolean isPatch;
    @SerializedName("is_public")
    public boolean isPublic;
    @SerializedName("last_change_time")
    public String lastChangeTime;
    @SerializedName("ref")
    public String ref;
    @SerializedName("size")
    public int size;

    public static ContentValues toContentValues(Attachment attachment) {
        ContentValues values = new ContentValues();
        values.put(Columns.ATTACHMENT_ID.getName(), attachment.attachmentId);
        values.put(Columns.BUGS_ID.getName(), attachment.bugs_id);
        values.put(Columns.CREATION_TIME.getName(), attachment.creationTime);
        values.put(Columns.ATTACHER_NAME.getName(), attachment.attacher.name);
        values.put(Columns.ATTACHER_REAL_NAME.getName(), attachment.attacher.realName);
        values.put(Columns.DESCRIPTION.getName(), attachment.description);
        return values;
    }

    public static ContentValues[] toContentValues(List<Attachment> attachments, int bugs_id) {
        ContentValues[] values = new ContentValues[attachments.size()];
        for (int i = 0; i < attachments.size(); i++) {
            Attachment attachment = attachments.get(i);
            attachment.bugs_id = bugs_id;
            values[i] = toContentValues(attachment);
        }
        return values;
    }

    public static Attachment toAttachment(Cursor cursor) {
        Attachment attachment = new Attachment();
        attachment._id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.ID.getName()));
        attachment.attachmentId = cursor
            .getInt(cursor.getColumnIndexOrThrow(Columns.ATTACHMENT_ID.getName()));
        attachment.bugs_id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.BUGS_ID.getName()));
        attachment.creationTime = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CREATION_TIME
            .getName()));
        attachment.attacher = new User();
        attachment.attacher.name = cursor.getString(cursor.getColumnIndexOrThrow(Columns.ATTACHER_NAME
            .getName()));
        attachment.attacher.realName = cursor.getString(cursor
            .getColumnIndexOrThrow(Columns.ATTACHER_REAL_NAME.getName()));
        attachment.description = cursor
            .getString(cursor.getColumnIndexOrThrow(Columns.DESCRIPTION.getName()));
        return attachment;
    }

    public static ArrayList<Attachment> getAttachmentsForBug(int bugs_id) {
        ArrayList<Attachment> attachments = new ArrayList<Attachment>();
        Context context = BugDroidApplication.mContext;
        String selection = Columns.BUGS_ID + "=" + bugs_id;
        Cursor cursor = context.getContentResolver().query(Attachments.CONTENT_URI, Attachments.PROJECTION,
            selection, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                attachments.add(toAttachment(cursor));
            }
        }
        cursor.close();
        return attachments;
    }
}