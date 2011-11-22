package fr.julienvermet.bugdroid.bugs.search;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import fr.julienvermet.bugdroid.R;

public class SearchBugActivity extends TabActivity {

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	
	setContentView(R.layout.search);
	
	TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
	
	TabSpec firstTabSpec = tabHost.newTabSpec("tid1");
	TabSpec secondTabSpec = tabHost.newTabSpec("tid1");
	
	firstTabSpec.setIndicator("Simple search").setContent(new Intent(this,SearchSimpleActivity.class));
	secondTabSpec.setIndicator("Saved searches").setContent(new Intent(this,SavedSearchesActivity.class));
		
	tabHost.addTab(firstTabSpec);
	tabHost.addTab(secondTabSpec);
	
}

	
}
