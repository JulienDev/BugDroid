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
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Ccs;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Ccs.Columns;

public class Cc extends User implements Serializable {

    private static final long serialVersionUID = -4808759883130841992L;
    public int _id;
    public int bugs_id;
    
    public static ContentValues toContentValues(Cc cc) {
        ContentValues values = new ContentValues();
        values.put(Columns.BUGS_ID.getName(), cc.bugs_id);
        values.put(Columns.NAME.getName(), cc.name);
        values.put(Columns.REAL_NAME.getName(), cc.realName);
        return values;
    }

    public static ContentValues[] toContentValues(List<Cc> ccs, int bugId) {
        ContentValues[] values = new ContentValues[ccs.size()];
        for (int i = 0; i < ccs.size(); i++) {
            Cc cc = ccs.get(i);
            cc.bugs_id = bugId;
            values[i] = toContentValues(cc);
        }
        return values;
    }

    public static Cc toCc(Cursor cursor) {
        Cc cc = new Cc();

        cc._id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.ID.getName()));
        cc.bugs_id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.BUGS_ID.getName()));
        cc.name = cursor.getString(cursor.getColumnIndexOrThrow(Columns.NAME.getName()));
        cc.realName = cursor.getString(cursor.getColumnIndexOrThrow(Columns.REAL_NAME.getName()));
        return cc;
    }

    public static ArrayList<Cc> getCcsForBug(int bugs_id) {
        ArrayList<Cc> ccs = new ArrayList<Cc>();
        Context context = BugDroidApplication.mContext;
        String selection = Columns.BUGS_ID + "=" + bugs_id;
        Cursor cursor = context.getContentResolver().query(Ccs.CONTENT_URI, Ccs.PROJECTION, selection, null,
            null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                ccs.add(toCc(cursor));
            }
        }
        cursor.close();
        return ccs;
    }
}