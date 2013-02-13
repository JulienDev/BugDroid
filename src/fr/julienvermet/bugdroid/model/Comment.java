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
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Comments;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Comments.Columns;

public class Comment implements Serializable {

    private static final long serialVersionUID = 349651992386144173L;
    public int _id;
    public int bugs_id;
    @SerializedName("creation_time")
    public String creationTime;
    @SerializedName("creator")
    public User creator;
    @SerializedName("id")
    public int commentId;
    @SerializedName("is_public")
    public boolean isPublic;
    @SerializedName("text")
    public String text;

    public static ContentValues toContentValues(Comment comment) {
        ContentValues values = new ContentValues();
        values.put(Columns.COMMENT_ID.getName(), comment.commentId);
        values.put(Columns.BUGS_ID.getName(), comment.bugs_id);
        values.put(Columns.CREATION_TIME.getName(), comment.creationTime);
        values.put(Columns.CREATOR_NAME.getName(), comment.creator.name);
        values.put(Columns.CREATOR_REAL_NAME.getName(), comment.creator.realName);
        values.put(Columns.TEXT.getName(), comment.text);
        return values;
    }

    public static ContentValues[] toContentValues(List<Comment> comments, int bugId) {
        ContentValues[] values = new ContentValues[comments.size()];
        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            comment.bugs_id = bugId;
            values[i] = toContentValues(comment);
        }
        return values;
    }

    public static Comment toComment(Cursor cursor) {
        Comment comment = new Comment();
        comment._id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.ID.getName()));
        comment.commentId = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.COMMENT_ID.getName()));
        comment.bugs_id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.BUGS_ID.getName()));
        comment.creationTime = cursor
            .getString(cursor.getColumnIndexOrThrow(Columns.CREATION_TIME.getName()));
        comment.creator = new User();
        comment.creator.name = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CREATOR_NAME.getName()));
        comment.creator.realName = cursor.getString(cursor.getColumnIndexOrThrow(Columns.CREATOR_REAL_NAME
            .getName()));
        comment.text = cursor.getString(cursor.getColumnIndexOrThrow(Columns.TEXT.getName()));
        return comment;
    }

    public static ArrayList<Comment> getCommentsForBug(int bugs_id) {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        Context context = BugDroidApplication.mContext;
        String selection = Columns.BUGS_ID + "=" + bugs_id;
        Cursor cursor = context.getContentResolver().query(Comments.CONTENT_URI, Comments.PROJECTION,
            selection, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                comments.add(toComment(cursor));
            }
        }
        cursor.close();
        return comments;
    }
}