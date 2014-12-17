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

package fr.julienvermet.bugdroid.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import fr.julienvermet.bugdroid.model.Account;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Accounts;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Instances;
import fr.julienvermet.bugdroid.ui.InstancesListFragment;

public class BugDroidApplication extends Application implements OnSharedPreferenceChangeListener {

    // Android
    public static Context mContext;
    private static SharedPreferences mPrefs;

    // Objects
    public static Instance mCurrentInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mPrefs.registerOnSharedPreferenceChangeListener(this);

        setDefaultInstance();
    }

    private static void setDefaultInstance() {

        int instanceId = mPrefs.getInt(InstancesListFragment.INSTANCE_ID, -1);
        int accountId = mPrefs.getInt(InstancesListFragment.ACCOUNT_ID, -1);

        Account account = null;

        if (accountId >= 0) {
            Uri uriAccount = Uri.withAppendedPath(Accounts.CONTENT_URI, String.valueOf(accountId));
            Cursor cursorAccount = mContext.getContentResolver().query(uriAccount, Accounts.PROJECTION, null,
                null, null);
            if (cursorAccount.getCount() > 0) {
                cursorAccount.moveToFirst();
                account = Account.toAccount(cursorAccount);
            }
            cursorAccount.close();
        }

        if (instanceId >= 0) {
            Uri uri = Uri.withAppendedPath(Instances.CONTENT_URI, String.valueOf(instanceId));
            Cursor cursorInstance = mContext.getContentResolver().query(uri, Instances.PROJECTION, null,
                null, null);
            if (cursorInstance.getCount() > 0) {
                cursorInstance.moveToFirst();
                mCurrentInstance = Instance.toInstance(cursorInstance);
                mCurrentInstance.account = account;
            }
            cursorInstance.close();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(InstancesListFragment.ACCOUNT_ID) || key.equals(InstancesListFragment.INSTANCE_ID)) {
            setDefaultInstance();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }
}