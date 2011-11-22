package fr.julienvermet.bugdroid.bugs.viewpager;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.users.User;

public class CcsFragment extends Fragment {

	ArrayList<User> ccs;

	public static CcsFragment newInstance(ArrayList<User> ccs) {
		Log.i("Pager", "TestFragment.newInstance()");

		CcsFragment fragment = new CcsFragment();

		Bundle args = new Bundle();
		args.putSerializable("ccs", ccs);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.ccs = (ArrayList<User>) getArguments().getSerializable("ccs");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.i("Pager", "CcsFragment.onCreateView()");
		
		if (ccs != null)
		{
			LinearLayout llCcs = (LinearLayout) inflater.inflate(R.layout.ccs, null);
			
			ListView lvCcs = (ListView) llCcs.findViewById(R.id.lvCcs);
			
			lvCcs.setAdapter(new CcsListAdapter(ccs, getActivity()));

			return llCcs;
		}
		else
		{
			TextView tv = new TextView(getActivity());
			tv.setText("No cc");
			return tv;
		}
	}
}