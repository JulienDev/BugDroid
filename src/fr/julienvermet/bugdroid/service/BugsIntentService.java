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

import java.util.ArrayList;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.model.Bugs;
import fr.julienvermet.bugdroid.model.Search;
import fr.julienvermet.bugdroid.util.NetworkUtils;

public class BugsIntentService extends IntentService {

    public static final String BUGS = "bugs";
    public static final String SEARCH_TYPE = "searchType";
    private static final String SEARCH = "query";
    public static final String MESSENGER = "messenger";
    public static final String REQUEST_CODE = "requestCode";

    public BugsIntentService() {
        super(BugsIntentService.class.getSimpleName());
    }

    public static Intent getIntent(Context context, Search search) {
        Intent intent = new Intent(context, BugsIntentService.class);
        intent.putExtra(SEARCH, search);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        Search search = (Search) bundle.getSerializable(SEARCH);
        String query = search.getQuery();
        int requestCode = search.requestCode;
        String jsonString = NetworkUtils.readJson(query).result;
        try {
            Gson gson = new Gson();
            Bugs bugs = gson.fromJson(jsonString, Bugs.class);

            Bundle extras = intent.getExtras();
            Messenger messenger = (Messenger) extras.get(MESSENGER);
            if (messenger != null) {
                Message msg = Message.obtain();
                Bundle data = new Bundle();
                data.putInt(REQUEST_CODE, requestCode);
                if (bugs != null) {
                    data.putSerializable(BUGS, (ArrayList<Bug>) bugs.bugs); 
                }
                msg.setData(data);
                try {
                    messenger.send(msg);
                } catch (android.os.RemoteException e1) {
                    Log.w(getClass().getName(), "Exception sending message", e1);
                }
            }
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}