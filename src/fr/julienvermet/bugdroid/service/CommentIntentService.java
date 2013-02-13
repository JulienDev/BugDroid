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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import fr.julienvermet.bugdroid.model.Account;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.util.NetworkUtils;
import fr.julienvermet.bugdroid.util.NetworkUtils.NetworkResult;

public class CommentIntentService extends IntentService {

    private static final String QUERY = "query";
    private static final String BUG_ID = "bugId";
    public static final String MESSENGER = "messenger";
    public static final String JSON_DATA = "jsonData";
    public static final String STATUS_CODE = "statusCode";
    public static final String RESULT = "result";

    private static final String COMMENT_SUFFIX = "bug/%d/comment";
    private static final String ACCOUNT_SUFFIX = "?username=%s&password=%s";

    public CommentIntentService() {
        super(CommentIntentService.class.getSimpleName());
    }

    public static Intent getIntent(Context context, Instance instance, int bugId, JSONObject jsonData) {
        Intent intent = new Intent(context, CommentIntentService.class);
        String accountSuffix = "";
        Account account = instance.account;
        if (account != null) {
            accountSuffix = String.format(ACCOUNT_SUFFIX, account.username, account.password);
        }

        String bugSuffix = String.format(COMMENT_SUFFIX, bugId);
        String query = instance.url.concat(bugSuffix).concat(accountSuffix);

        intent.putExtra(JSON_DATA, jsonData.toString());
        intent.putExtra(QUERY, query);
        intent.putExtra(BUG_ID, bugId);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        String query = bundle.getString(QUERY);
        String jsonDataString = bundle.getString(JSON_DATA);
        try {
            JSONObject jsonData = new JSONObject(jsonDataString);
            NetworkResult networkResult = NetworkUtils.postJson(query, jsonData);
            sendResult(intent, networkResult.statusCode, networkResult.result);
        } catch (JSONException e) {
            sendResult(intent, 0, "JSONException");
            e.printStackTrace();
        }
    }

    private void sendResult(Intent intent, int statusCode, String result) {
        Bundle extras = intent.getExtras();
        Messenger messenger = (Messenger) extras.get(MESSENGER);
        if (messenger != null) {
            Message msg = Message.obtain();
            Bundle data = new Bundle();
            data.putInt(STATUS_CODE, statusCode);
            data.putString(RESULT, result);
            msg.setData(data);
            try {
                messenger.send(msg);
            } catch (android.os.RemoteException e1) {
                Log.w(getClass().getName(), "Exception sending message", e1);
            }
        }
    }
}