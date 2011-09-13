package com.handstandtech.timetap.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;

import com.handstandtech.timetap.Constants;

public class TimeTapBaseActivity extends Activity {

	protected static final String PROP_USERNAME = "username";
	protected static final String PROP_PASSWORD = "password";
	public static final String TAG = "TimeTap";

	protected static SharedPreferences preferences; // Instantiating editor
													// object

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = getSharedPreferences(TAG,
				android.content.Context.MODE_PRIVATE);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}

	public String getUsernameFromPrefs() {
		return preferences.getString(Constants.PREF_USERNAME, null);
	}
	

	public String getSubdomainFromPrefs() {
		return preferences.getString(Constants.PREF_SUBDOMAIN, null);
	}

	public String getPasswordFromPrefs() {
		return preferences.getString(Constants.PREF_PASSWORD, null);
	}

	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	//
	// unbindDrawables(findViewById(R.id.RootView));
	// System.gc();
	// }
	//
	// private void unbindDrawables(View view) {
	// if (view.getBackground() != null) {
	// view.getBackground().setCallback(null);
	// }
	// if (view instanceof ViewGroup) {
	// for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	// unbindDrawables(((ViewGroup) view).getChildAt(i));
	// }
	// ((ViewGroup) view).removeAllViews();
	// }
	// }
}