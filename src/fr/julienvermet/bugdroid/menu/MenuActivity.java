package fr.julienvermet.bugdroid.menu;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewFlipper;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.bugs.SavedBugsActivity;
import fr.julienvermet.bugdroid.bugs.search.SearchBugActivity;
import fr.julienvermet.bugdroid.bugs.search.SearchBugLoadingActivity;
import fr.julienvermet.bugdroid.preferences.PreferencesActivity;
import fr.julienvermet.bugdroid.products.ProductsActivity;
import fr.julienvermet.bugdroid.tools.Tools;

public class MenuActivity extends Activity implements OnItemClickListener {

	private static final int sStopFlash = 0;
	private static final long sSplashTime = 100;
	ViewFlipper viewFlipper;
	ArrayList<Menu> menus = new ArrayList<Menu>();
	Animation animFadeIn;
	EditText etSearchBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		menus.add(new Menu("List bugs", ProductsActivity.class,
				R.drawable.file_a_bug));
		menus.add(new Menu("Saved bugs", SavedBugsActivity.class,
				R.drawable.file_a_bug));
		menus.add(new Menu("Search", SearchBugActivity.class, R.drawable.search));
		menus.add(new Menu("Prefs", PreferencesActivity.class, R.drawable.users));

		if (GetOrientation() == 0) {
			portrait();
		} else {
			paysage();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { // Si
			// paysage
			paysage();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) { // Si
			// portrait
			portrait();
		}
	}

	public int GetOrientation() {
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
				.getDefaultDisplay();

		return display.getOrientation();
	}

	public void portrait() {
		setContentView(R.layout.menu);
		
		Animation animSlideLeft = AnimationUtils.loadAnimation(this,
				R.anim.slide_left_offset);
		Animation animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		Animation slide_top_offset = AnimationUtils.loadAnimation(this,
				R.anim.slide_top_offset);
		Animation slide_bottom_offset = AnimationUtils.loadAnimation(this,
				R.anim.slide_bottom_offset);

		LinearLayout llSearchBar = (LinearLayout) findViewById(R.id.llSearchBar);
		llSearchBar.setAnimation(slide_bottom_offset);

		etSearchBar = (EditText) findViewById(R.id.etSearchBar);
		etSearchBar.setHint("Bug ID");
		etSearchBar.setInputType(InputType.TYPE_CLASS_NUMBER);

		Button bSearchBar = (Button) findViewById(R.id.bSearchBar);
		bSearchBar.setText("Quick access");
		bSearchBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				searchBug();
			}
		});

		ImageView ivInfo = (ImageView) findViewById(R.id.ivInfo);
		ivInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(0);				
			}
		});		
		ivInfo.setAnimation(slide_top_offset);

		ListView llMenu = (ListView) findViewById(R.id.llMenu);
		llMenu.startAnimation(animFadeIn);
		MenuListAdapter mla = new MenuListAdapter(menus, this);
		llMenu.setAdapter(mla);
		llMenu.setOnItemClickListener(this);

		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		viewFlipper.setInAnimation(MenuActivity.this,
				R.anim.view_transition_in_left);
		viewFlipper.setOutAnimation(MenuActivity.this,
				R.anim.view_transition_out_left);

		ImageView ivBugdroid = (ImageView) findViewById(R.id.ivBugdroid);
		ivBugdroid.startAnimation(animSlideLeft);

		// Create a message
		Message msg = new Message();
		msg.what = sStopFlash;
		splashHandler.sendMessageDelayed(msg, sSplashTime);
	}

	public void paysage() {
		setContentView(R.layout.menu);
		
		Animation animSlideLeft = AnimationUtils.loadAnimation(this,
				R.anim.slide_left_offset);
		Animation animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		Animation slide_top_offset = AnimationUtils.loadAnimation(this,
				R.anim.slide_top_offset);
		Animation slide_bottom_offset = AnimationUtils.loadAnimation(this,
				R.anim.slide_bottom_offset);
		
		LinearLayout llSearchBar = (LinearLayout) findViewById(R.id.llSearchBar);
		llSearchBar.setAnimation(slide_top_offset);

		etSearchBar = (EditText) findViewById(R.id.etSearchBar);
		etSearchBar.setHint("Bug ID");
		etSearchBar.setInputType(InputType.TYPE_CLASS_NUMBER);

		Button bSearchBar = (Button) findViewById(R.id.bSearchBar);
		bSearchBar.setText("Quick access");
		bSearchBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				searchBug();
			}
		});

		ImageView ivInfo = (ImageView) findViewById(R.id.ivInfo);
		ivInfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(0);				
			}
		});
		ivInfo.setAnimation(slide_bottom_offset);

		ImageView ivBugdroidLandscape = (ImageView) findViewById(R.id.ivBugdroidLandscape);
		ivBugdroidLandscape.startAnimation(animSlideLeft);

		Animation anim3 = AnimationUtils.loadAnimation(this,
				R.anim.view_transition_in_right);
		ImageView ivDinosaureLandscape = (ImageView) findViewById(R.id.ivDinosaureLandscape);
		ivDinosaureLandscape.startAnimation(anim3);

		GridView gvMenu = (GridView) findViewById(R.id.gvMenu);
		gvMenu.startAnimation(animSlideLeft);
		MenuListAdapter mla = new MenuListAdapter(menus, this);
		gvMenu.setAdapter(mla);
		gvMenu.setOnItemClickListener(this);
	}

	/**
	 * Handler of the splashscreen
	 */
	private Handler splashHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case sStopFlash:
				viewFlipper.showPrevious();

				break;
			}
			super.handleMessage(msg);
		}
	};

	private void launchActivity(int position) {
		if (menus.get(position).getIntent() == null)
			Tools.showToast(this, "Not yet availabe");
		else {
			Intent intent = new Intent(MenuActivity.this, menus.get(position)
					.getIntent());
			startActivity(intent);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			return createAboutDialog();
		default:
			return null;
		}
	}

	/**
	 * Crée une fenetre de dialogue
	 * 
	 * @return Dialog La fenetre de dialogue crée
	 */
	private Dialog createAboutDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		WebView wvAbout = new WebView(this);
		wvAbout.setPadding(5, 0, 0, 0);
		wvAbout.loadDataWithBaseURL(null, 
				"<font color='#ffffff'>" 
						+ "Project by :<br/>"
						+ "Julien Vermet <a href=\"mailto:ju.vermet@gmail.com\" ><font color=\"#ff0000\">ju.vermet@gmail.com</font></a><br/><br/>"
						+ "<div style='text-align: justify;'>" 
						+ "I'm looking for a 5 months internship (in Android dev, webdev, or IT) starting in February 2012. If you like my work and have something I might be interested in, email me :)"
						+ "</font></div>", null, "UTF-8", null);
		wvAbout.setBackgroundColor(Color.TRANSPARENT);

		builder.setTitle("About");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(wvAbout);
		builder.setPositiveButton(getString(android.R.string.ok), null);
		builder.setCancelable(true);
		return builder.create();
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		menu.add(0, 1000, 0, "About").setIcon(
				android.R.drawable.ic_menu_info_details);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 1000:
			showDialog(0);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		launchActivity(position);
	}

	private void searchBug()
	{
		if (!etSearchBar.getText().toString().trim().equals(""))
		{
			Intent intent = new Intent(MenuActivity.this, SearchBugLoadingActivity.class);
			intent.putExtra("bug_id", Integer.parseInt( etSearchBar.getText().toString().trim() ) );
			startActivity(intent);
		}	
	}
}