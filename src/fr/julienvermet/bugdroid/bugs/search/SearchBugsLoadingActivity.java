package fr.julienvermet.bugdroid.bugs.search;

import java.util.ArrayList;

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
import fr.julienvermet.bugdroid.bugs.Bug;
import fr.julienvermet.bugdroid.bugs.BugsActivity;
import fr.julienvermet.bugdroid.products.Product;
import fr.julienvermet.bugdroid.tools.Tools;

public class SearchBugsLoadingActivity extends Activity implements Runnable{

	ArrayList<Bug> bugs;
	Product product = null;
	String params = "";
	String type = "";
	static Context ctx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		type = extras.getString("type");
		if (type.equals("search"))
			params = extras.getString("params");
		else
			product = (Product) extras.getSerializable("product");

		ctx = this;

		Thread thread = new Thread(this);
		thread.start();

		setContentView(R.layout.loading_dialog);

		WebView wvLoading = (WebView) findViewById(R.id.wvLoading);
		wvLoading.loadDataWithBaseURL("", "<center><img src='file:///android_asset/mozchomp.gif'></center>", "", "", "");

		TextView tvLoading = (TextView) findViewById(R.id.tvLoading);
		tvLoading.setText( Html.fromHtml("<b>Please wait while your bugs are retrieved.</b>"));
		tvLoading.setTextColor(Color.BLACK);
	}

	@Override
	public void run() {

		if (type.equals("search"))
			bugs = SearchBug.SearchBugsByParams(params, ctx);
		else
			bugs = SearchBug.SearchBugsByProduct(product.getName(), ctx, false);

		if (bugs != null)
		{
			Intent intent = new Intent(SearchBugsLoadingActivity.this, BugsActivity.class);
			intent.putExtra("bugs", bugs);
			intent.putExtra("product", product);
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
				Tools.showToast(ctx, "Error while retrieving the bugs. Please check your connection and your login details.");
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