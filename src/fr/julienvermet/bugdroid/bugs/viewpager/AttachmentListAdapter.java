package fr.julienvermet.bugdroid.bugs.viewpager;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.database.Attachment;
import fr.julienvermet.bugdroid.database.Flag;

public class AttachmentListAdapter extends BaseAdapter {

	private Context ctx;
	ArrayList<Attachment> attachments;

	public AttachmentListAdapter(ArrayList<Attachment> attachments, Context ctx) {
		super();
		this.attachments = attachments;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		return attachments.size();
	}

	@Override
	public Object getItem(int arg0) {
		return attachments.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		Attachment attachment = attachments.get(arg0);

		LayoutInflater li = LayoutInflater.from(ctx);
		LinearLayout llAttachment = (LinearLayout)li.inflate(R.layout.an_attachment, null);
		
		TextView tvAttachmentText = (TextView) llAttachment.findViewById(R.id.tvAttachmentText);
		tvAttachmentText.setText(attachment.getDescription());

		TextView tvAttachmentInfos = (TextView) llAttachment.findViewById(R.id.tvAttachmentInfos);
		tvAttachmentInfos.setText("(" + attachment.getSize() + ") " + attachment.getCreationTime() );

		LinearLayout llAttachmentFlags = (LinearLayout) llAttachment.findViewById(R.id.llAttachmentFlags);
		
		if (attachment.getFlags() != null)
		{
			for (int j=0; j<attachment.getFlags().size(); j++)
			{
				Flag flag = attachment.getFlags().get(j);

				TextView tvAttachmentFlags = new TextView(ctx);
				tvAttachmentFlags.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
				tvAttachmentFlags.setText(flag.getSetter().getName() + ": " + flag.getName() + flag.getStatus() + "");
				tvAttachmentFlags.setTextColor(Color.BLACK);
				
				llAttachmentFlags.addView(tvAttachmentFlags);
			}
		}
		
		String attacher =  attachment.getAttacher().getName();
		if (attachment.getAttacher().getRealName() != null)
			attacher += " (" + attachment.getAttacher().getRealName() + ")";
		
		TextView tvAttachmentAttacher = (TextView) llAttachment.findViewById(R.id.tvAttachmentAttacher);
		tvAttachmentAttacher.setText(attacher);
				
		return llAttachment;
	}
}