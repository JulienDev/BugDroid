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
import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.ui.SettingsActivity;

public abstract class Search implements Serializable {

    private static final long serialVersionUID = 4902715531248845223L;
    // Vars
    public static final String BUG_SUFFIX = "bug?";
    public static String CHANGED_AFTER_SUFFIX = "&changed_after=%s";
    private static final String ACCOUNT_SUFFIX = "username=%s&password=%s";

    // Search queries
    private static final String BUGS_QUICK_SEARCH = "&quicksearch=%s";
    // TODO : Limit fields
    private static final String BUGS_PRODUCT_SUFFIX = "&product=%s&changed_after=12h";
    private static final String DASHBOARD_QUERY_TO_REVIEW = "&status=NEW&status=UNCONFIRMED&status=ASSIGNED&status=REOPENED&flag.requestee=%s"; // 2012-09-21T23%3A08%3A19Z
    private static final String DASHBOARD_QUERY_REPORTED = "&status=NEW&status=UNCONFIRMED&status=ASSIGNED&status=REOPENED&email1=%s&email1_type=equals&email1_creator=1&email2=%s&email2_type=not_equals&email2_assigned_to=1";
    private static final String DASHBOARD_QUERY_ASSIGNED = "&status=NEW&status=UNCONFIRMED&status=ASSIGNED&status=REOPENED&email1=%s&email1_type=equals&email1_assigned_to=1";
    private static final String DASHBOARD_QUERY_CCD = "&status=NEW&status=UNCONFIRMED&status=ASSIGNED&status=REOPENED&email1=%s&email1_type=equals&email1_cc=1&email2=%s&email2_type=not_equals&email2_assigned_to=1&email2_creator=1";
    private static final String DASHBOARD_QUERY_RECENTLY_FIXED = "&resolution=FIXED&email1=%s&email1_type=equals&email1_assigned_to=1&email1_creator=1&email1_cc=1";

    // Search types
    public static final int QUICK_SEARCH = 0;
    public static final int PRODUCT_NAME = QUICK_SEARCH + 1;
    public static final int DASHBOARD_TO_REVIEW = PRODUCT_NAME + 1;
    public static final int DASHBOARD_REPORTED = DASHBOARD_TO_REVIEW + 1;
    public static final int DASHBOARD_ASSIGNED = DASHBOARD_REPORTED + 1;
    public static final int DASHBOARD_CCD = DASHBOARD_ASSIGNED + 1;
    public static final int DASHBOARD_RECENTLY_FIXED = DASHBOARD_CCD + 1;

    public String name = "";
    public int requestCode;
    private Instance instance;
    private static Account account;

    public Search(int requestCode, Instance instance) {
        super();
        this.requestCode = requestCode;
        this.instance = instance;
        this.account = getAccount();
    }

    public Search(String name, int requestCode, Instance instance) {
        super();
        this.name = name;
        this.requestCode = requestCode;
        this.instance = instance;
        this.account = getAccount();
    }

    private Account getAccount() {
        return instance.account;
    }

    protected abstract String buildQuery();

    public String getQuery() {
        String accountSuffix = "";
        if (account != null) {
            accountSuffix = String.format(ACCOUNT_SUFFIX, account.username, account.password);  
        }

        Context context = BugDroidApplication.mContext;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String changedAfterValue = prefs.getString(SettingsActivity.KEY_SEARCH_PERIOD, "7d");
        String changedAfter = String.format(CHANGED_AFTER_SUFFIX, changedAfterValue);

        StringBuilder sb = new StringBuilder();
        sb.append(instance.url);
        sb.append(BUG_SUFFIX);
        sb.append(accountSuffix);
        sb.append(changedAfter);
        sb.append(buildQuery());
        return sb.toString();
    }

    public static class ProductSearch extends Search {

        private static final long serialVersionUID = 3847198290311299655L;
        public String productName;

        public ProductSearch(int requestCode, Instance instance, String productName) {
            super(productName, requestCode, instance);
            this.productName = productName;
        }

        @Override
        protected String buildQuery() {
            productName = URLEncoder.encode(productName);
            return String.format(BUGS_PRODUCT_SUFFIX, productName);
        }
    }

    public static class QuickSearch extends Search {

        private static final long serialVersionUID = -6877783643462458265L;
        public String keywords;

        public QuickSearch(int requestCode, Instance instance, String keywords) {
            super(keywords, requestCode, instance);
            this.keywords = keywords;
        }

        @Override
        protected String buildQuery() {
            keywords = URLEncoder.encode(keywords);
            return String.format(BUGS_QUICK_SEARCH, keywords);
        }
    }

    public static class DashboardSearch extends Search {

        private static final long serialVersionUID = -7918981619210024917L;
        int searchType;

        public DashboardSearch(String name, int requestCode, Instance instance, int searchType) {
            super(requestCode, instance);
            this.searchType = searchType;
        }

        @Override
        protected String buildQuery() {
            String username = account.username;
            switch (searchType) {
            case DASHBOARD_TO_REVIEW:
                return String.format(DASHBOARD_QUERY_TO_REVIEW, username);
            case DASHBOARD_REPORTED:
                return String.format(DASHBOARD_QUERY_REPORTED, username, username);
            case DASHBOARD_ASSIGNED:
                return String.format(DASHBOARD_QUERY_ASSIGNED, username);
            case DASHBOARD_CCD:
                return String.format(DASHBOARD_QUERY_CCD, username, username);
            case DASHBOARD_RECENTLY_FIXED:
                return String.format(DASHBOARD_QUERY_RECENTLY_FIXED, username);
            default:
                return "";
            }
        }
    }

    public static class FormSearch extends Search {

        private static final long serialVersionUID = 9127989443996952914L;
        ArrayList<Pair<String, String>> formValues;

        public FormSearch(String name, int requestCode, Instance instance,
            ArrayList<Pair<String, String>> formValues) {
            super(name, requestCode, instance);
            this.formValues = formValues;
        }

        @Override
        protected String buildQuery() {
            StringBuilder sb = new StringBuilder();
            for (Pair<String, String> pair : formValues) {
                sb.append("&");
                sb.append(pair.key);
                sb.append("=");
                sb.append(pair.value);
            }
            return sb.toString();
        }
    }
}