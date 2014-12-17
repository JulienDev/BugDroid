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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.model.Bug;

public class BugDetailsFragment extends SherlockFragment {

    public static final String BUG = "bug";

    private Bug mBug;

    private TextView mSummary;
    // private Spinner mStatus;
    // private Spinner mResolution;
    private TextView mStatus;
    private TextView mResolution;
    private TextView mProduct;
    private TextView mComponent;
    private TextView mVersion;
    private TextView mPlatform;
    private TextView mImportance;
    private TextView mAssignedTo;
    private TextView mQAContact;
    private TextView mTargetMil;

    public static BugDetailsFragment newInstance(Bug bug) {
        BugDetailsFragment fragment = new BugDetailsFragment();

        Bundle args = new Bundle();
        args.putSerializable(BUG, bug);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bug_details, null);

        mSummary = (TextView) view.findViewById(R.id.bugSummary);
        // For edit mode
        // mStatus = (Spinner) view.findViewById(R.id.bugStatus);
        // mResolution = (Spinner) view.findViewById(R.id.bugResolution);
        mStatus = (TextView) view.findViewById(R.id.bugStatus);
        mResolution = (TextView) view.findViewById(R.id.bugResolution);
        mProduct = (TextView) view.findViewById(R.id.bugProduct);
        mComponent = (TextView) view.findViewById(R.id.bugComponent);
        mVersion = (TextView) view.findViewById(R.id.bugVersion);
        mPlatform = (TextView) view.findViewById(R.id.bugPlatform);
        mImportance = (TextView) view.findViewById(R.id.bugImportance);
        mAssignedTo = (TextView) view.findViewById(R.id.bugAssignedTo);
        mQAContact = (TextView) view.findViewById(R.id.bugQAContact);
        mTargetMil = (TextView) view.findViewById(R.id.bugTargetMil);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBug = (Bug) getArguments().getSerializable(BUG);

        mSummary.setText(mBug.summary);
        // mStatus = (Spinner) view.findViewById(R.id.bugSummary);
        // mResolution = (Spinner) view.findViewById(R.id.bugSummary);
        mStatus.setText(mBug.status);
        mResolution.setText(mBug.resolution);
        mProduct.setText(mBug.product);
        mComponent.setText(mBug.component);
        mVersion.setText(mBug.version);
        mPlatform.setText(mBug.platform);
        mImportance.setText(mBug.severity);
        mAssignedTo.setText(mBug.assignedTo.toString());
        mQAContact.setText(mBug.qaContact.toString());
        mTargetMil.setText(mBug.targetMilestone);
    }
}