package com.handstandtech.timetap.task;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.handstandtech.harvest.impl.WhoAmIResponse;
import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.Util;
import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public class LoginTask extends AsyncTask<String, Integer, WhoAmIResponse> {

  private static final String TAG = "TimeTap";
  private TimeTapBaseActivity context;
  private SharedPreferences sharedPreferences; // Instantiating editor object
  private AsyncTaskCallback<WhoAmIResponse> callback;
  private String username;
  private String password;
  private String subdomain;

  public LoginTask(TimeTapBaseActivity context, String subdomain, String username, String password,
      AsyncTaskCallback<WhoAmIResponse> callback) {
    super();
    this.username = username;
    this.password = password;
    this.subdomain = subdomain;
    this.context = context;
    this.callback = callback;
    sharedPreferences = context.getSharedPreferences(TAG, android.content.Context.MODE_PRIVATE);
  }

  @Override
  protected WhoAmIResponse doInBackground(String... items) {
    Log.e(TAG, "doInBackground");

    if (username == null || password == null) {
      Log.e(TAG, "Username or Password was null");
    } else {
      String content = attemptLogin(subdomain, username, password);
      if (content != null) {
        Log.i(TAG, content);
        try {
          WhoAmIResponse response = new Gson().fromJson(content, WhoAmIResponse.class);
          return response;
        } catch (Exception e) {
          Log.w(TAG, e.getMessage(), e);
          return null;
        }
      }
    }
    return null;
  }

  private String attemptLogin(String subdomain, String username, String password) {
    Log.i(TAG, "Attempting Login");

    // TODO Auto-generated method stub
    AndroidHttpClient client = AndroidHttpClient.newInstance(Constants.ANDROID_HTTPCLIENT);

    // HttpClient client = new DefaultHttpClient();
    String url = Util.getHarvestBase(subdomain) + "/account/who_am_i";
    Log.i(TAG, "URL: " + url);
    HttpRequestBase requestBase = new HttpGet(url);

    Util.addHarvestHeaders(requestBase);

    Util.addBasicAuth(requestBase, username, password);
    try {
      HttpResponse response = client.execute(requestBase);
      HttpEntity reponseEntity = response.getEntity();
      InputStream contentInputStream = reponseEntity.getContent();
      BufferedReader rd = new BufferedReader(new InputStreamReader(contentInputStream));
      // read d response till d end
      StringBuffer content = new StringBuffer();
      String line = null;
      while ((line = rd.readLine()) != null) {
        // process the line response
        content.append(line);
      }
      Log.i(TAG, content.toString());
      return content.toString();
    } catch (Exception e) {
      // Could provide a more explicit error message for IOException or
      // IllegalStateException
      requestBase.abort();
      Log.i("ImageDownloader", "Error while retrieving: " + url);
    } finally {
      if (client != null) {
        client.close();
      }
    }
    return null;
  }

  @Override
  protected void onCancelled() {
    Log.e(TAG, "onCancelled");

    super.onCancelled();
  }

  @Override
  protected void onPreExecute() {
    Log.e(TAG, "onPreExecute");
  }

  @Override
  protected void onProgressUpdate(Integer... progress) {
    // setProgressPercent(progress[0]);
    Log.e(TAG, "onProgessUpdate: " + progress[0]);
  }

  @Override
  protected void onPostExecute(WhoAmIResponse success) {
    Log.e(TAG, "onPostExecute: " + success);
    // showDialog("Downloaded " + result + " bytes");

    callback.onTaskComplete(success);
  }
}