package fr.julienvermet.bugdroid.bugs;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.bugs.search.SearchBugLoadingActivity;
import fr.julienvermet.bugdroid.bugs.viewpager.PageAdapter;
import fr.julienvermet.bugdroid.bugs.viewpager.ViewPagerIndicator;
import fr.julienvermet.bugdroid.database.Database;

public class ABugActivity extends FragmentActivity{

	Bug bug;
	PageAdapter mPagerAdapter;
	ViewPager  mViewPager;

	Database db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			bug = (Bug) savedInstanceState.getSerializable("bug");
		} else {
			Bundle extras = getIntent().getExtras();
			bug = (Bug) extras.getSerializable("bug");
		}

		setContentView(R.layout.pager);

		db = new Database(getApplication());
		if (db.isBugSaved(bug.getId()))
			(Toast.makeText(getApplication(), "Bug in cache. Press Menu in order to update", Toast.LENGTH_LONG)).show();

		TextView tvProduct = (TextView) findViewById(R.id.tvProduct);
		tvProduct.setText( bug.getProduct() );

		// Create our custom adapter to supply pages to the viewpager.
		mPagerAdapter = new PageAdapter(getSupportFragmentManager(), bug);
		mViewPager = (ViewPager)findViewById(R.id.pager);
		mViewPager.setAdapter(mPagerAdapter);

		// Start at a custom position
		mViewPager.setCurrentItem(1);

		// Find the indicator from the layout
		ViewPagerIndicator indicator = (ViewPagerIndicator)findViewById(R.id.indicator);

		// Set the indicator as the pageChangeListener
		mViewPager.setOnPageChangeListener(indicator);

		// Initialize the indicator. We need some information here:
		// * What page do we start on.
		// * How many pages are there in total
		// * A callback to get page titles
		indicator.init(1, mPagerAdapter.getCount(), mPagerAdapter);
		Resources res = getResources();
		Drawable prev = res.getDrawable(R.drawable.indicator_prev_arrow);
		Drawable next = res.getDrawable(R.drawable.indicator_next_arrow);

		// Set images for previous and next arrows.
		indicator.setArrows(prev, next);
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {

		savedInstanceState.putSerializable("bug", bug);

		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.removeGroup(0);
		
		if (!db.isBugSaved(bug.getId()))
		{
			menu.add(0, 1000, 0, "Save").setIcon(android.R.drawable.ic_menu_save);
		}
		else
		{
			menu.add(0, 1001, 0, "Update").setIcon(android.R.drawable.ic_popup_sync);
			menu.add(0, 1002, 0, "Delete").setIcon(android.R.drawable.ic_menu_delete);
		}
		
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
				
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1000:
			saveBug();
			break;
		case 1001:
			updateBug();
			break;
		case 1002:
			deleteBug();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveBug()
	{
		db.isBugSaved(bug.id);

		if (db.insertBug(bug))
			(Toast.makeText(getApplication(), "Bug saved", Toast.LENGTH_SHORT)).show();
		else
			(Toast.makeText(getApplication(), "Can't save the bug", Toast.LENGTH_SHORT)).show();
	}

	private void updateBug()
	{
		Intent intent = new Intent(ABugActivity.this, SearchBugLoadingActivity.class);
		intent.putExtra("bug_id", bug.getId());
		intent.putExtra("update", true);
		startActivity(intent);
		finish();
	}
	
	private void deleteBug()
	{
		if (db.deleteBug(bug.getId()))
			(Toast.makeText(getApplication(), "Bug deleted", Toast.LENGTH_SHORT)).show();
		else
			(Toast.makeText(getApplication(), "Error while deleting bug", Toast.LENGTH_SHORT)).show();
	}
}