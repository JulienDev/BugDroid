package fr.julienvermet.bugdroid.bugs.search;

import java.net.URLEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.database.Database;
import fr.julienvermet.bugdroid.tools.Tools;

public class SearchSimpleActivity extends Activity {

	EditText etWords;
	Spinner sProduct;
	SeekBar sbFrom;
	String[] listStatus = { "Open", "Closed", "All" };
	String[] listStatusUrl = { "__open__", "__close__", "__all__" };

	String[] listProducts = { "All", "Add-on SDK", "addons.mozilla.org", "AUS",
			"Bugzilla", "bugzilla.mozilla.org", "Calendar", "Camino", "CCK",
			"Composer", "Core", "Core Graveyard", "Derivatives", "Directory",
			"Documentation", "Extend Firefox", "Fennec", "Fennec Native", "Firefox", "Grendel",
			"Input", "JSS", "MailNews Core", "MailNews Core Graveyard",
			"Marketing", "Minimo", "Mozilla Communities",
			"Mozilla Developer Network", "Mozilla Grants", "Mozilla Labs",
			"Mozilla Labs Graveyard", "Mozilla Localizations",
			"Mozilla Messaging", "Mozilla Metrics", "Mozilla QA",
			"Mozilla Services", "mozilla.org", "MozillaClassic", "NSPR", "NSS",
			"Other Applications", "Pancake", "Penelope", "Plugins",
			"quality.mozilla.org", "Rhino", "SeaMonkey", "Servo", "Skywriter",
			"Snippets", "support.mozilla.com", "support.mozillamessaging.com",
			"Tamarin", "Tech Evangelism", "Testing", "Testopia", "Thunderbird",
			"Toolkit", "Toolkit Graveyard", "Web Apps", "Websites",
			"Websites Graveyard", "Webtools", "Webtools Graveyard" };

	int statusId = 0, productId = 0;

	Context ctx;
	Database db;
	static final int DIALOG_SAVE_SEARCH = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.search_simple);

		db = new Database(this);
		ctx = this;

		Spinner sStatuts = (Spinner) findViewById(R.id.sStatus);
		SpinnerAdapter saStatus = new SpinnerAdapter(listStatus, this);
		sStatuts.setAdapter(saStatus);

		sStatuts.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				statusId = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		sProduct = (Spinner) findViewById(R.id.sProduct);
		SpinnerAdapter saProducts = new SpinnerAdapter(listProducts, this);
		sProduct.setAdapter(saProducts);

		sProduct.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				productId = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		Button bSearch = (Button) findViewById(R.id.bSearch);
		bSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				searchBugs();
			}
		});

		etWords = (EditText) findViewById(R.id.etWords);

		final TextView tvFrom = (TextView) findViewById(R.id.tvFrom);

		sbFrom = (SeekBar) findViewById(R.id.sbFrom);
		sbFrom.setMax(19);
		sbFrom.setProgress(0);
		sbFrom.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				tvFrom.setText("From : " + (arg1 + 1) + " day(s)");
			}
		});
	}

	private void searchBugs() {
		if (isWordsFilled()) {

			String url = buildUrl();
			
			Log.d("url", "u:"+ url);

			Intent intent = new Intent(SearchSimpleActivity.this,
					SearchBugsLoadingActivity.class);
			intent.putExtra("type", "search");
			intent.putExtra("params", url);
			startActivity(intent);
		}
	}
	
	private String buildUrl()
	{
		String product = "";

		if (productId > 0)
			product = listProducts[productId];
		
		String url = "query_format=specific&order=relevance+desc&bug_status="
				+ listStatusUrl[statusId]
						+ "&product="
						+ URLEncoder.encode(product)
						+ "&content="
						+ Uri.encode(etWords.getText().toString().trim())
						+ "&changed_after=" + (sbFrom.getProgress() + 1) + "d";
		
		return url;
	}

	private boolean isWordsFilled() {
		if (etWords.getText().toString().trim().equals("")) {
			Tools.showToast(this, "Please enter keywords in summary");
			return false;
		}

		return true;
	}

	public void showSaveSearchDialog(View v) {
		showDialog(DIALOG_SAVE_SEARCH);
	}

	public void saveSearch(String name) {
		if (isWordsFilled()) {

			/*String status = listStatusUrl[statusId];
			
			String product = "";

			if (productId > 0)
				product = listProducts[productId];

			String words = etWords.getText().toString().trim();

			int days = sbFrom.getProgress() + 1;

			Search search = new Search(name, status, product, words, days);*/
			
			String url = buildUrl();
			Log.d("url", "u:"+ url);
			
			Search search = new Search(name, url);

			if (db.insertSearch(search))
				Tools.showToast(ctx, "Search saved");
			else
				Tools.showToast(ctx, "Error while saving the search");
		}
	}

	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_SAVE_SEARCH:
			dialog = createSaveSearchDialog();
			break;
		}
		return dialog;
	}

	private Dialog createSaveSearchDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		final EditText etName = new EditText(this);
		etName.setHint("Name");
		etName.setGravity(1);

		Button bAdd = new Button(this);
		bAdd.setText("Save");
		bAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				saveSearch(etName.getText().toString().trim());
				dismissDialog(DIALOG_SAVE_SEARCH);
			}
		});

		ll.addView(etName);
		ll.addView(bAdd);

		removeDialog(DIALOG_SAVE_SEARCH);

		builder.setTitle("Enter the name of your search");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(ll);
		builder.setCancelable(true);
		return builder.create();
	}

}