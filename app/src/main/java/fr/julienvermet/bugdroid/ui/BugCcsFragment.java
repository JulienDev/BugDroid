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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.model.Cc;

public class BugCcsFragment extends SherlockListFragment {

    public static final String BUG = "bug";

    // UI
    private View mInformations;
    private TextView mInformationsText;
    private ProgressBar mInformationsProgress;

    // Objects
    private ArrayList<Cc> mCcs;

    public static BugCcsFragment newInstance(Bug bug) {
        BugCcsFragment fragment = new BugCcsFragment();

        Bundle args = new Bundle();
        args.putSerializable(BUG, bug);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, null);
        mInformations = view.findViewById(R.id.informations);
        mInformationsText = (TextView) view.findViewById(R.id.informationsText);
        mInformationsProgress = (ProgressBar) mInformations.findViewById(R.id.informationsProgress);
        mInformationsProgress.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bug bug = (Bug) getArguments().getSerializable(BUG);
        mCcs = (ArrayList<Cc>) bug.cc;

        getListView().setAdapter(new CcAdapter());
        mInformationsText.setText(R.string.ccs_no_cc);
        getListView().setEmptyView(mInformations);
    }

    private class CcAdapter extends BaseAdapter {

        LayoutInflater mInflater;

        public CcAdapter() {

            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (mCcs == null) {
                return 0;
            }
            return mCcs.size();
        }

        @Override
        public Object getItem(int position) {
            return mCcs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_cc, null);
                holder.ccName = (TextView) convertView;

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Cc cc = mCcs.get(position);

            holder.ccName.setText(cc.toString());

            return convertView;
        }

        private class ViewHolder {
            TextView ccName;
        }
    }
}