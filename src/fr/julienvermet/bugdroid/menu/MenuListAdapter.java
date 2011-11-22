package fr.julienvermet.bugdroid.menu;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;

public class MenuListAdapter extends BaseAdapter {

	ArrayList<Menu> menus;
	Context ctx;
	LayoutInflater inflater;
	
	public MenuListAdapter(ArrayList<Menu> menus, Context ctx) {
		super();
		this.menus = menus;
		this.ctx = ctx;
		this.inflater = LayoutInflater.from(ctx);
	}

	@Override
	public int getCount() {
		return menus.size();
	}

	@Override
	public Menu getItem(int position) {
		return menus.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if(convertView == null) {

			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.menu_element, null);

			holder.ivMenuImage = (ImageView)convertView.findViewById(R.id.ivMenuImage);
			holder.tvMenuName = (TextView)convertView.findViewById(R.id.tvMenuName);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.ivMenuImage.setImageResource( getItem(position).getImage() );
		holder.tvMenuName.setText( getItem(position).getName() );
		
		return convertView;
	}

	private class ViewHolder {
		ImageView ivMenuImage;
		TextView tvMenuName;
	}
}