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
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Messenger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.model.Search;
import fr.julienvermet.bugdroid.service.BugIntentService;
import fr.julienvermet.bugdroid.service.BugsIntentService;
import fr.julienvermet.bugdroid.ui.BugFragment.BugLoadingListener;
import fr.julienvermet.bugdroid.ui.phone.BugActivity;
import fr.julienvermet.bugdroid.ui.phone.BugsListActivity;
import fr.julienvermet.bugdroid.ui.tablet.AbsBugsMultiPaneFragment;
import fr.julienvermet.bugdroid.util.UIUtils;

public class BugsListFragment extends SherlockListFragment implements OnItemClickListener {

    private static final String SEARCH = "search";
    private static final String BUGS = "bugs";

    // Android
    private BugAdapter mBugAdapter;
    private LayoutInflater mInflater;

    // UI
    private TextView mListTitle;
    private View mInformations;
    private TextView mInformationsText;
    private ProgressBar mInformationsProgress;

    // Objects
    private Search mSearch;
    private ArrayList<Bug> mBugs;
    private BugsLoadingListener mBugsLoadingListener;
    
    public interface BugsLoadingListener {
        void onBugsLoading();
        void onBugsLoaded(ArrayList<Bug> bugs);
    }

    public static BugsListFragment newInstance(Search search) {
        BugsListFragment bugsListFragment = new BugsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(SEARCH, search);
        bugsListFragment.setArguments(args);
        return bugsListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_with_title, null);

        if (!UIUtils.isTablet(getActivity())) {
            view.findViewById(R.id.titleView).setVisibility(View.GONE);
        } else {
            mListTitle = (TextView) view.findViewById(R.id.listTitle);
            setListTitle();
        }

        mInformations = view.findViewById(R.id.informations);
        mInformationsText = (TextView) mInformations.findViewById(R.id.informationsText);
        mInformationsProgress = (ProgressBar) mInformations.findViewById(R.id.informationsProgress);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (!UIUtils.isTablet(getActivity())) {
            mBugsLoadingListener = (BugsLoadingListener) getActivity();
        }
        
        Bundle bundle = getArguments();
        if (savedInstanceState != null) {
            mSearch = (Search) savedInstanceState.getSerializable(SEARCH);
            mBugs = (ArrayList<Bug>) savedInstanceState.getSerializable(BUGS);
        } else {
            mSearch = (Search) bundle.getSerializable(SEARCH);
            loadBugs();
        }

        getListView().setEmptyView(mInformations);
        mBugAdapter = new BugAdapter();
        getListView().addHeaderView(mInflater.inflate(R.layout.list_header_bugs, null));
        getListView().setAdapter(mBugAdapter);
        getListView().setOnItemClickListener(this);
    }
    
    public void refresh() {
        if (mBugsLoadingListener != null) {
            mBugsLoadingListener.onBugsLoading();
        }
        loadBugs();
    }

    public void loadBugs() {
        mInformationsText.setVisibility(View.GONE);
        mInformationsProgress.setVisibility(View.VISIBLE);

        Intent intent = BugsIntentService.getIntent(getActivity(), mSearch);
        Messenger messenger = new Messenger(onBugReceivedHandler);
        intent.putExtra(BugIntentService.MESSENGER, messenger);
        getActivity().startService(intent);
    }

    private void bindBugs() {
        if (mBugs != null && mBugs.size() > 0) {
            setListTitle();
            mBugAdapter.swapBugs(mBugs);
        } else {
            mInformationsProgress.setVisibility(View.GONE);
            mInformationsText.setVisibility(View.VISIBLE);
            if (mBugs == null) {
                mInformationsText.setText(R.string.bugs_error);
            } else {
                mInformationsText.setText(R.string.bugs_nothing_found);    
            }
        }
        
        if (mBugsLoadingListener != null) {
            mBugsLoadingListener.onBugsLoaded(mBugs);
        }
    }

    private void setListTitle() {
        String subtitle;
        if (mBugs == null) {
            subtitle = getString(R.string.bugs_search_in_progress);
        } else {
            subtitle = String.format(getString(R.string.bugs_found), mBugs.size());
        }

        if (UIUtils.isTablet(getActivity())) {
            mListTitle.setText(subtitle);
        } else {
            getSherlockActivity().getSupportActionBar().setSubtitle(subtitle);
        }
    }

    Handler onBugReceivedHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (getSherlockActivity() != null) {
                mBugs = (ArrayList<Bug>) msg.getData().getSerializable(BugsIntentService.BUGS);
                bindBugs();
            }
            return false;
        }
    });

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(BUGS, mBugs);
        outState.putSerializable(SEARCH, mSearch);
    }

    @Override
    public void onItemClick(AdapterView<?> listView, View arg1, int position, long arg3) {
        if (position == 0) {
            return;
        }

        Bug bug = (Bug) getListView().getItemAtPosition(position);
        if (UIUtils.isTablet(getActivity())) {
            ((AbsBugsMultiPaneFragment)getParentFragment()).onItemClickOnListFragment(this, bug);
        } else {
            Intent intent = BugActivity.getIntent(getActivity(), bug.bugId, bug.summary);
            startActivity(intent);
        }
    }
    
    private class BugAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mBugs == null) {
                return 0;
            }
            return mBugs.size();
        }

        @Override
        public Object getItem(int position) {
            return mBugs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_bug, null);
                holder.bugSummary = (TextView) convertView.findViewById(R.id.bugSummary);
                holder.bugId = (TextView) convertView.findViewById(R.id.bugId);
                holder.bugSeverity = (TextView) convertView.findViewById(R.id.bugSeverity);
                holder.bugStatus = (TextView) convertView.findViewById(R.id.bugStatus);
                holder.bugPriority = (TextView) convertView.findViewById(R.id.bugPriority);
                holder.bugOS = (TextView) convertView.findViewById(R.id.bugOS);
                holder.bugResolution = (TextView) convertView.findViewById(R.id.bugResolution);
                holder.bugAssignee = (TextView) convertView.findViewById(R.id.bugAssignee);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Bug bug = mBugs.get(position);

            holder.bugSummary.setText(bug.summary);
            holder.bugId.setText(String.valueOf(bug.bugId));
            holder.bugSeverity.setText(bug.severity);
            holder.bugStatus.setText(bug.status);
            holder.bugPriority.setText(bug.priority);
            holder.bugOS.setText(bug.opSys);
            holder.bugResolution.setText(bug.resolution);
            holder.bugAssignee.setText(bug.assignedTo.toString());

            return convertView;
        }

        private class ViewHolder {
            TextView bugSummary;
            TextView bugId;
            TextView bugSeverity;
            TextView bugStatus;
            TextView bugPriority;
            TextView bugOS;
            TextView bugResolution;
            TextView bugAssignee;
        }

        public void swapBugs(ArrayList<Bug> bugs) {
            mBugs = bugs;
            notifyDataSetChanged();
        }
    }
}