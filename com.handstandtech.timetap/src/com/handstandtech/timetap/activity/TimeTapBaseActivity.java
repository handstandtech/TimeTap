package com.handstandtech.timetap.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;

import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.application.TimeTapApplication;

public class TimeTapBaseActivity extends Activity {

  protected static final String PROP_USERNAME = "username";
  protected static final String PROP_PASSWORD = "password";
  public static final String TAG = "TimeTap";

  protected static SharedPreferences preferences; // Instantiating editor

  // object

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    preferences = getSharedPreferences(TAG, android.content.Context.MODE_PRIVATE);
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

  protected void setUsernameInPrefs(String username) {
    Editor editor = preferences.edit();
    editor.putString(Constants.PREF_USERNAME, username);
    editor.commit();
  }

  protected void setPasswordInPrefs(String password) {
    Editor editor = preferences.edit();
    editor.putString(Constants.PREF_PASSWORD, password);
    editor.commit();
  }

  protected void setSubdomainInPrefs(String subdomain) {
    Editor editor = preferences.edit();
    editor.putString(Constants.PREF_SUBDOMAIN, subdomain);
    editor.commit();
  }

  public TimeTapApplication getTimeTap() {
    return (TimeTapApplication) getApplication();
  }

}