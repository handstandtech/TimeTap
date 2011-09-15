package com.handstandtech.timetap.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.handstandtech.harvest.impl.WhoAmIResponse;
import com.handstandtech.timetap.Util;
import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public class LoginTask extends AbstractAsyncTask<String, Integer, WhoAmIResponse> {

  private String username;
  private String password;
  private String subdomain;

  public LoginTask(TimeTapBaseActivity context, String subdomain, String username, String password,
      AsyncTaskCallback<WhoAmIResponse> callback) {
    super(context, callback);
    this.username = username;
    this.password = password;
    this.subdomain = subdomain;
  }

  @Override
  protected WhoAmIResponse doInBackground(String... items) {
    if (username == null || password == null || subdomain == null) {
      Log.e(TAG, "Username or Password was null");
    } else {
      Log.i(TAG, "Attempting Login");
      DefaultHttpClient client = new DefaultHttpClient();
      String url = Util.getHarvestBase(subdomain) + "/account/who_am_i";
      Log.i(TAG, "URL: " + url);
      HttpRequestBase requestBase = new HttpGet(url);
      Util.addHarvestHeaders(requestBase);
      Util.addBasicAuth(requestBase, username, password);
      try {
        HttpResponse httpResponse = client.execute(requestBase);
        String content = Util.getContentFromHttpResponse(httpResponse);
        Log.i(TAG, content);
        return Util.getGson().fromJson(content, WhoAmIResponse.class);
      } catch (Exception e) {
        // Could provide a more explicit error message for IOException or
        // IllegalStateException
        requestBase.abort();
      }
    }
    return null;
  }
}