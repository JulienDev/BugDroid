package fr.julienvermet.bugdroid.bugs.viewpager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.bugs.Bug;
import fr.julienvermet.bugdroid.bugs.search.SearchBugLoadingActivity;
import fr.julienvermet.bugdroid.bugs.search.SpinnerAdapter;
import fr.julienvermet.bugdroid.tools.Json;
import fr.julienvermet.bugdroid.tools.Tools;

public class BugFragment extends Fragment {

	Bug bug;

	int bug_id;
	LinearLayout llBugCcList;
	Spinner sStatus, sResolution;
	int statusId, resolutionId = 0;
	String[] bugStatus, bugResolution;
	EditText etDupeOf;

	private static final String PREFS_NAME = "login";

	public static BugFragment newInstance(Bug bug) {
		Log.i("Pager", "TestFragment.newInstance()");

		BugFragment fragment = new BugFragment();

		Bundle args = new Bundle();
		args.putSerializable("bug", bug);
		fragment.setArguments(args);

		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.bug = (Bug) getArguments().getSerializable("bug");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.i("Pager", "BugFragment.onCreateView()");

		ScrollView svBug = (ScrollView) inflater.inflate(R.layout.a_bug, null);

		TextView tvBug = (TextView) svBug.findViewById(R.id.tvBug);
		tvBug.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Tools.copyToClipboard(getActivity(), ""+ bug.getId(), "ID");
			}
		});

		TextView tvBugStatus = (TextView) svBug.findViewById(R.id.tvBugStatus);
		sStatus = (Spinner) svBug.findViewById(R.id.sStatus);
		bugStatus = getBugStatus();
		setDefaultStatus();

		TextView tvBugResolution = (TextView) svBug.findViewById(R.id.tvBugResolution);
		bugResolution = getResources().getStringArray(R.array.bugResolution);
		sResolution = (Spinner) svBug.findViewById(R.id.sResolution);
		setDefaultResolution();
		sResolution.setSelection(1);
		etDupeOf = (EditText) svBug.findViewById(R.id.etDupeOf);
		etDupeOf.setInputType(InputType.TYPE_CLASS_NUMBER);

		TextView tvBugKeywords = (TextView) svBug.findViewById(R.id.tvBugKeywords);
		TextView tvBugProduct = (TextView) svBug.findViewById(R.id.tvBugProduct);
		TextView tvBugComponent = (TextView) svBug.findViewById(R.id.tvBugComponent);
		TextView tvBugVersion = (TextView) svBug.findViewById(R.id.tvBugVersion);
		TextView tvBugPlatform = (TextView) svBug.findViewById(R.id.tvBugPlatform);
		TextView tvBugImportance = (TextView) svBug.findViewById(R.id.tvBugImportance);
		TextView tvBugTargetMilestone = (TextView) svBug.findViewById(R.id.tvBugTargetMilestone);
		TextView tvBugAssignedTo = (TextView) svBug.findViewById(R.id.tvBugAssignedTo);
		TextView tvBugQaContact = (TextView) svBug.findViewById(R.id.tvBugQaContact);

		tvBug.setText( bug.getId() + " - " + bug.getSummary() );
		tvBugStatus.setText( Html.fromHtml("<b>Status :</b>"));
		tvBugResolution.setText( Html.fromHtml("<b>Resolution :</b>"));
		tvBugProduct.setText( Html.fromHtml("<b>Product :</b> " +  bug.getProduct() ) );

		tvBugComponent.setText( Html.fromHtml("<b>Component :</b> " +  bug.getComponent() ) );
		tvBugVersion.setText( Html.fromHtml("<b>Version :</b>" + bug.getVersion() ) );
		tvBugPlatform.setText( Html.fromHtml("<b>Platform :</b> " + bug.getPlatform() ) );
		tvBugImportance.setText( Html.fromHtml("<b>Importance :</b> " + bug.getSeverity() ) );
		tvBugAssignedTo.setText( Html.fromHtml("<b>Assigned to :</b><br/> " + bug.getAssignedTo().getName()  + " <br/> " +  bug.getAssignedTo().getRealName() ) );
		tvBugQaContact.setText( Html.fromHtml("<b>QA Contact :</b> " + bug.getQaContact().getName() ) );
		tvBugTargetMilestone.setText( Html.fromHtml("<b>Target Mil. :</b> " + bug.getTargetMilestone() ) );

		if (bug.getKeywords().length > 0)
		{
			String keywords = "";
			for (int i=0; i < bug.getKeywords().length; i++)
			{
				if (i!=0)
					keywords += ",";

				keywords += bug.getKeywords()[i];
			}
			tvBugKeywords.setText( keywords );
		}
		else
			tvBugKeywords.setVisibility(View.GONE);



		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		String username = settings.getString("username", "");
		String password = settings.getString("password", "");

		if (!username.equals("") && !password.equals(""))
		{	
			sStatus.setClickable(true);
			sResolution.setClickable(true);

			Button bUpdate = (Button) svBug.findViewById(R.id.bUpdate);
			bUpdate.setVisibility(View.VISIBLE);
			bUpdate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					postUpdatedBug();

				}
			});
		}

		return svBug;
	}

	private void postUpdatedBug()
	{
		String resolution = "";
		int dupeOf = 0;
		
		if( bugStatus[statusId].equals("RESOLVED") || bugStatus[statusId].equals("VERIFIED") || bugStatus[statusId].equals("CLOSED") )
		{
			resolution = bugResolution[resolutionId];

			if (resolution.equals("DUPLICATE"))
			{
				if ( (!etDupeOf.getText().toString().equals("")))
					dupeOf = Integer.parseInt( etDupeOf.getText().toString() );
				else
					Tools.showToast(getActivity(), "Please enter the duplicate bug");
			}
		}	

		if ( Json.updateStatus(bugStatus[statusId], resolution, dupeOf, bug.getId(), getActivity()) )
		{
			Tools.showToast(getActivity(), "The bug has been updated");

			updateBug();
		}
		else
			Tools.showToast(getActivity(), "An error has occured while posting your comment. Please check your internet connection, your permissions and informations submitted");	

	}

	private String[] getBugStatus()
	{
		Resources res = getResources();

		Log.d("status", "s:" + bug.getStatus() );

		if(bug.getStatus().equals("NEW"))
			return res.getStringArray(R.array.bugStatusNew);
		else if (bug.getStatus().equals("ASSIGNED"))
			return res.getStringArray(R.array.bugStatusAssigned);
		else if (bug.getStatus().equals("RESOLVED"))
			return res.getStringArray(R.array.bugStatusResolved);
		else if (bug.getStatus().equals("UNCONFIRMED"))
			return res.getStringArray(R.array.bugStatusUnconfirmed);
		else if (bug.getStatus().equals("REOPENED"))
			return res.getStringArray(R.array.bugStatusReopened);
		else if (bug.getStatus().equals("VERIFIED"))
			return res.getStringArray(R.array.bugStatusVerified);
		else if (bug.getStatus().equals("CLOSED"))
			return res.getStringArray(R.array.bugStatusClosed);

		return null;
	}

	private void setDefaultStatus()
	{
		sStatus.setAdapter(new SpinnerUpdateAdapter(bugStatus, getActivity()));

		sStatus.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				statusId = arg2;

				if( bugStatus[statusId].equals("RESOLVED") || bugStatus[statusId].equals("VERIFIED") || bugStatus[statusId].equals("CLOSED") )
				{
					sResolution.setAdapter(new SpinnerAdapter( bugResolution , getActivity()));
					sResolution.setClickable(true);
					setDefaultResolution();
				}
				else
				{
					String[] nothing = {};
					sResolution.setAdapter(new SpinnerUpdateAdapter( nothing, getActivity() ));	
					sResolution.setClickable(false);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		for (int i=0; i < bugStatus.length; i++)
		{
			if(bugStatus[i].equals(bug.getStatus()))
				sStatus.setSelection(i);
		}
	}

	private void setDefaultResolution()
	{
		sResolution.setAdapter(new SpinnerUpdateAdapter(bugResolution, getActivity()));

		if(!bug.getResolution().equals(""))
		{

			Log.d("bug.getResolution()", "bug.getResolution()" + bug.getResolution());
			for (int i=0; i < bugResolution.length; i++)
			{
				Log.d("bugResolution[i]", "bugResolution[i]:"+ bugResolution[i]);
				if(bugResolution[i].equals(bug.getResolution()))
				{
					Log.d("true", "true:" + i);
					sResolution.setSelection(i);
				}
			}
		}

		sResolution.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				resolutionId = arg2;
				
				if (bugResolution[resolutionId].equals("DUPLICATE"))
					etDupeOf.setVisibility(View.VISIBLE);
				else
					etDupeOf.setVisibility(View.GONE);	
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	private void updateBug()
	{
		Intent intent = new Intent(getActivity(), SearchBugLoadingActivity.class);
		intent.putExtra("bug_id", bug.getId());
		intent.putExtra("update", true);
		startActivity(intent);
		getActivity().finish();
	}
}