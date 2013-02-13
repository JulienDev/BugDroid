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

package fr.julienvermet.bugdroid.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.util.SparseArrayCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.model.Account;
import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.model.Search;
import fr.julienvermet.bugdroid.model.Search.DashboardSearch;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Bugs;
import fr.julienvermet.bugdroid.service.BugIntentService;
import fr.julienvermet.bugdroid.service.BugsIntentService;
import fr.julienvermet.bugdroid.ui.phone.BugActivity;
import fr.julienvermet.bugdroid.ui.tablet.BugMultiPaneActivity;
import fr.julienvermet.bugdroid.util.UIUtils;

public class DashboardFragment extends SherlockFragment {
    
    private ExpandableListView mExpandableListView;
    private SparseArrayCompat<ArrayList<Bug>> mBugs = new SparseArrayCompat<ArrayList<Bug>>();
    private DashboardAdapter mDashboardAdapter;
    private Instance mInstance;
    private int mAccounts_id;

    private enum DashboardElements {
        BOOKMARKS("Bookmarks", 0), 
        THINGS_TO_REVIEW("Things To Review", Search.DASHBOARD_TO_REVIEW), 
        ASSIGNED_BUGS("Assigned Bugs", Search.DASHBOARD_ASSIGNED), 
        REPORTED_BUGS("Reported Bugs", Search.DASHBOARD_REPORTED), 
        CC_BUGS("CC'd Bugs", Search.DASHBOARD_CCD), 
        RECENTLY_FIXED_BUGS("Recently Fixed Bugs", Search.DASHBOARD_RECENTLY_FIXED);

        public String title;
        public int searchType;

        DashboardElements(String title, int searchType) {
            this.title = title;
            this.searchType = searchType;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, null);
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.dashboardList);
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();

        if (mInstance != BugDroidApplication.mCurrentInstance) {
            mInstance = BugDroidApplication.mCurrentInstance;

            int groupCount;
            Account account = mInstance.account;
            if (account == null) {
                mAccounts_id = -1;
                groupCount = 1;
            } else {
                mAccounts_id = account._id;
                groupCount = DashboardElements.values().length;
            }

            for (int i = 1; i < groupCount; i++) {
                DashboardElements element = DashboardElements.values()[i];
                loadBugs(element.ordinal(), element.searchType);
            }
        }
        mDashboardAdapter = new DashboardAdapter();
        mExpandableListView.setAdapter(mDashboardAdapter);
        mExpandableListView.setOnChildClickListener(mDashboardAdapter);

        if (mInstance != null) {
            new BookmarksAsync().execute();
        }
    }

    private class DashboardAdapter extends BaseExpandableListAdapter implements OnChildClickListener {

        private LayoutInflater mInflater;

        public DashboardAdapter() {
            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getGroupCount() {
            if (mInstance == null) {
                return 0;
            } else if (mInstance.account == null) {
                return 1;
            }
            return DashboardElements.values().length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            DashboardElements element = (DashboardElements) getGroup(groupPosition);
            if (mBugs.get(element.ordinal()) == null)
                return 0;

            return mBugs.get(element.ordinal()).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return DashboardElements.values()[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            DashboardElements element = (DashboardElements) getGroup(groupPosition);
            return mBugs.get(element.ordinal()).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            // Todo : Recycle views
            DashboardElements element = (DashboardElements) getGroup(groupPosition);
            View view = mInflater.inflate(R.layout.list_item_dashboard, null);
            TextView textView = (TextView) view.findViewById(R.id.dashboardTitle);
            textView.setText(element.title);

            TextView dashboardBugsCounter = (TextView) view.findViewById(R.id.dashboardBugsCounter);
            ProgressBar progress = (ProgressBar) view.findViewById(R.id.dashboardProgress);
            ArrayList<Bug> bugs = mBugs.get(element.ordinal());
            if (bugs != null) {
                progress.setVisibility(View.GONE);
                dashboardBugsCounter.setVisibility(View.VISIBLE);
                dashboardBugsCounter.setText(String.valueOf(bugs.size()));
            }
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
            ViewGroup parent) {
            // Todo : Recycle views
            Bug bug = (Bug) getChild(groupPosition, childPosition);

            View view = mInflater.inflate(R.layout.list_item_dashboard_bug, null);
            TextView textView = (TextView) view.findViewById(R.id.dashboardBugTitle);
            textView.setText(bug.summary);
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
            long id) {
            Bug bug = (Bug) getChild(groupPosition, childPosition);
            Intent intent;
            if (UIUtils.isTablet(getActivity())) {
                intent = BugMultiPaneActivity.getIntent(getActivity(), bug.bugId, bug.summary);
            } else {
                intent = BugActivity.getIntent(getActivity(), bug.bugId, bug.summary);
            }
            startActivity(intent);
            return true;
        }
    }

    private void loadBugs(int requestCode, int searchType) {
        DashboardSearch dashboardSearch = new DashboardSearch("", requestCode, mInstance, searchType);
        Intent intent = BugsIntentService.getIntent(getActivity(), dashboardSearch);
        Messenger messenger = new Messenger(onBugReceivedHandler);
        intent.putExtra(BugIntentService.MESSENGER, messenger);
        getActivity().startService(intent);
    }

    Handler onBugReceivedHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (getSherlockActivity() != null) {
                Bundle bundle = msg.getData();
                int requestCode = bundle.getInt(BugsIntentService.REQUEST_CODE);
                ArrayList<Bug> bugs = (ArrayList<Bug>) bundle.getSerializable(BugsIntentService.BUGS);
                mBugs.put(requestCode, bugs);
                mDashboardAdapter.notifyDataSetChanged();
            }
            return false;
        }
    });

    private class BookmarksAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String selection = Bugs.Columns.INSTANCES_ID.getName() + "=" + mInstance._id
                + " AND " + Bugs.Columns.ACCOUNTS_ID.getName() + "=" + mAccounts_id
                + " AND " + Bugs.Columns.BOOKMARK.getName() + "=1";
            Cursor cursor = getActivity().getContentResolver().query(Bugs.CONTENT_URI, Bugs.PROJECTION, selection,
                null, null);
            ArrayList<Bug> bugs = new ArrayList<Bug>();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    Bug bug = Bug.toBug(cursor);
                    bugs.add(bug);
                }
            }
            cursor.close();
            mBugs.put(0, bugs);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mDashboardAdapter.notifyDataSetChanged();
        }
    }
}