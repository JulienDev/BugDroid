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

package fr.julienvermet.bugdroid.ui.phone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Spinner;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.ui.BugFragment;

public class BugActivity extends SherlockFragmentActivity implements ActionBar.TabListener,
    ViewPager.OnPageChangeListener, BugFragment.BugLoadingListener {

    private static final String BUG_ID = "bugId";
    private static final String BUG_TITLE = "bugTitle";

    // Android
    private Tab mTabDetails, mTabComments, mTabAttachments, mTabCcs;
    public BugFragment mBugFragment;
    private ActionBar mActionBar;

    // Objects
    private int mBugId;
    private String mBugTitle;

    public static Intent getIntent(Context context, int bugId, String bugTitle) {
        Intent intent = new Intent(context, BugActivity.class);
        intent.putExtra(BUG_ID, bugId);
        intent.putExtra(BUG_TITLE, bugTitle);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_singlepane_empty);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mBugId = bundle.getInt(BUG_ID);
            mBugTitle = bundle.getString(BUG_TITLE);
        } else {
            mBugId = savedInstanceState.getInt(BUG_ID);
            mBugTitle = savedInstanceState.getString(BUG_TITLE);
        }

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(String.valueOf(mBugId));

        // We use tab navigation
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mTabDetails = mActionBar.newTab().setText("Details").setTabListener(this);
        mActionBar.addTab(mTabDetails);
        mTabComments = mActionBar.newTab().setText("Comments").setTabListener(this);
        mActionBar.addTab(mTabComments);
        mTabAttachments = mActionBar.newTab().setText("Attachments").setTabListener(this);
        mActionBar.addTab(mTabAttachments);
        mTabCcs = mActionBar.newTab().setText("Ccs").setTabListener(this);
        mActionBar.addTab(mTabCcs);

        mBugFragment = (BugFragment) getSupportFragmentManager().findFragmentByTag(
            BugFragment.class.getSimpleName());
        if (mBugFragment == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            mBugFragment = BugFragment.newInstance(mBugId, mBugTitle);
            ft.add(R.id.root_container, mBugFragment, BugFragment.class.getSimpleName());
            ft.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BUG_ID, mBugId);
        outState.putString(BUG_TITLE, mBugTitle);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (mBugFragment != null && mBugFragment.mViewPager != null) {
            mBugFragment.mViewPager.setCurrentItem(tab.getPosition());
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position) {
        mActionBar.setSelectedNavigationItem(position);

        mActionBar.getTabAt(position).select();
        ViewParent root = findViewById(android.R.id.content).getParent();
        findAndUpdateSpinner(root, position);
    }

    /**
     * Searches the view hierarchy excluding the content view for a possible
     * Spinner in the ActionBar.
     * 
     * @param root
     *            The parent of the content view
     * @param position
     *            The position that should be selected
     * @return if the spinner was found and adjusted
     */
    private boolean findAndUpdateSpinner(Object root, int position) {
        if (root instanceof android.widget.Spinner) {
            // Found the Spinner
            Spinner spinner = (Spinner) root;
            spinner.setSelection(position);
            return true;
        } else if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            if (group.getId() != android.R.id.content) {
                // Found a container that isn't the container holding our screen
                // layout
                for (int i = 0; i < group.getChildCount(); i++) {
                    if (findAndUpdateSpinner(group.getChildAt(i), position)) {
                        // Found and done searching the View tree
                        return true;
                    }
                }
            }
        }
        // Nothing found
        return false;
    }

    @Override
    public void onBugLoading(int bugId) {
        setSupportProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void onBugLoaded(Bug bug) {
        setSupportProgressBarIndeterminateVisibility(false);
    }
}