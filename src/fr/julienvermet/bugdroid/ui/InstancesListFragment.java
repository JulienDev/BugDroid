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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.internal.widget.IcsListPopupWindow;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.model.Account;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Accounts;
import fr.julienvermet.bugdroid.provider.BugDroidContent.Instances;
import fr.julienvermet.bugdroid.util.Patterns;

public class InstancesListFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String INSTANCE_ID = "instance_id";
    public static final String ACCOUNT_ID = "account_id";

    // Android
    private static InstanceAdapter mInstanceAdapter;
    private static FragmentManager mFragmentManager;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mPrefsEditor;

    // UI
    private IcsListPopupWindow mListPopupWindow;
    private View mInformations;
    private TextView mInformationsText;

    // Objects

    public static InstancesListFragment newInstance() {
        return new InstancesListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_instances, null);
        mInformations = view.findViewById(R.id.informations);
        mInformationsText = (TextView) view.findViewById(R.id.informationsText);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mPrefsEditor = mPrefs.edit();

        mFragmentManager = getActivity().getSupportFragmentManager();
        mInformationsText.setText(R.string.instances_loading);
        getLoaderManager().initLoader(0, null, this);
        getListView().setEmptyView(mInformations);
        mInstanceAdapter = new InstanceAdapter(getActivity(), null, 0);
        setListAdapter(mInstanceAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.accounts_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            getActivity().finish();
            break;
        case R.id.menu_instance_add:
            new InstanceDialogFragment().show(mFragmentManager, InstanceDialogFragment.class.getSimpleName());
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class AccountDialogFragment extends DialogFragment implements TextWatcher,
        OnCheckedChangeListener {

        private static final String ACCOUNT_ID = "accountId";

        private EditText mEmail, mPassword;
        private CheckBox mShowPassword;
        private Button mPositiveButton;
        private int mInstanceIdValue;
        private int mAccountIdValue;

        public static AccountDialogFragment newInstance(int instanceId, Account account) {
            AccountDialogFragment addAccountDialogFragment = new AccountDialogFragment();

            Bundle args = new Bundle();
            args.putInt(INSTANCE_ID, instanceId);
            if (account != null) {
                args.putInt(ACCOUNT_ID, account._id);
            }
            addAccountDialogFragment.setArguments(args);

            return addAccountDialogFragment;
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            if (getArguments() != null) {
                mInstanceIdValue = getArguments().getInt(INSTANCE_ID);
                mAccountIdValue = getArguments().getInt(ACCOUNT_ID, -1);
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            mEmail = (EditText) getDialog().findViewById(R.id.accountEmail);
            mEmail.addTextChangedListener(this);
            mPassword = (EditText) getDialog().findViewById(R.id.accountPassword);
            mPassword.addTextChangedListener(this);
            mShowPassword = (CheckBox) getDialog().findViewById(R.id.accountShowPassword);
            mShowPassword.setOnCheckedChangeListener(this);
            mPositiveButton = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
            mPositiveButton.setEnabled(false);
            //TODO : Implement edit account
            if (mAccountIdValue > 0) {
                mPositiveButton.setText("Edit");
            }
        }

        private void isFormValid() {
            if (isEmailValid(mEmail.getText().toString()) && mPassword.getText().toString().length() > 0) {
                mPositiveButton.setEnabled(true);
                return;
            }
            mPositiveButton.setEnabled(false);
        }

        private boolean isEmailValid(CharSequence email) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.fragment_account_add, null);

            return new AlertDialog.Builder(getActivity()).setTitle("Add account").setView(view)
                .setCancelable(false).setNegativeButton("Cancel", null)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Accounts.Columns.INSTANCES_ID.getName(), mInstanceIdValue);
                        contentValues.put(Accounts.Columns.EMAIL.getName(), mEmail.getText().toString());
                        contentValues.put(Accounts.Columns.PASSWORD.getName(), mPassword.getText().toString());
                        getActivity().getContentResolver().insert(Accounts.CONTENT_URI, contentValues);
                    }
                }).create();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            isFormValid();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.equals(mShowPassword)) {
                if (isChecked) {
                    mPassword.setTransformationMethod(null);
                } else {
                    mPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        }
    }

    public static class InstanceDialogFragment extends DialogFragment implements TextWatcher {

        private static final String INSTANCE_ID = "instanceId";
        private static final String INSTANCE_NAME = "instanceName";
        private static final String INSTANCE_URL = "instanceUrl";
        private EditText mName, mUrl;
        private Button mPositiveButton;
        private String mNameValue, mUrlValue;
        private int mIdValue;

        public static InstanceDialogFragment newInstance(Instance instance) {
            InstanceDialogFragment addInstanceDialogFragment = new InstanceDialogFragment();

            Bundle args = new Bundle();
            args.putInt(INSTANCE_ID, instance._id);
            args.putString(INSTANCE_NAME, instance.name);
            args.putString(INSTANCE_URL, instance.url);
            addInstanceDialogFragment.setArguments(args);
            return addInstanceDialogFragment;
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            setCancelable(false);
            
            if (getArguments() != null) {
                mIdValue = getArguments().getInt(INSTANCE_ID);
                mNameValue = getArguments().getString(INSTANCE_NAME);
                mUrlValue = getArguments().getString(INSTANCE_URL);
                getDialog().setTitle("Edit instance");
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            mName = (EditText) getDialog().findViewById(R.id.instanceName);
            mName.setText(mNameValue);
            mName.addTextChangedListener(this);
            mUrl = (EditText) getDialog().findViewById(R.id.instanceUrl);
            mUrl.setText(mUrlValue);
            mUrl.addTextChangedListener(this);
            mPositiveButton = ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
            mPositiveButton.setEnabled(false);
            if (mIdValue > 0) {
                mPositiveButton.setText("Edit");
            }
            isFormValid();
        }

        private void isFormValid() {
            if (isUrlValid(mUrl.getText().toString()) && mName.getText().toString().length() > 0) {
                mPositiveButton.setEnabled(true);
                return;
            }
            mPositiveButton.setEnabled(false);
        }

        private boolean isUrlValid(CharSequence email) {
            return Patterns.WEB_URL.matcher(email).matches();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.fragment_instance_add, null);

            AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity())
                .setTitle("Add an instance")
                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Instances.Columns.NAME.getName(), mName.getText().toString());
                        contentValues.put(Instances.Columns.URL.getName(), mUrl.getText().toString());
                        if (mIdValue != 0) {
                            Uri uri = Uri.withAppendedPath(Instances.CONTENT_URI, Integer.toString(mIdValue));
                            getActivity().getContentResolver().update(uri, contentValues, null, null);
                        } else {
                            getActivity().getContentResolver().insert(Instances.CONTENT_URI, contentValues);
                        }
                    }
                });
            if (mInstanceAdapter.getCount() > 0) {
                builder.setNegativeButton("Cancel", null);
            }
            return builder.create();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            isFormValid();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
          return new CursorLoader(getActivity(), Instances.CONTENT_URI, Instances.PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
        if (data != null) {
            mInstanceAdapter.swapCursor(data);
            if (mInstanceAdapter.getCount() == 0) {
                showAddInstanceDialog.sendEmptyMessage(0);
            }
        }
    }

    public static Handler showAddInstanceDialog = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            new InstanceDialogFragment().show(mFragmentManager, InstanceDialogFragment.class.getSimpleName());
        }
    };

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mInstanceAdapter.swapCursor(null);
    }

    private class InstanceAdapter extends CursorAdapter implements OnItemClickListener {

        public InstanceAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);

            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private LayoutInflater mInflater;

        @Override
        public int getCount() {
            if (getCursor() == null) {
                return 0;
            }
            return getCursor().getCount();
        }

        @Override
        public Object getItem(int position) {
            return getCursor().moveToPosition(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            int position;
            TextView instanceName;
            TextView instanceUrl;
            LinearLayout accountsList;
            View withoutAccount;
            RadioButton withoutAccountRadioButton;
        }

        @Override
        public void bindView(View view, Context arg1, Cursor cursor) {
            final Instance instance = Instance.toInstance(cursor);

            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.instanceName.setText(instance.name);
            holder.instanceUrl.setText(instance.url);
            holder.withoutAccount.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedAccount(instance._id, -1);
                }
            });
            holder.withoutAccountRadioButton.setChecked(isInstanceSelected(instance));
            holder.withoutAccountRadioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        setSelectedAccount(instance._id, -1);
                    }
                }
            });

            int accountsCount = holder.accountsList.getChildCount();
            for (int i = 1; i < accountsCount; i++) {
                holder.accountsList.removeViewAt(1);
            }
            
            String selection = Accounts.Columns.INSTANCES_ID + "=" + instance._id;
            Cursor cursorAccounts = mContext.getContentResolver().query(Accounts.CONTENT_URI, Accounts.PROJECTION,
                selection, null, null);
            if (cursorAccounts.getCount() > 0) {
                cursorAccounts.moveToFirst();
                for (int i = 0; i < cursorAccounts.getCount(); i++) {
                    cursorAccounts.moveToPosition(i);
                    final Account account = Account.toAccount(cursorAccounts);
                    View viewAccount = mInflater.inflate(R.layout.list_item_account, null);
                    viewAccount.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSelectedAccount(instance._id, account._id);
                        }
                    });
                    viewAccount.setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            deleteAccount(instance, account);
                            return true;
                        }
                    });
                    TextView accountEmail = (TextView) viewAccount.findViewById(R.id.accountEmail);
                    RadioButton accountRadioButton = (RadioButton) viewAccount
                        .findViewById(R.id.accountRadioButton);
                    accountRadioButton.setChecked(isAccountSelected(instance, account));
                    accountRadioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                setSelectedAccount(instance._id, account._id);
                            }
                        }
                    });
                    accountEmail.setText(account.username);
                    holder.accountsList.addView(viewAccount);
                }
            }
            cursorAccounts.close();
        }

        @Override
        public View newView(Context arg0, final Cursor cursor, ViewGroup arg2) {

            final ViewHolder holder = new ViewHolder();
            View view = mInflater.inflate(R.layout.list_item_instance, null);
            holder.instanceName = (TextView) view.findViewById(R.id.instanceName);
            holder.instanceUrl = (TextView) view.findViewById(R.id.instanceUrl);
            holder.accountsList = (LinearLayout) view.findViewById(R.id.accountsList);
            holder.withoutAccount = view.findViewById(R.id.withoutAccount);
            holder.withoutAccountRadioButton = (RadioButton) holder.withoutAccount
                .findViewById(R.id.accountRadioButton);
            holder.position = cursor.getPosition();
            final View instanceMenu = view.findViewById(R.id.instanceMenu);
            instanceMenu.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInstancePosition = holder.position;
                    String[] listItems = { "Add account", "Edit", "Delete" };
                    mListPopupWindow = new IcsListPopupWindow(getActivity());
                    int popupWindowWidth = (int) getResources().getDimension(R.dimen.instances_menu_width);
                    mListPopupWindow.setContentWidth(popupWindowWidth);
                    mListPopupWindow.setAdapter(new ArrayAdapter(getActivity(),
                        android.R.layout.simple_list_item_1, listItems));
                    mListPopupWindow.setAnchorView(instanceMenu);
                    mListPopupWindow.setOnItemClickListener(InstanceAdapter.this);
                    mListPopupWindow.show();
                }
            });

            view.setTag(holder);
            return view;
        }

        private int mInstancePosition;

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

            getCursor().moveToPosition(mInstancePosition);
            Instance instance = Instance.toInstance(getCursor());

            switch (position) {
            case 0:
                AccountDialogFragment.newInstance(instance._id, instance.account).show(mFragmentManager,
                    AccountDialogFragment.class.getSimpleName());
                break;
            case 1:
                InstanceDialogFragment.newInstance(instance).show(mFragmentManager,
                    InstanceDialogFragment.class.getSimpleName());
                break;
            case 2:
                Uri uri = Uri.withAppendedPath(Instances.CONTENT_URI, Integer.toString(instance._id));
                mContext.getContentResolver().delete(uri, null, null);
                break;
            }
            mListPopupWindow.dismiss();
        }

        private boolean isInstanceSelected(Instance instance) {
            int instanceId = mPrefs.getInt(INSTANCE_ID, -1);
            int accountId = mPrefs.getInt(ACCOUNT_ID, -1);

            if (instanceId == instance._id && accountId == -1) {
                return true;
            }
            return false;
        }

        private boolean isAccountSelected(Instance instance, Account account) {
            int instanceId = mPrefs.getInt(INSTANCE_ID, -1);
            int accountId = mPrefs.getInt(ACCOUNT_ID, -1);

            if (instanceId == instance._id && accountId == account._id) {
                return true;
            }
            return false;
        }

        private void setSelectedAccount(int instanceId, int accountId) {

            mPrefsEditor.putInt(INSTANCE_ID, instanceId);
            mPrefsEditor.commit();

            if (accountId >= 0) {
                mPrefsEditor.putInt(ACCOUNT_ID, accountId);
            } else {
                mPrefsEditor.remove(ACCOUNT_ID);
            }
            mPrefsEditor.commit();

            mInstanceAdapter.notifyDataSetChanged();
        }

        private void deleteAccount(final Instance instance, final Account account) {
            new AlertDialog.Builder(getActivity()).setTitle("Delete account : " + account.username)
                .setCancelable(true).setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.withAppendedPath(Accounts.CONTENT_URI, String.valueOf(account._id));
                        mContext.getContentResolver().delete(uri, null, null);

                        setSelectedAccount(instance._id, -1);
                    }
                }).create().show();
        }
    }
}