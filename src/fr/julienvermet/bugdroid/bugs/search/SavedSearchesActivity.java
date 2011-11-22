package fr.julienvermet.bugdroid.bugs.search;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.database.Database;
import fr.julienvermet.bugdroid.products.Product;
import fr.julienvermet.bugdroid.tools.Tools;

public class SavedSearchesActivity extends Activity {

	Product product;
	ArrayList<Search> searches = new ArrayList<Search>();
	Database db;
	boolean myOpenBugs = false;

	private static final String PREFS_NAME = "login";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = new Database(this);

		setContentView(R.layout.search_saved);
		fillBugsList();
		
		/*if (searches.size() == 0) {
			TextView tv = new TextView(this);
			tv.setText("No saved search yet");
			tv.setGravity(Gravity.CENTER);
			setContentView(tv);
		} else {
			setContentView(R.layout.search_saved);
			fillBugsList();
		}*/
	}

	private void fillBugsList() {
		ListView lvSavedSearches = (ListView) findViewById(R.id.lvSavedSearches);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String username = settings.getString("username", "");
		String password = settings.getString("password", "");

		if (!username.equals("") && !password.equals("")) {
			
			String params = "resolution=---&resolution=DUPLICATE&emailtype1=exact&query_based_on=Myopenbugs&emailassigned_to1=1&query_format=advanced&bug_status=UNCONFIRMED&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&email1="+ username +"&known_name=My open bugs";
			myOpenBugs = true;
			
			Search search = new Search(0, "myOpenBugs", params);
			
			searches.add(search);
		}
		
		searches.addAll( db.getSavedSearches() );

		final SavedSearchesListAdapter bla = new SavedSearchesListAdapter(
				searches, this);
		lvSavedSearches.setAdapter(bla);
		registerForContextMenu(lvSavedSearches);

		lvSavedSearches.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				searchBugs(bla.getItem(position));
			}
		});
	}

	private void searchBugs(Search search) {

		/*
		 * String url = "query_format=specific&order=relevance+desc&bug_status="
		 * + search.getStatus() + "&product=" + search.getProduct() +
		 * "&content=" + search.getWords() + "&changed_after=" +
		 * search.getDays() + "d";
		 */

		String url = search.getParams();

		Intent intent = new Intent(SavedSearchesActivity.this,
				SearchBugsLoadingActivity.class);
		intent.putExtra("type", "search");
		intent.putExtra("params", url);
		startActivity(intent);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		if (v.getId() == R.id.lvSavedSearches) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle("Action");

			menu.add(0, 1, 0, "Delete");

		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();

		int searchId = searches.get(info.position).getId();
		
		if (searchId==0)
			Tools.showToast(this, "Can't delete your open bugs search");
		else
		{
			db.deleteSavedSearch(searchId);
			searches = db.getSavedSearches();
			fillBugsList();
		}
		
		return true;
	}
}