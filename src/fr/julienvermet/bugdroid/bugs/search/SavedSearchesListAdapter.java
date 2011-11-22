package fr.julienvermet.bugdroid.bugs.search;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SavedSearchesListAdapter extends BaseAdapter {

	ArrayList<Search> searches = new ArrayList<Search>();
	Context ctx;
	LayoutInflater inflater;
	
	public SavedSearchesListAdapter(ArrayList<Search> searches, Context ctx) {
		super();
		this.searches = searches;
		this.ctx = ctx;
		this.inflater = LayoutInflater.from(ctx);
	}

	@Override
	public int getCount() {
		return searches.size();
	}

	@Override
	public Search getItem(int position) {
		return searches.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView tvSearchName = new TextView(ctx);
		tvSearchName.setText( getItem(position).getName());
		tvSearchName.setTextColor(Color.BLACK);
		tvSearchName.setTextSize(18);
		tvSearchName.setPadding(5, 10, 0, 10);
		
		return tvSearchName;
	}
}