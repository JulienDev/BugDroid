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

package fr.julienvermet.bugdroid.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import fr.julienvermet.bugdroid.model.Account;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.model.Product;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Products;
import fr.julienvermet.bugdroid.util.NetworkUtils;

public class ProductsIntentService extends IntentService {

    private static final String QUERY = "query";
    private static final String INSTANCES_ID = "instances_id";
    private static final String ACCOUNTS_ID = "accounts_id";
    public static final String FORCE_RELOAD = "forceReload";
    public static final String MESSENGER = "messenger";
    public static final String PRODUCTS = "products";

    private static final String CONFIGURATION_SUFFIX = "configuration?flags=0";

    public ProductsIntentService() {
        super(ProductsIntentService.class.getSimpleName());
    }

    public static Intent getIntent(Context context, Instance instance, boolean forceReload) {
        Intent intent = new Intent(context, ProductsIntentService.class);
        String query = instance.url + CONFIGURATION_SUFFIX;
        intent.putExtra(QUERY, query);
        intent.putExtra(INSTANCES_ID, instance._id);
        Account account = instance.account;
        if (account != null) {
            intent.putExtra(ACCOUNTS_ID, account._id);
        }
        intent.putExtra(FORCE_RELOAD, forceReload);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();

        Bundle bundle = intent.getExtras();
        String query = bundle.getString(QUERY);
        int instances_id = bundle.getInt(INSTANCES_ID);
        int accounts_id = bundle.getInt(ACCOUNTS_ID, -1);
        boolean forceReload = bundle.getBoolean(FORCE_RELOAD);

        ArrayList<Product> products = null;
        if (!forceReload) {
            String selection = Products.Columns.INSTANCES_ID.getName() + "=" + instances_id
                + " AND " + Products.Columns.ACCOUNTS_ID.getName() + "=" + accounts_id;
            String sortOrder = Products.Columns.NAME.getName();
            Cursor cursor = context.getContentResolver().query(Products.CONTENT_URI, Products.PROJECTION,
                selection, null, sortOrder);
            if (cursor.getCount() > 0) {
                products = new ArrayList<Product>();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    Product product = Product.toProduct(cursor);
                    products.add(product);
                }
                sendResult(intent, products);
                return;
            }
            cursor.close();
        }
        String jsonString = NetworkUtils.readJson(query).result;
        products = parse(jsonString);
        sendResult(intent, products);

        // Delete old products for instance
        String selection = Products.Columns.INSTANCES_ID + "=" + instances_id
            + " AND " + Products.Columns.ACCOUNTS_ID.getName() + "=" + accounts_id;
        context.getContentResolver().delete(Products.CONTENT_URI, selection, null);
        context.getContentResolver().bulkInsert(Products.CONTENT_URI,
            Product.toContentValues(products, instances_id, accounts_id));
    }

    private ArrayList<Product> parse(String jsonString) {
        ArrayList<Product> productsList = new ArrayList<Product>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject productsObject = (JSONObject) jsonObject.get("product");
            JSONArray productsArray = productsObject.names();
            for (int i = 0; i < productsArray.length(); i++) {
                Product product = new Product();
                product.name = productsArray.getString(i);
                JSONObject productObject = (JSONObject) productsObject.get(product.name);
                product.description = productObject.optString("description");
                productsList.add(product);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return productsList;
    }

    private void sendResult(Intent intent, ArrayList<Product> products) {
        Bundle extras = intent.getExtras();
        Messenger messenger = (Messenger) extras.get(MESSENGER);
        if (messenger != null) {
            Message msg = Message.obtain();
            Bundle data = new Bundle();
            data.putSerializable(PRODUCTS, products);
            msg.setData(data);
            try {
                messenger.send(msg);
            } catch (android.os.RemoteException e1) {
                Log.w(getClass().getName(), "Exception sending message", e1);
            }
        }
    }
}