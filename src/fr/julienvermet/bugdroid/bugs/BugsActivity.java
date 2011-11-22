package fr.julienvermet.bugdroid.bugs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.bugs.search.SearchBug;
import fr.julienvermet.bugdroid.bugs.search.SearchBugLoadingActivity;
import fr.julienvermet.bugdroid.products.Product;
import fr.julienvermet.bugdroid.products.ProductsActivity;

public class BugsActivity extends Activity{

	Product product;
	ArrayList<Bug> bugs;
	EditText etSearchBar;
	static final int DIALOG_LOADING = 0;
	Context ctx;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bugs);

		Bundle extras = getIntent().getExtras();
		product = (Product) extras.getSerializable("product");
		bugs = (ArrayList<Bug>) extras.getSerializable("bugs");

		ctx = this;

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

		fillBugsList();
	}

	private void searchBug()
	{
		if (!etSearchBar.getText().toString().trim().equals(""))
		{
			Intent intent = new Intent(BugsActivity.this, SearchBugLoadingActivity.class);
			intent.putExtra("bug_id", Integer.parseInt( etSearchBar.getText().toString().trim() ) );
			startActivity(intent);
		}	
	}

	private void fillBugsList()
	{
		ListView lvBugs = (ListView) findViewById(R.id.lvBugs);
		BugsListAdapter bla = new BugsListAdapter(bugs, this, 0); //TODO : Image du bug
		lvBugs.setAdapter(bla);

		lvBugs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				Intent intent = new Intent(BugsActivity.this, SearchBugLoadingActivity.class);
				intent.putExtra("bug_id", bugs.get(position).getId());
				startActivity(intent);		
			}
		});
	}
}