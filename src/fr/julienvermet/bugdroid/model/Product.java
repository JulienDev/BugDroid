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

package fr.julienvermet.bugdroid.model;

import java.io.Serializable;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Products.Columns;

public class Product implements Serializable {

    private static final long serialVersionUID = -5751123663310027371L;
    public int _id;
    public int instances_id;
    public int accounts_id;
    public String name;
    public String description;

    public static ContentValues toContentValues(Product product) {
        ContentValues values = new ContentValues();
        values.put(Columns.INSTANCES_ID.getName(), product.instances_id);
        values.put(Columns.ACCOUNTS_ID.getName(), product.accounts_id);
        values.put(Columns.NAME.getName(), product.name);
        values.put(Columns.DESCRIPTION.getName(), product.description);
        return values;
    }

    public static ContentValues[] toContentValues(List<Product> products, int instances_id, int accounts_id) {
        ContentValues[] values = new ContentValues[products.size()];
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            product.instances_id = instances_id;
            product.accounts_id = accounts_id;
            values[i] = toContentValues(product);
        }
        return values;
    }

    public static Product toProduct(Cursor cursor) {
        Product product = new Product();
        product._id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.ID.getName()));
        product.instances_id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.INSTANCES_ID.getName()));
        product.accounts_id = cursor.getInt(cursor.getColumnIndexOrThrow(Columns.ACCOUNTS_ID.getName()));
        product.name = cursor.getString(cursor.getColumnIndexOrThrow(Columns.NAME.getName()));
        product.description = cursor.getString(cursor.getColumnIndexOrThrow(Columns.DESCRIPTION.getName()));
        return product;
    }
}