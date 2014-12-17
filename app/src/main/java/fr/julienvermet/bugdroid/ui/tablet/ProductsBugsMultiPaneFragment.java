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

import com.actionbarsherlock.app.SherlockListFragment;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.model.Product;
import fr.julienvermet.bugdroid.model.Search.ProductSearch;
import fr.julienvermet.bugdroid.ui.BugsListFragment;
import fr.julienvermet.bugdroid.ui.ProductsListFragment;

public class ProductsBugsMultiPaneFragment extends AbsBugsMultiPaneFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        ProductsListFragment productsListFragment = (ProductsListFragment) mFragmentManager.findFragmentByTag(ProductsListFragment.class.getSimpleName());
        if (productsListFragment == null) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            productsListFragment = new ProductsListFragment();
            ft.add(R.id.leftView, productsListFragment, ProductsListFragment.class.getSimpleName());
            ft.commit();
        }
    }

    public void onItemClickOnListFragment(SherlockListFragment fragment, Object data) {
        super.onItemClickOnListFragment(fragment, data);
        
        if (fragment instanceof ProductsListFragment) {
            collapseLeftPane();
            if (!mIsBugsShown) {
                mIsBugsShown = true;
                Animation toLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
                mBugsPane.startAnimation(toLeft);
                mBugsPane.setVisibility(View.VISIBLE);
            } else {
                if (mIsBugShown) {
                    hideBugPane(); 
                }
            }

            Product product = (Product) data;
            mLeftName.setText("Product : " + product.name);

            ProductSearch productSearch = new ProductSearch(-1, mInstance, product.name);
            BugsListFragment bugsListFragment = BugsListFragment.newInstance(productSearch);
            mFragmentManager.beginTransaction().replace(R.id.fragment_container_bugs, bugsListFragment).commit();
        }
    }
}