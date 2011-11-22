package fr.julienvermet.bugdroid.tools;

import android.content.Context;
import android.text.ClipboardManager;
import android.widget.Toast;

public class Tools {

	public static void copyToClipboard(Context ctx, String text, String description)
	{
		((ClipboardManager) ctx.getSystemService(ctx.CLIPBOARD_SERVICE)).setText(text);
		
		showToast(ctx, description + " copied to the clipboard");
	}
	
	public static void showToast(Context ctx, String text)
	{
		(Toast.makeText(ctx, text, Toast.LENGTH_SHORT)).show();
	}
}
