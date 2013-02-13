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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.model.Account;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.model.Search.QuickSearch;
import fr.julienvermet.bugdroid.ui.phone.BugActivity;
import fr.julienvermet.bugdroid.ui.phone.BugsListActivity;
import fr.julienvermet.bugdroid.ui.tablet.BugMultiPaneActivity;
import fr.julienvermet.bugdroid.ui.tablet.ProductsBugsMultiPaneFragment;
import fr.julienvermet.bugdroid.ui.tablet.SearchBugsMultiPaneFragment;
import fr.julienvermet.bugdroid.util.UIUtils;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.TabListener,
    ActionBar.OnNavigationListener, SearchView.OnQueryTextListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String TAB_SELECTED = "tabSelected";

    private Tab mTabDashboard, mTabProducts, mTabSearch;
    private Fragment mFragment;
    private Instance mInstance;
    private ActionBar mActionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();

        // We use tab navigation
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mTabProducts = mActionBar.newTab().setText(R.string.ab_products).setTabListener(this);
        mTabDashboard = mActionBar.newTab().setText(R.string.ab_dashboard).setTabListener(this);
        mTabSearch = mActionBar.newTab().setText(R.string.ab_search).setTabListener(this);

        mActionBar.addTab(mTabDashboard);
        mActionBar.addTab(mTabProducts);
        mActionBar.addTab(mTabSearch);

        if (savedInstanceState != null) {
            int selectedTab = savedInstanceState.getInt(TAB_SELECTED);
            mActionBar.setSelectedNavigationItem(selectedTab);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(TAB_SELECTED, mActionBar.getSelectedNavigationIndex());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mInstance == BugDroidApplication.mCurrentInstance) {
            if (mInstance == null) {
                startActivity(new Intent(this, InstancesListActivity.class));
            }
            return;
        }

        mInstance = BugDroidApplication.mCurrentInstance;
        mActionBar.setTitle(mInstance.name);
        Account account = mInstance.account;
        if (account == null) {
            mActionBar.setSubtitle("Without account");
        } else {
            mActionBar.setSubtitle(account.username);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_menu_items, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);

        // If the device is Xlarge, show the non iconified SearchView
        if (UIUtils.isXLarge(this)) {
            searchView.setIconifiedByDefault(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            getSupportActionBar().selectTab(mTabDashboard);
            break;
        case R.id.menu_instance_accounts:
            startActivity(new Intent(this, InstancesListActivity.class));
            break;
        case R.id.menu_about:
            AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
            aboutDialogFragment.show(getSupportFragmentManager(), AboutDialogFragment.class.getSimpleName());
            break;
        case R.id.menu_settings:
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String keywords) {
        // If bug ID input, launch BugActivity
        try {
            int bugId = Integer.parseInt(keywords);
            Intent intent;
            if (UIUtils.isTablet(this)) {
                intent = BugMultiPaneActivity.getIntent(this, bugId, "");
            } else {
                intent = BugActivity.getIntent(this, bugId, "");
            }
            startActivity(intent);
        } catch (NumberFormatException e) {
            QuickSearch quickSearch = new QuickSearch(-1, mInstance, keywords);
            Intent intent = BugsListActivity.getIntent(this, quickSearch);
            startActivity(intent);
        }
        return false;
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        if (tab == mTabDashboard) {
            mFragment = getSupportFragmentManager().findFragmentByTag(DashboardFragment.class.getSimpleName());
            if (mFragment == null) {
                mFragment = new DashboardFragment();
                ft.add(R.id.fragmentBody, mFragment, DashboardFragment.class.getSimpleName());
            } else {
                ft.attach(mFragment);
            }
        } else if (tab == mTabProducts) {
            if (UIUtils.isTablet(getApplicationContext())) {
                mFragment = getSupportFragmentManager().findFragmentByTag(
                    ProductsBugsMultiPaneFragment.class.getSimpleName());
                if (mFragment == null) {
                    mFragment = new ProductsBugsMultiPaneFragment();
                    ft.add(R.id.fragmentBody, mFragment, ProductsBugsMultiPaneFragment.class.getSimpleName());
                } else {
                    ft.attach(mFragment);
                }
            } else {
                mFragment = getSupportFragmentManager().findFragmentByTag(
                    ProductsListFragment.class.getSimpleName());
                if (mFragment == null) {
                    mFragment = new ProductsListFragment();
                    ft.add(R.id.fragmentBody, mFragment, ProductsListFragment.class.getSimpleName());
                } else {
                    ft.attach(mFragment);
                }
            }
        } else if (tab == mTabSearch) {
            if (UIUtils.isTablet(getApplicationContext())) {
                mFragment = getSupportFragmentManager().findFragmentByTag(
                    SearchBugsMultiPaneFragment.class.getSimpleName());
                if (mFragment == null) {
                    mFragment = new SearchBugsMultiPaneFragment();
                    ft.add(R.id.fragmentBody, mFragment, SearchBugsMultiPaneFragment.class.getSimpleName());
                } else {
                    ft.attach(mFragment);
                }
            } else {
                mFragment = getSupportFragmentManager().findFragmentByTag(SearchFragment.class.getSimpleName());
                if (mFragment == null) {
                    mFragment = new SearchFragment();
                    ft.add(R.id.fragmentBody, mFragment, SearchFragment.class.getSimpleName());
                } else {
                    ft.attach(mFragment);
                }
            }
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            ft.detach(mFragment);
        }
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }
}