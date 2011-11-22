package fr.julienvermet.bugdroid.bugs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.bugs.search.SearchBug;
import fr.julienvermet.bugdroid.database.Database;
import fr.julienvermet.bugdroid.products.Product;

public class SavedBugsActivity extends Activity{

	Product product;
	ArrayList<Bug> bugs = new ArrayList<Bug>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Database db = new Database(this);
		bugs = db.getSavedBugs();

		if (bugs.size() == 0)
		{
			TextView tv = new TextView(this);
			tv.setText("No bug saved yet");
			tv.setGravity(Gravity.CENTER);
			setContentView(tv);
		}
		else
		{
			setContentView(R.layout.saved_bugs);

			fillBugsList();
		}
	}

	private void fillBugsList()
	{
		ListView lvBugs = (ListView) findViewById(R.id.lvBugs);
		BugsListAdapter bla = new BugsListAdapter(bugs, this, R.drawable.dino);
		lvBugs.setAdapter(bla);

		lvBugs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				Bug bug = SearchBug.SearchBugById(bugs.get(position).getId(), getApplication(), false );

				if (bug != null)
				{
					Intent intent = new Intent(SavedBugsActivity.this, ABugActivity.class);
					intent.putExtra("bug", bug);
					startActivity(intent);
				}			
			}
		});
	}
}