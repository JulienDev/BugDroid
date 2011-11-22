package fr.julienvermet.bugdroid.bugs.search;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpinnerAdapter extends BaseAdapter {
	
	private String[] list;
	private Context ctx;
	
	public SpinnerAdapter(String[] list, Context ctx) {
		this.list = list;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		return list.length;
	}

	@Override
	public Object getItem(int arg0) {
		return list[arg0];
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
		tv.setPadding(10, 10, 0, 10);
		tv.setTextColor(Color.BLACK);
		
		return tv;
	}

}
