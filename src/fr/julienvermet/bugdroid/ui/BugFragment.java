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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.service.BugIntentService;
import fr.julienvermet.bugdroid.ui.phone.BugActivity;
import fr.julienvermet.bugdroid.ui.tablet.BugMultiPaneActivity;
import fr.julienvermet.bugdroid.util.UIUtils;

public class BugFragment extends SherlockFragment implements OnCheckedChangeListener,
ViewPager.OnPageChangeListener {

    private static final String BUG_ID = "bugId";
    private static final String BUG_TITLE = "bugTitle";
    private static final String BUG = "bug";

    // UI
    public ViewPager mViewPager;
    private RadioGroup mBugTabs;
    private TextView mListTitle;
    private MenuItem mRefreshMenu, mBookmarkMenu;
    private View mInformations;
    private TextView mInformationsText;
    private ProgressBar mInformationsProgress;

    // Objects
    private BugLoadingListener mBugLoadingListener;
    private int mBugId;
    private String mBugTitle;
    public Bug mBug;
    private Instance mInstance;

    public interface BugLoadingListener {
        void onBugLoading(int bugId);

        void onBugLoaded(Bug bug);
    }

    public static BugFragment newInstance(int bugId, String bugTitle) {
        BugFragment bugFragment = new BugFragment();

        Bundle args = new Bundle();
        args.putInt(BUG_ID, bugId);
        args.putString(BUG_TITLE, bugTitle);
        bugFragment.setArguments(args);

        return bugFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bug, null);

        mViewPager = (ViewPager) view.findViewById(R.id.bugViewPager);
        mViewPager.setOnPageChangeListener(this);

        if (getActivity() instanceof BugActivity || getActivity() instanceof BugMultiPaneActivity) {
            view.findViewById(R.id.titleView).setVisibility(View.GONE);
            view.findViewById(R.id.bugTabsScrollable).setVisibility(View.GONE);
        } else {
            mListTitle = (TextView) view.findViewById(R.id.listTitle);
            mBugTabs = (RadioGroup) view.findViewById(R.id.bugTabs);
            mBugTabs.setOnCheckedChangeListener(this);
            mBugTabs.check(R.id.bugTabDetails);
        }

        mInformations = view.findViewById(R.id.informations);
        mInformationsText = (TextView) mInformations.findViewById(R.id.informationsText);
        mInformationsProgress = (ProgressBar) mInformations.findViewById(R.id.informationsProgress);
        mInformationsText.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
        mInstance = BugDroidApplication.mCurrentInstance;

        if (!UIUtils.isTablet(getActivity())) {
            getSherlockActivity().getSupportActionBar().setTitle(String.valueOf(mBugId));
            mBugLoadingListener = (BugLoadingListener) getActivity();
        }
        Bundle bundle = getArguments();
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
        setListTitle(mBugTitle);
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

    public void bookmark() {
        if (mBookmarkMenu == null || mBug == null) {
            return;
        }

        boolean newState = mBookmarkMenu.isChecked() ? false : true;
        Bug.bookmark(mBug._id, newState);
        setBookmarkMenu(newState);
        mBug.isBookmarked = newState;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (getActivity() instanceof BugActivity) {
            inflater.inflate(R.menu.bug_menu_items, menu);

            mRefreshMenu = menu.findItem(R.id.menu_refresh);
            mBookmarkMenu = menu.findItem(R.id.menu_bookmark);

            if (mBug != null) {
                setBookmarkMenu(mBug.isBookmarked);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            getActivity().finish();
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
            //            Toast.makeText(getActivity(), "Not yet implemented", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void refresh() {
        if (mBugLoadingListener != null) {
            mBugLoadingListener.onBugLoading(mBugId);
        }
        loadBug(false);
    }

    public void loadBug(boolean fromCache) {
        Intent intent = BugIntentService.getIntent(getActivity(), mInstance, mBugId, fromCache);
        Messenger messenger = new Messenger(onBugReceivedHandler);
        intent.putExtra(BugIntentService.MESSENGER, messenger);
        getActivity().startService(intent);
    }

    private void bindBug() {
        onPageSelected(0);
        
        if (mBug != null) {
            setListTitle(mBug.summary);
            mViewPager.setAdapter(new BugFragmentPager(getSherlockActivity()));
            setBookmarkMenu(mBug.isBookmarked);
        } else {
            mInformationsProgress.setVisibility(View.GONE);
            mInformationsText.setVisibility(View.VISIBLE);
            mInformationsText.setText(R.string.bug_error);
        }

        mInformationsProgress.setVisibility(View.GONE);
        if (mBugLoadingListener != null) {
            mBugLoadingListener.onBugLoaded(mBug);
        }
    }

    private void setListTitle(String title) {
        if (getActivity() instanceof BugActivity || getActivity() instanceof BugMultiPaneActivity) {
            getSherlockActivity().getSupportActionBar().setTitle(String.valueOf(mBugId));
            getSherlockActivity().getSupportActionBar().setSubtitle(title);
        } else {
            title = mBugId + " - " + title;
            mListTitle.setText(title);
        }
    }

    Handler onBugReceivedHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            boolean fromCache = msg.getData().getBoolean(BugIntentService.FROM_CACHE);
            if (fromCache) {
                Toast.makeText(getActivity(), "Bug from cache", Toast.LENGTH_SHORT).show();
            }
            mBug = (Bug) msg.getData().getSerializable(BugIntentService.BUG);
            bindBug();
            return false;
        }
    });

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BUG_ID, mBugId);
        outState.putString(BUG_TITLE, mBugTitle);
        outState.putSerializable(BUG, mBug);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
        case R.id.bugTabDetails:
            mViewPager.setCurrentItem(0);
            break;
        case R.id.bugTabComments:
            mViewPager.setCurrentItem(1);
            break;
        case R.id.bugTabAttachments:
            mViewPager.setCurrentItem(2);
            break;
        case R.id.bugTabCcs:
            mViewPager.setCurrentItem(3);
            break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int position) {

        if (getSherlockActivity() instanceof BugActivity) {
            ((BugActivity) getSherlockActivity()).onPageSelected(position);
        }

        if (mBugTabs != null) {
            switch (position) {
            case 0:
                mBugTabs.check(R.id.bugTabDetails);
                break;
            case 1:
                mBugTabs.check(R.id.bugTabComments);
                break;
            case 2:
                mBugTabs.check(R.id.bugTabAttachments);
                break;
            case 3:
                mBugTabs.check(R.id.bugTabCcs);
                break;
            }
        }
    }

    private class BugFragmentPager extends FragmentPagerAdapter {

        public BugFragmentPager(FragmentActivity activity) {
            super(getChildFragmentManager());
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return BugDetailsFragment.newInstance(mBug);
            } else if (position == 1) {
                return BugCommentsFragment.newInstance(mBug);
            } else if (position == 2) {
                return BugAttachmentsFragment.newInstance(mBug);
            } else if (position == 3) {
                return BugCcsFragment.newInstance(mBug);
            }

            return null;
        }
    }

    public void share() {
        if (mBug == null) {
            return;
        }
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");

        String subject = "[" + mBug.product + "] " + mBug.bugId + " - " + mBug.summary;
        String body = mBug.toString();

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        startActivity(intent);
    }

    public void fullscreen() {
        if (mBug != null) {
            Intent intent = BugMultiPaneActivity.getIntent(getActivity(), mBugId, mBugTitle);
            startActivity(intent);
        }
    }
}