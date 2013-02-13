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

import android.database.Cursor;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Accounts.Columns;

public class Account implements Serializable {

    private static final long serialVersionUID = -2274154012859845136L;
    public int _id;
    public int instances_id;
    public String username;
    public String password;

    public static Account toAccount(Cursor cursor) {
        Account account = new Account();
        account._id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.ID.getName()));
        account.instances_id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.INSTANCES_ID.getName()));
        account.username = cursor.getString(cursor.getColumnIndexOrThrow(Columns.EMAIL.getName()));
        account.password = cursor.getString(cursor.getColumnIndexOrThrow(Columns.PASSWORD.getName()));
        return account;
    }
}