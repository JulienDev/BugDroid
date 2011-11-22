package fr.julienvermet.bugdroid.bugs.viewpager;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.database.Comment;

public class CommentListAdapter extends BaseAdapter implements SectionIndexer{

	private Context ctx;
	ArrayList<Comment> comments;
	Integer[] sections;

	public CommentListAdapter(ArrayList<Comment> comments, Context ctx) {
		super();
		this.comments = comments;
		this.ctx = ctx;

		sections = new Integer[comments.size()];

		for (int i=0; i < comments.size(); i++)
		{
			sections[i] = i;
		}
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Object getItem(int arg0) {
		return comments.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		final Comment comment = comments.get(arg0);

		LayoutInflater li = LayoutInflater.from(ctx);
		LinearLayout llComment = (LinearLayout)li.inflate(R.layout.a_comment, null);

		ImageView ivCommentShare = (ImageView) llComment.findViewById(R.id.ivCommentShare);
		ivCommentShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				share(comment);
			}
		});

		TextView tvCommentNumber = (TextView) llComment.findViewById(R.id.tvCommentNumber);
		tvCommentNumber.setText("" + arg0);

		TextView tvCommentCreator = (TextView) llComment.findViewById(R.id.tvCommentCreator);
		tvCommentCreator.setText(comment.getCreator().getName());

		TextView tvCommentDate = (TextView) llComment.findViewById(R.id.tvCommentDate);
		tvCommentDate.setText( comment.getCreationTime() );

		TextView tvCommentText = (TextView) llComment.findViewById(R.id.tvCommentText);
		tvCommentText.setText( comment.getText() );

		llComment.setId(comment.getId());

		return llComment;

	}

	private void share(Comment comment)
	{
		Intent sendMailIntent = new Intent(Intent.ACTION_SEND);
		sendMailIntent.putExtra(Intent.EXTRA_SUBJECT, "Comment of " + comment.getCreator().getName() );
		sendMailIntent.putExtra(Intent.EXTRA_TEXT, "Comment of " + comment.getCreator().getName() + " -" + comment.getCreationTime() +"\n \n" + comment.getText());
		sendMailIntent.setType("text/plain");
		ctx.startActivity(Intent.createChooser(sendMailIntent, "Share comment"));
	}

	@Override
	public int getPositionForSection(int position) {
		return sections[position];
	}

	@Override
	public int getSectionForPosition(int position) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return sections;
	}
}