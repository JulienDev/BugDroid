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

import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockFragment;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.model.Pair;
import fr.julienvermet.bugdroid.model.Product;
import fr.julienvermet.bugdroid.model.Search.FormSearch;
import fr.julienvermet.bugdroid.service.ProductsIntentService;
import fr.julienvermet.bugdroid.ui.phone.BugsListActivity;
import fr.julienvermet.bugdroid.ui.tablet.SearchBugsMultiPaneFragment;
import fr.julienvermet.bugdroid.util.UIUtils;

public class SearchFragment extends SherlockFragment implements OnItemSelectedListener, OnEditorActionListener,
    OnClickListener {

    private Spinner mStatus;
    private Spinner mProducts;
    private EditText mWords;
    private Button mSearch;
    private Instance mInstance;
    private ArrayList<Product> mProductsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);
        mStatus = (Spinner) view.findViewById(R.id.searchStatus);
        mProducts = (Spinner) view.findViewById(R.id.searchProducts);
        mWords = (EditText) view.findViewById(R.id.searchWords);
        mSearch = (Button) view.findViewById(R.id.search);

        mStatus.setOnItemSelectedListener(this);
        mProducts.setOnItemSelectedListener(this);
        mWords.setOnEditorActionListener(this);
        mSearch.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mInstance = BugDroidApplication.mCurrentInstance;
        loadProducts();
    }

    private void loadProducts() {
        getSherlockActivity().setSupportProgressBarIndeterminateVisibility(true);
        Intent intent = ProductsIntentService.getIntent(getActivity(), mInstance, false);
        Messenger messenger = new Messenger(onProductsReceivedHandler);
        intent.putExtra(ProductsIntentService.MESSENGER, messenger);
        getActivity().startService(intent);
    }

    private void bindProducts() {
        int productsListSize = mProductsList.size();
        String[] choices = new String[productsListSize];
        for (int i = 0; i < productsListSize; i++) {
            choices[i] = mProductsList.get(i).name;
        }
        ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
            choices);
        mProducts.setAdapter(a);
    }

    Handler onProductsReceivedHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (getSherlockActivity() != null) {
                mProductsList = (ArrayList<Product>) msg.getData().getSerializable(ProductsIntentService.PRODUCTS);
                bindProducts();
            }
            return false;
        }
    });

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view == mStatus) {

        } else if (view == mProducts) {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
        case EditorInfo.IME_ACTION_SEARCH:
            search();
            return true;
        default:
            return false;
        }
    }
    
    private FormSearch createSearch() {
        ArrayList<Pair<String, String>> formValues = new ArrayList<Pair<String, String>>();
        String bugStatus = getResources().getStringArray(R.array.search_status_entries_values)[mStatus
            .getSelectedItemPosition()];
        formValues.add(new Pair<String, String>("bug_status", bugStatus));
        String product = (String) mProducts.getSelectedItem();
        String productEncoded = URLEncoder.encode(product);
        formValues.add(new Pair<String, String>("product", productEncoded));
        String words = mWords.getText().toString();
        String contentEncoded = URLEncoder.encode(words);
        formValues.add(new Pair<String, String>("content", contentEncoded));
        
        String queryName = words;
        if (TextUtils.isEmpty(queryName)) {
            queryName = product;
        }
        return new FormSearch(queryName, -1, mInstance, formValues);
    }

    private void search() {
        FormSearch formSearch = createSearch();
        if (UIUtils.isTablet(getActivity())) {
            SearchBugsMultiPaneFragment searchFragment = (SearchBugsMultiPaneFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(SearchBugsMultiPaneFragment.class.getSimpleName());
            searchFragment.onSearch(formSearch);
        } else {
            Intent intent = BugsListActivity.getIntent(getActivity(), formSearch);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mSearch) {
            search();
        }
    }
}