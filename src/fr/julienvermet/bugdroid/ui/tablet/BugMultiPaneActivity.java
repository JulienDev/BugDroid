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

package fr.julienvermet.bugdroid.ui.tablet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.service.BugIntentService;
import fr.julienvermet.bugdroid.ui.BugAttachmentsFragment;
import fr.julienvermet.bugdroid.ui.BugCcsFragment;
import fr.julienvermet.bugdroid.ui.BugCommentsFragment;
import fr.julienvermet.bugdroid.ui.BugDetailsFragment;

public class BugMultiPaneActivity extends SherlockFragmentActivity {

    private static final String BUG_ID = "bugId";
    private static final String BUG_TITLE = "bugTitle";
    private static final String BUG = "bug";

    //UI
    private MenuItem mRefreshMenu, mBookmarkMenu;
    private FrameLayout mBugDetails, mBugComments, mBugAttachments, mBugCcs;

    // Objects
    private int mBugId;
    private String mBugTitle;
    private Bug mBug;
    private Instance mInstance;

    public static Intent getIntent(Context context, int bugId, String bugTitle) {
        Intent intent = new Intent(context, BugMultiPaneActivity.class);
        intent.putExtra(BUG_ID, bugId);
        intent.putExtra(BUG_TITLE, bugTitle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_bug_multipane);

        mInstance = BugDroidApplication.mCurrentInstance;

        Bundle bundle = getIntent().getExtras();
        if (savedInstanceState != null) {
            mBugId = savedInstanceState.getInt(BUG_ID);
            mBugTitle = savedInstanceState.getString(BUG_TITLE);
            mBug = (Bug) savedInstanceState.getSerializable(BUG);
            bindBug();
        } else {
            mBugId = bundle.getInt(BUG_ID);
            mBugTitle = bundle.getString(BUG_TITLE);
            loadBug(true);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBugDetails = (FrameLayout) findViewById(R.id.bugDetails);
        mBugComments = (FrameLayout) findViewById(R.id.bugComments);
        mBugAttachments = (FrameLayout) findViewById(R.id.bugAttachments);
        mBugCcs = (FrameLayout) findViewById(R.id.bugCcs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportMenuInflater().inflate(R.menu.bug_menu_items, menu);

        mRefreshMenu = menu.findItem(R.id.menu_refresh);
        mBookmarkMenu = menu.findItem(R.id.menu_bookmark);

        if (mBug != null) {
            setBookmarkMenu(mBug.isBookmarked);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        case R.id.menu_refresh:
            refresh();
            break;
        case R.id.menu_bookmark:
            bookmark();
            break;
            // case R.id.menu_edit:
            // break;
        case R.id.menu_share:
            share();
            break;
        default:
//            Toast.makeText(this, "Not yet implemented", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BUG_ID, mBugId);
        outState.putString(BUG_TITLE, mBugTitle);
        outState.putSerializable(BUG, mBug);
    }

    public void refresh() {
        loadBug(false);
    }

    public void bookmark() {
        if (mBookmarkMenu == null) {
            return;
        }

        if (mBookmarkMenu.isChecked()) {
            Bug.bookmark(mBug._id, false);
            setBookmarkMenu(false);
        } else {
            Bug.bookmark(mBug._id, true);
            setBookmarkMenu(true);
        }
    }

    public void share() {
        if (mBug != null) {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");

            String subject = "[" + mBug.product + "] " + mBug.bugId + " - " + mBug.summary;
            String body = mBug.toString();

            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
            startActivity(intent);
        }
    }

    private void setBookmarkMenu(boolean bookmark) {
        if (mBookmarkMenu == null) {
            return;
        }

        if (bookmark) {
            mBookmarkMenu.setIcon(R.drawable.ic_action_star_on);
        } else {
            mBookmarkMenu.setIcon(R.drawable.ic_action_star_off);
        }
        mBookmarkMenu.setChecked(bookmark);
    }

    public void loadBug(boolean fromCache) {
        if (mRefreshMenu != null) {
            mRefreshMenu.setVisible(false);
        }
        setSupportProgressBarIndeterminateVisibility(true);
        Intent intent = BugIntentService.getIntent(this, mInstance, mBugId, fromCache);
        Messenger messenger = new Messenger(onBugReceivedHandler);
        intent.putExtra(BugIntentService.MESSENGER, messenger);
        startService(intent);
    }

    private void bindBug() {
        if (mRefreshMenu != null) {
            mRefreshMenu.setVisible(true);
        }
        setSupportProgressBarIndeterminateVisibility(false);
        setBugTitle(mBug.summary);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        BugDetailsFragment bugDetailsFragment = BugDetailsFragment.newInstance(mBug);
        ft.replace(R.id.bugDetails, bugDetailsFragment, BugDetailsFragment.class.getSimpleName());

        BugCommentsFragment bugCommentsFragment = BugCommentsFragment.newInstance(mBug);
        ft.replace(R.id.bugComments, bugCommentsFragment, BugCommentsFragment.class.getSimpleName());

        if (mBug.attachments.size() > 0) {
            BugAttachmentsFragment bugAttachmentsFragment = BugAttachmentsFragment.newInstance(mBug);
            ft.replace(R.id.bugAttachments, bugAttachmentsFragment, BugAttachmentsFragment.class.getSimpleName()); 
        } else {
            mBugAttachments.setVisibility(View.GONE);
        }

        if (mBug.cc.size() > 0) {
            BugCcsFragment bugCcsFragment = BugCcsFragment.newInstance(mBug);
            ft.replace(R.id.bugCcs, bugCcsFragment, BugCcsFragment.class.getSimpleName()); 
        } else {
            mBugCcs.setVisibility(View.GONE);
        }
        ft.commit();
        setBookmarkMenu(mBug.isBookmarked);
    }

    private void setBugTitle(String title) {
        getSupportActionBar().setTitle(String.valueOf(mBugId));
        getSupportActionBar().setSubtitle(title);
    }

    Handler onBugReceivedHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            boolean fromCache = msg.getData().getBoolean(BugIntentService.FROM_CACHE);
            if (fromCache) {
                Toast.makeText(BugMultiPaneActivity.this, "Bug from cache", Toast.LENGTH_SHORT).show();
            }
            mBug = (Bug) msg.getData().getSerializable(BugIntentService.BUG);
            bindBug();
            return false;
        }
    });
}