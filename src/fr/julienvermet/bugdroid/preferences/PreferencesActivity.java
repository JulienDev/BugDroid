package fr.julienvermet.bugdroid.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import fr.julienvermet.bugdroid.R;
import fr.julienvermet.bugdroid.tools.Tools;

public class PreferencesActivity extends Activity {

	private static final String PREFS_NAME = "login";
	private EditText etUsername;
	private EditText etPassword;
	private String username;
	private String password;
	
	SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.preferences);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
	
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
	
		username = settings.getString("username", "");
		password = settings.getString("password", "");
	
		etUsername.setText(username);
		etPassword.setText(password);
	
		final TextView tvFrom = (TextView) findViewById(R.id.tvFrom);

		SeekBar sbFrom = (SeekBar) findViewById(R.id.sbFrom);
		sbFrom.setMax(47);
		sbFrom.setProgress( settings.getInt("hoursSearch", 1) - 1 );
		tvFrom.setText("List bugs from : " + (settings.getInt("hoursSearch", 1) ) + " hour(s)");
		sbFrom.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}
	
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}
	
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				tvFrom.setText("List bugs from : " + (arg1 + 1) + " hour(s)");
				editor.putInt("hoursSearch", (arg1 + 1));
				editor.commit();
			}
		});
	}

	public void saveLoginDetails(View v)
	{
		username = etUsername.getText().toString();
		password = etPassword.getText().toString();

		if (!username.equals("") && !password.equals(""))
		{
			editor.putString("username", username);
			editor.putString("password", password);

			editor.commit();

			Tools.showToast(this, "Login details saved");
		}
		else
			Tools.showToast(this, "Please enter username and password");
	}

	public void deleteLoginDetails(View v)
	{
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.remove("username");
		editor.remove("password");

		etUsername.setText("");
		etPassword.setText("");

		editor.commit();

		Tools.showToast(this, "Login details deleted");
	}
}