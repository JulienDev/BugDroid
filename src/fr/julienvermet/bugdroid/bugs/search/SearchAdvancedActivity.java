package fr.julienvermet.bugdroid.bugs.search;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;
import fr.julienvermet.bugdroid.R;

public class SearchAdvancedActivity extends Activity {

	String[] listClassification = { "", "Client Sofware", "Components",
			"Server Software", "Other", "Graveyard" };
	String[] listProducts = { "Add-on SDK", "addons.mozilla.org", "AUS",
			"Bugzilla", "bugzilla.mozilla.org", "Calendar", "Camino", "CCK",
			"Composer", "Core", "Core Graveyard", "Derivatives", "Directory",
			"Documentation", "Extend Firefox", "Fennec", "Firefox", "Grendel",
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.search_advanced);

		Spinner sClassification = (Spinner) findViewById(R.id.sClassification);
		SpinnerAdapter saClassification = new SpinnerAdapter(listClassification, this);
		sClassification.setAdapter(saClassification);

		Spinner sProduct = (Spinner) findViewById(R.id.sProduct);
		SpinnerAdapter saProducts = new SpinnerAdapter(listProducts, this);
		sProduct.setAdapter(saProducts);

	}

}
