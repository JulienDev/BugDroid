package fr.julienvermet.bugdroid.tools;

import fr.julienvermet.bugdroid.bugs.search.SearchBugLoadingActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class Intentfilter extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Uri uri = getIntent().getData();
		int bug_id = Integer.parseInt( uri.getQueryParameter("id") );
		
		Intent intent = new Intent(Intentfilter.this, SearchBugLoadingActivity.class);
		intent.putExtra("bug_id", bug_id );
		startActivity(intent);
		
		finish();
	}
}