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

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.model.Search.FormSearch;
import fr.julienvermet.bugdroid.ui.BugsListFragment;
import fr.julienvermet.bugdroid.ui.SearchFragment;

public class SearchBugsMultiPaneFragment extends AbsBugsMultiPaneFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SearchFragment searchFragment = (SearchFragment) mFragmentManager.findFragmentByTag(SearchFragment.class.getSimpleName());
        if (searchFragment == null) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            searchFragment = new SearchFragment();
            ft.add(R.id.leftView, searchFragment, SearchFragment.class.getSimpleName());
            ft.commit();
        }
    }

    public void onSearch(FormSearch search) {
        collapseLeftPane();
        Animation toLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
        mBugsPane.startAnimation(toLeft);
        mBugsPane.setVisibility(View.VISIBLE);

        mLeftName.setText("Query : " + search.name);
        mLeftName.setVisibility(View.VISIBLE);

        BugsListFragment bugsListFragment = BugsListFragment.newInstance(search);
        mFragmentManager.beginTransaction().replace(R.id.fragment_container_bugs, bugsListFragment).commit();
    }

    //    public void onItemClickOnListFragment(SherlockListFragment fragment, Object data) {
    //        super.onItemClickOnListFragment(fragment, data);
    //        
    //        if (fragment instanceof ProductsListFragment) {
    //            appearProductsPane();
    //            Animation toLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
    //            mBugsPane.startAnimation(toLeft);
    //            mBugsPane.setVisibility(View.VISIBLE);
    //
    //            Product product = (Product) data;
    //            mLeftName.setText("Product : " + product.name);
    //            mLeftName.setVisibility(View.VISIBLE);
    //
    //            ProductSearch productSearch = new ProductSearch(-1, mInstance, product.name);
    //            BugsListFragment bugsListFragment = BugsListFragment.newInstance(productSearch);
    //            mFragmentManager.beginTransaction().replace(R.id.fragment_container_bugs, bugsListFragment).commit();
    //        }
    //    }
}