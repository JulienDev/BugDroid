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

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Handler.Callback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.application.BugDroidApplication;
import fr.julienvermet.bugdroid.model.Account;
import fr.julienvermet.bugdroid.model.Bug;
import fr.julienvermet.bugdroid.model.Comment;
import fr.julienvermet.bugdroid.model.Instance;
import fr.julienvermet.bugdroid.service.CommentIntentService;
import fr.julienvermet.bugdroid.ui.phone.BugActivity;
import fr.julienvermet.bugdroid.util.DateUtils;

public class BugCommentsFragment extends SherlockListFragment implements OnClickListener {

    public static final String BUG = "bug";

    // UI
    private EditText mCommentMessage;
    private ImageButton mCommentSend;
    private ProgressBar mCommentSendProgress;
    private ViewGroup mCommentAdd;

    // Objects
    private ArrayList<Comment> mComments;
    private Instance mInstance;
    private Bug mBug;

    public static BugCommentsFragment newInstance(Bug bug) {
        BugCommentsFragment fragment = new BugCommentsFragment();

        Bundle args = new Bundle();
        args.putSerializable(BUG, bug);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bug_comments, null);
        mCommentAdd = (ViewGroup) view.findViewById(R.id.commentAdd);
        mCommentMessage = (EditText) view.findViewById(R.id.commentMessage);
        mCommentSend = (ImageButton) view.findViewById(R.id.commentSend);
        mCommentSendProgress = (ProgressBar) view.findViewById(R.id.commentSendProgress);
        mCommentSend.setOnClickListener(this);
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        mCommentMessage.clearFocus();
        getListView().requestFocus();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mInstance = BugDroidApplication.mCurrentInstance;

        getListView().setAdapter(new CommentAdapter());

        Instance instance = BugDroidApplication.mCurrentInstance;
        Account account = instance.account;

        if (account == null) {
            mCommentAdd.setVisibility(View.GONE);
        }

        mBug = (Bug) getArguments().getSerializable(BUG);
        mComments = (ArrayList<Comment>) mBug.comments;
    }

    private void postComment() {
        try {
            String message = mCommentMessage.getText().toString();
            if (message.isEmpty()) {
                Toast.makeText(getActivity(), R.string.comment_empty, Toast.LENGTH_LONG).show();
                return;
            }
            
            mCommentSendProgress.setVisibility(View.VISIBLE);
            mCommentSend.setVisibility(View.GONE);

            JSONObject jsonData = new JSONObject();
            jsonData.put("text", message);

            Intent intent = CommentIntentService.getIntent(getActivity(), mInstance, mBug.bugId,
                jsonData);
            Messenger messenger = new Messenger(onCommentHandler);
            intent.putExtra(CommentIntentService.MESSENGER, messenger);
            getActivity().startService(intent);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    Handler onCommentHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (getSherlockActivity() != null) {
                int statusCode = msg.getData().getInt(CommentIntentService.STATUS_CODE);
                String toastMessage = "";
                switch (statusCode) {
                case 201:
                    toastMessage = getString(R.string.comment_post_success);
                    mCommentMessage.setText("");
                    BugFragment bugFragment = ((BugActivity) getActivity()).mBugFragment;
                    bugFragment.refresh();
                    break;
                default:
                    String result = msg.getData().getString(CommentIntentService.RESULT);
                    toastMessage = getString(R.string.comment_post_error) + result;
                    break;
                }
                mCommentSendProgress.setVisibility(View.GONE);
                mCommentSend.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_LONG).show();
            }
            return false;
        }
    });

    private class CommentAdapter extends BaseAdapter {

        LayoutInflater mInflater;

        public CommentAdapter() {

            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if (mComments == null) {
                return 0;
            }
            return mComments.size();
        }

        @Override
        public Object getItem(int position) {
            return mComments.get(position);
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
                convertView = mInflater.inflate(R.layout.list_item_comment, null);
                holder.commentDate = (TextView) convertView.findViewById(R.id.commentDate);
                holder.commentCreator = (TextView) convertView.findViewById(R.id.commentCreator);
                holder.commentText = (TextView) convertView.findViewById(R.id.commentText);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Comment comment = mComments.get(position);

            String sDate = DateUtils.getAtomDate(comment.creationTime);
            holder.commentDate.setText("#" + position + "\n" + sDate);
            holder.commentCreator.setText(comment.creator.toString());
            holder.commentText.setText(comment.text);

            return convertView;
        }

        private class ViewHolder {
            TextView commentDate;
            TextView commentCreator;
            TextView commentText;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mCommentSend) {
            postComment();
        }
    }
}