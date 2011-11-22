package fr.julienvermet.bugdroid.bugs;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;

public class BugsListAdapter extends BaseAdapter {

	ArrayList<Bug> bugs = new ArrayList<Bug>();
	Context ctx;
	LayoutInflater inflater;
	int image;
	
	public BugsListAdapter(ArrayList<Bug> products, Context ctx, int image) {
		super();
		this.bugs = products;
		this.ctx = ctx;
		this.inflater = LayoutInflater.from(ctx);
		this.image = image;
	}

	@Override
	public int getCount() {
		return bugs.size();
	}

	@Override
	public Bug getItem(int position) {
		return bugs.get(position);
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
			convertView = inflater.inflate(R.layout.bug_element, null);

			holder.ivProduct = (ImageView)convertView.findViewById(R.id.ivProduct);
			holder.tvBugSummary = (TextView)convertView.findViewById(R.id.tvBugSummary);
			holder.tvBugId = (TextView)convertView.findViewById(R.id.tvBugId);
			holder.tvBugSeverity = (TextView)convertView.findViewById(R.id.tvBugSeverity);
			holder.tvBugPriority = (TextView)convertView.findViewById(R.id.tvBugPriority);
			holder.tvBugOS = (TextView)convertView.findViewById(R.id.tvBugOS);
			holder.tvBugAssignee = (TextView)convertView.findViewById(R.id.tvBugAssignee);
			holder.tvBugStatus = (TextView)convertView.findViewById(R.id.tvBugStatus);
			holder.tvBugResolution = (TextView)convertView.findViewById(R.id.tvBugResolution);
			
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.ivProduct.setImageResource(image);
		holder.tvBugSummary.setText( getItem(position).getSummary() );
		holder.tvBugId.setText( "ID : " + getItem(position).getId() );
		holder.tvBugSeverity.setText( " | Severity : " + getItem(position).getSeverity() );
		holder.tvBugPriority.setText( "Priority : " + getItem(position).getPriority() );
		holder.tvBugOS.setText( " | OS : " + getItem(position).getOpSys() );
		holder.tvBugAssignee.setText( "Assignee : " + getItem(position).getAssignedTo().getName() );
		holder.tvBugStatus.setText( "Status : " +getItem(position).getStatus() );
		holder.tvBugResolution.setText( "Resolution : " +getItem(position).getResolution() );

		return convertView;
	}

	private class ViewHolder {
		ImageView ivProduct;
		TextView tvBugSummary;
		TextView tvBugId;
		TextView tvBugSeverity;
		TextView tvBugPriority;
		TextView tvBugOS;
		TextView tvBugAssignee;
		TextView tvBugStatus;
		TextView tvBugResolution;
	}
}