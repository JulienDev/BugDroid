package fr.julienvermet.bugdroid.bugs.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.bugs.ABugActivity;
import fr.julienvermet.bugdroid.bugs.Bug;
import fr.julienvermet.bugdroid.tools.Tools;

public class SearchBugLoadingActivity extends Activity implements Runnable{
		
	Bug bug;
	int bug_id;
	static Context ctx;
	boolean update = false;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			bug_id = savedInstanceState.getInt("bug_id");
			update = savedInstanceState.getBoolean("update");
		} else {
			Bundle extras = getIntent().getExtras();
			bug_id = extras.getInt("bug_id");
			update = extras.getBoolean("update");
		}
						
		ctx = this;
		
		Thread thread = new Thread(this);
		thread.start();
		
		setContentView(R.layout.loading_dialog);

		WebView wvLoading = (WebView) findViewById(R.id.wvLoading);
		wvLoading.loadDataWithBaseURL("", "<center><img src='file:///android_asset/mozchomp.gif'></center>", "", "", "");

		TextView tvLoading = (TextView) findViewById(R.id.tvLoading);
		tvLoading.setText( Html.fromHtml("<b>Please wait while your bug is retrieved.</b>"));
		tvLoading.setTextColor(Color.BLACK);
	}

	@Override
	public void run() {
		bug = SearchBug.SearchBugById(bug_id, ctx, update);	
		
		if (bug != null)
		{
		Intent intent = new Intent(SearchBugLoadingActivity.this, ABugActivity.class);
		intent.putExtra("bug", bug);
		startActivity(intent);
		}
		else
			sendMessage(0);
		
		finish();
	}
	
	private void sendMessage(int type) {
		final Message message = Message.obtain();
		message.what = type;
		handler.sendMessage(message);
	}
	
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			if (message == null)
				return;
			switch (message.what) {
			case 0:
				Tools.showToast(ctx, "Error while retrieving the bug. Please check your connection and your login details.");
				break;
			}
		}
	};
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{      
	    super.onConfigurationChanged(newConfig); // add this line
	}

}