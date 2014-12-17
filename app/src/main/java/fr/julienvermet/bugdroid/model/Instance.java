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

import android.content.ContentValues;
import android.database.Cursor;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Instances;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Instances.Columns;

public class Instance implements Serializable {

    private static final long serialVersionUID = 6357426966223304678L;
    public int _id;
    public String name;
    public String url;
    public Account account;

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Instances.Columns.ID.getName(), _id);
        contentValues.put(Instances.Columns.NAME.getName(), name);
        contentValues.put(Instances.Columns.URL.getName(), url);
        return contentValues;
    }

    public static Instance toInstance(Cursor cursor) {
        Instance instance = new Instance();
        instance._id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.ID.getName()));
        instance.name = cursor.getString(cursor.getColumnIndexOrThrow(Columns.NAME.getName()));
        instance.url = cursor.getString(cursor.getColumnIndexOrThrow(Columns.URL.getName()));
        return instance;
    }
}