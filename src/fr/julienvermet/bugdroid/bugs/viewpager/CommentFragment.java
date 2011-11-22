package fr.julienvermet.bugdroid.bugs.viewpager;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.bugs.Bug;
import fr.julienvermet.bugdroid.bugs.search.SearchBugLoadingActivity;
import fr.julienvermet.bugdroid.database.Comment;
import fr.julienvermet.bugdroid.database.Database;
import fr.julienvermet.bugdroid.tools.Json;
import fr.julienvermet.bugdroid.tools.Tools;

public class CommentFragment extends Fragment {

	Bug bug;
	Button bCommentPost;
	ListView lvComments;
	CommentListAdapter cla;
	Database db;

	private static final String PREFS_NAME = "login";

	public static CommentFragment newInstance(Bug bug) {
		Log.i("Pager", "CommentFragment.newInstance()");

		CommentFragment fragment = new CommentFragment();

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

		Log.i("Pager", "CommentFragment.onCreateView()");
		
		db = new Database(getActivity());

		if (bug.getComments() != null)
		{
			RelativeLayout rlComments = (RelativeLayout) inflater.inflate(R.layout.comments, null);

			lvComments = (ListView) rlComments.findViewById(R.id.lvComments);
			
			cla = new CommentListAdapter(bug.getComments(), getActivity());

			lvComments.setAdapter(cla);

			final ImageView ivCommentArrowLeft = (ImageView) rlComments.findViewById(R.id.ivCommentArrowLeft);
			final ImageView ivCommentArrowRight = (ImageView) rlComments.findViewById(R.id.ivCommentArrowRight);

			SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
			String username = settings.getString("username", "");
			String password = settings.getString("password", "");

			final SlidingDrawer sdComment = (SlidingDrawer) rlComments.findViewById(R.id.sdComment);

			if (!username.equals("") && !password.equals(""))
			{
				lvComments.setPadding(0, 0, 0, 80);
				
				final EditText etCommentText = (EditText) rlComments.findViewById(R.id.etCommentText);

				sdComment.setOnDrawerOpenListener(new OnDrawerOpenListener() {

					@Override
					public void onDrawerOpened() {
						ivCommentArrowLeft.setImageResource(R.drawable.expander_close_holo_light);
						ivCommentArrowRight.setImageResource(R.drawable.expander_close_holo_light);
						etCommentText.requestFocus();
					}
				});
				sdComment.setOnDrawerCloseListener(new OnDrawerCloseListener() {

					@Override
					public void onDrawerClosed() {
						ivCommentArrowLeft.setImageResource(R.drawable.expander_open_holo_light);
						ivCommentArrowRight.setImageResource(R.drawable.expander_open_holo_light);
					}
				});

				
				bCommentPost = (Button) rlComments.findViewById(R.id.bCommentPost);
				bCommentPost.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						String text = etCommentText.getText().toString();
						if (!text.trim().equals(""))
						{
							if ( Json.postComment(text, bug.getId(), getActivity() ) )
							{
								Tools.showToast(getActivity(), "Your comment has been posted");
								sdComment.close();

								ArrayList<Comment> comments = Json.getComments(bug.getId(), getActivity());
								
								cla = new CommentListAdapter(comments, getActivity());

								lvComments.setAdapter(cla);
								db.updateComments(bug.getId(), comments);
								
								scrollMyListViewToBottom();
							}
							else
								Tools.showToast(getActivity(), "An error has occured while posting your comment. Please check your internet connection and your login details");	
						}
						else
							Tools.showToast(getActivity(), "Please enter a message");	

					}
				});
			}
			else
			{
				sdComment.setVisibility(View.INVISIBLE);
			}

			return rlComments;
		}
		else
		{
			TextView tv = new TextView(getActivity());
			tv.setText("No commment");
			return tv;
		}
	}

	private void updateBug()
	{
		Intent intent = new Intent(getActivity(), SearchBugLoadingActivity.class);
		intent.putExtra("bug_id", bug.getId());
		intent.putExtra("update", true);
		startActivity(intent);
		getActivity().finish();
	}
	
	private void scrollMyListViewToBottom() {
		lvComments.post(new Runnable() {
	        @Override
	        public void run() {
	            // Select the last row so it will scroll into view...
	        	lvComments.setSelection(cla.getCount() - 1);
	        }
	    });
	}
}