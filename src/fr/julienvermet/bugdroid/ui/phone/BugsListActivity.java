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

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.model.Search;
import fr.julienvermet.bugdroid.ui.BugsListFragment;

public class BugsListActivity extends SherlockFragmentActivity implements BugsListFragment.BugsLoadingListener {

    private static final String SEARCH = "search";

    //Android
    private ActionBar mActionBar;
    private BugsListFragment mBugsListFragment;
//    public MenuItem mMenuRefresh;
    
    //Objects
    private Search mSearch;
    private boolean mIsLoading = true;
    
    public static Intent getIntent(Context context, Search search) {
        Intent intent = new Intent(context, BugsListActivity.class);
        intent.putExtra(SEARCH, search);
        return intent;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_singlepane_empty);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mSearch = (Search) bundle.getSerializable(SEARCH);
        } else {
            mSearch = (Search) savedInstanceState.getSerializable(SEARCH);
        }

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(mSearch.name);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            mBugsListFragment = (BugsListFragment) getSupportFragmentManager().findFragmentByTag(
                BugsListFragment.class.getSimpleName());
            if (mBugsListFragment == null) {
                mBugsListFragment = BugsListFragment.newInstance(mSearch);
                ft.add(R.id.root_container, mBugsListFragment, BugsListFragment.class.getSimpleName());
                ft.commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        outState.putSerializable(SEARCH, mSearch);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.bugs_menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuRefresh = menu.findItem(R.id.menu_refresh);
        menuRefresh.setVisible(!mIsLoading);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        case R.id.menu_refresh:
            if (mBugsListFragment != null) { 
                setSupportProgressBarIndeterminateVisibility(true);
                mBugsListFragment.refresh();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBugsLoading() {
        mIsLoading = true;
        setSupportProgressBarIndeterminateVisibility(true);
        invalidateOptionsMenu();
    }

    @Override
    public void onBugsLoaded(ArrayList<Bug> bugs) {
        mIsLoading = false;
        setSupportProgressBarIndeterminateVisibility(false);
        invalidateOptionsMenu();
    }
}