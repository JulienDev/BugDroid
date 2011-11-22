package fr.julienvermet.bugdroid.bugs.viewpager;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpinnerUpdateAdapter extends BaseAdapter {

	private String[] list;
	private Context ctx;

	public SpinnerUpdateAdapter(String[] list, Context ctx) {
		this.list = list;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
//		if (list != null)
			return list.length;
//		else return 0;
	}

	@Override
	public Object getItem(int arg0) {
//		if (list != null)
			return list[arg0];
//		else return 0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		TextView tv = new TextView(ctx);
		tv.setText( (String) getItem(arg0) );
		tv.setTextSize(20);
		tv.setPadding(5, 0, 0, 5);
		tv.setTextColor(Color.BLACK);

		return tv;

	}
}