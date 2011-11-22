package fr.julienvermet.bugdroid.bugs.viewpager;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.users.User;

public class CcsListAdapter extends BaseAdapter {

	private Context ctx;
	ArrayList<User> ccs;

	public CcsListAdapter(ArrayList<User> ccs, Context ctx) {
		super();
		this.ccs = ccs;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		return ccs.size();
	}

	@Override
	public Object getItem(int arg0) {
		return ccs.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) {

		User user = ccs.get(position);

		LayoutInflater li = LayoutInflater.from(ctx);
		LinearLayout llCc = (LinearLayout)li.inflate(R.layout.a_cc, null);
		
		TextView tvCcName = (TextView) llCc.findViewById(R.id.tvCcName);
		tvCcName.setText(user.getName());

		TextView tvCcRealName = (TextView) llCc.findViewById(R.id.tvCcRealName);
		tvCcRealName.setText(user.getRealName());
				
		return llCc;
	}
}