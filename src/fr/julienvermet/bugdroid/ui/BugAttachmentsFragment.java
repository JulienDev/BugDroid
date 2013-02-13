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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.model.Attachment;
import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.model.Flag;
import fr.julienvermet.bugdroid.util.DateUtils;

public class BugAttachmentsFragment extends SherlockListFragment {

    public static final String BUG = "bug";

    // UI
    private View mInformations;
    private TextView mInformationsText;
    private ProgressBar mInformationsProgress;

    // Objects
    private ArrayList<Attachment> mAttachments;

    public static BugAttachmentsFragment newInstance(Bug bug) {
        BugAttachmentsFragment fragment = new BugAttachmentsFragment();

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
        mAttachments = (ArrayList<Attachment>) bug.attachments;

        getListView().setAdapter(new CommentAdapter());
        mInformationsText.setText(R.string.attachments_no_attachment);
        getListView().setEmptyView(mInformations);
    }

    private class CommentAdapter extends BaseAdapter {

        LayoutInflater mInflater;

        public CommentAdapter() {

            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (mAttachments == null) {
                return 0;
            }
            return mAttachments.size();
        }

        @Override
        public Object getItem(int position) {
            return mAttachments.get(position);
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
                convertView = mInflater.inflate(R.layout.list_item_attachment, null);
                holder.attachmentDate = (TextView) convertView.findViewById(R.id.attachmentDate);
                holder.attachmentCreator = (TextView) convertView.findViewById(R.id.attachmentCreator);
                holder.attachmentDescription = (TextView) convertView.findViewById(R.id.attachmentDescription);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Attachment attachment = mAttachments.get(position);

            String description = attachment.description;
            ArrayList<Flag> flags = (ArrayList<Flag>) attachment.flags;
            if (flags != null && flags.size() > 0) {
                description += "<br/>";
                for (Flag flag : flags) {
                    description += "<br/><i>" + flag.setter.toString() + ": " + flag.name + flag.status + "</i>";
                }
            }
            String sDate = DateUtils.getAtomDate(attachment.creationTime);
            holder.attachmentDate.setText(sDate);
            holder.attachmentCreator.setText(attachment.attacher.name);
            holder.attachmentDescription.setText(Html.fromHtml(description));

            return convertView;
        }

        private class ViewHolder {
            TextView attachmentDate;
            TextView attachmentCreator;
            TextView attachmentDescription;
        }
    }
}