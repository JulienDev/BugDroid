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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.model.Product;
import fr.julienvermet.bugdroid.model.Search.ProductSearch;
import fr.julienvermet.bugdroid.service.ProductsIntentService;
import fr.julienvermet.bugdroid.ui.phone.BugsListActivity;
import fr.julienvermet.bugdroid.ui.tablet.ProductsBugsMultiPaneFragment;
import fr.julienvermet.bugdroid.util.UIUtils;

public class ProductsListFragment extends SherlockListFragment implements OnItemClickListener {

    // Android
    private LayoutInflater mInflater;
    private ProductsAdapter mProductsAdapter;

    // UI
//    private TextView mListInformations;
    private View mInformations;
    private TextView mInformationsText;
    private ProgressBar mInformationsProgress;
    private TextView mListTitle;

    // Objects
    private ArrayList<Product> mProducts = new ArrayList<Product>();
    private Instance mInstance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_with_title, null);
        if (!UIUtils.isTablet(getActivity())) {
            view.findViewById(R.id.titleView).setVisibility(View.GONE);
        } else {
            mListTitle = (TextView) view.findViewById(R.id.listTitle);
            mListTitle.setText(R.string.products);
        }
        
//        mListInformations = (TextView) view.findViewById(R.id.listInformations);
//        mListInformations.setText(R.string.products_loading);
        
        mInformations = view.findViewById(R.id.informations);
        mInformationsText = (TextView) mInformations.findViewById(R.id.informationsText);
        mInformationsProgress = (ProgressBar) mInformations.findViewById(R.id.informationsProgress);
        
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInstance = BugDroidApplication.mCurrentInstance;

        mProductsAdapter = new ProductsAdapter();

        getListView().setEmptyView(mInformations);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        getListView().setAdapter(mProductsAdapter);
        getListView().setOnItemClickListener(this);

        loadProducts();
    }

    private void loadProducts() {
        mInformationsText.setVisibility(View.GONE);
        mInformationsProgress.setVisibility(View.VISIBLE);
        
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
        Intent intent = ProductsIntentService.getIntent(getActivity(), mInstance, false);
        Messenger messenger = new Messenger(onProductsReceivedHandler);
        intent.putExtra(ProductsIntentService.MESSENGER, messenger);
        getActivity().startService(intent);
    }

    private void bindProducts() {
        if (mProducts.size() > 0) {
            mProductsAdapter.notifyDataSetChanged();
        } else {
            mInformationsProgress.setVisibility(View.GONE);
            mInformationsText.setVisibility(View.VISIBLE);
            mInformationsText.setText(R.string.products_nothing_found);
        }
    }

    Handler onProductsReceivedHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (getSherlockActivity() != null) {
                mProducts = (ArrayList<Product>) msg.getData().getSerializable(ProductsIntentService.PRODUCTS);
                bindProducts();
                getSherlockActivity().setSupportProgressBarIndeterminateVisibility(false);
            }
            return false;
        }
    });

    private class ProductsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_product, null);
                holder.tvProductName = (TextView) convertView.findViewById(R.id.productName);
                holder.tvProductDescription = (TextView) convertView.findViewById(R.id.tvProductDescription);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Product product = (Product) getItem(position);
            holder.tvProductName.setText(product.name);
            holder.tvProductDescription.setText(product.description);

            return convertView;
        }

        private class ViewHolder {
            TextView tvProductName;
            TextView tvProductDescription;
        }

        @Override
        public int getCount() {
            if (mProducts == null) {
                return 0;
            }
            return mProducts.size();
        }

        @Override
        public Object getItem(int position) {
            return mProducts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> listView, View arg1, int position, long arg3) {

        getListView().setSelection(position);
        getListView().setItemChecked(position, true);

        Product produt = mProducts.get(position);
        ProductSearch productSearch = new ProductSearch(-1, mInstance, produt.name);
        if (UIUtils.isTablet(getActivity())) {
            ProductsBugsMultiPaneFragment productsFragment = (ProductsBugsMultiPaneFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(ProductsBugsMultiPaneFragment.class.getSimpleName());
            productsFragment.onItemClickOnListFragment(this, produt);
        } else {
            Intent intent = BugsListActivity.getIntent(getActivity(), productSearch);
            startActivity(intent);
        }
    }
}