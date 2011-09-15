package com.handstandtech.timetap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.handstandtech.harvest.model.TimerResponse;
import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public class Util {
  private static Gson gson;
  static {
    GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
    // Add Serializers, etc
    builder.registerTypeAdapter(Date.class, new DateDeserializer());
    gson = builder.create();
  }

  private static class DateDeserializer implements JsonDeserializer<Date> {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    static {
      sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public Date deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
      String dateStr = arg0.getAsString();
      Date date = null;
      try {
        date = sdf.parse(dateStr);
      } catch (ParseException e) {

        e.printStackTrace();
      }
      return date;
    }

  }

  public static final String TAG = "TimeTap";

  public static Gson getGson() {
    return gson;
  }

  public static String getRealPathFromUri(Uri contentUri, Activity activity) {
    String[] proj = { MediaStore.Images.Media.DATA };
    Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    cursor.moveToFirst();
    return cursor.getString(column_index);
  }

  public static void addBasicAuth(HttpRequestBase requestBase, String username, String password) {
    requestBase.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(username, password),
        Constants.UTF_8, false));
  }

  public static void addHarvestHeaders(HttpRequestBase requestBase) {
    requestBase.addHeader("Accept", Constants.APPLICATION_JSON);
    requestBase.addHeader("Content-Type", Constants.APPLICATION_JSON);
  }

  public static String getContentFromHttpResponse(HttpResponse httpResponse) throws IOException {
    HttpEntity reponseEntity = httpResponse.getEntity();
    InputStream contentInputStream = reponseEntity.getContent();
    BufferedReader rd = new BufferedReader(new InputStreamReader(contentInputStream));
    // read d response till d end
    StringBuffer content = new StringBuffer();
    String line = null;
    while ((line = rd.readLine()) != null) {
      // process the line response
      content.append(line);
    }
    return content.toString();
  }

  /**
   * During debugging, sometimes we need to adjust the URL of an Image.
   * 
   * @param imageSrc
   *          What we got form the API
   * @return Correct Image SRC
   */
  public static String getFullImageUrl(String imageSrc) {
    // Strip out the HOST and add in the host we're using
    if (imageSrc.contains("0.0.0.0")) {
      // Remove up to and include "//"
      imageSrc = imageSrc.substring(imageSrc.indexOf("//") + 2, imageSrc.length());

      // Remove up to last "/"
      imageSrc = imageSrc.substring(imageSrc.indexOf("/"), imageSrc.length());
      return Constants.HOST + imageSrc;
    } else if (imageSrc.indexOf("http") == -1) {
      return Constants.HOST + imageSrc;
    }

    // Do Nothing
    return imageSrc;
  }

  public static String utf8Encode(String origSrc) {
    try {
      return URLEncoder.encode(origSrc, Constants.UTF_8);
    } catch (UnsupportedEncodingException e) {

      e.printStackTrace();
    }
    return null;
  }

  public static TimerResponse updateTimer(TimeTapBaseActivity context, Long timer_id, Long project_id, Long task_id,
      Double elapsedHoursDecimal, String notes) {
    String username = context.getUsernameFromPrefs();
    String password = context.getPasswordFromPrefs();
    String subdomain = context.getSubdomainFromPrefs();
    JSONObject requestJSON = new JSONObject();
    try {
      requestJSON.put("notes", notes);
      requestJSON.put("hours", elapsedHoursDecimal);
      requestJSON.put("project_id", project_id);
      requestJSON.put("task_id", task_id);
    } catch (JSONException e) {

      e.printStackTrace();
    }

    // AndroidHttpClient httpclient =
    // AndroidHttpClient.newInstance(Constants.ANDROID_HTTPCLIENT);
    DefaultHttpClient httpclient = new DefaultHttpClient();
    String url = getHarvestBase(context) + "/daily/update/" + timer_id;
    HttpPost requestBase = new HttpPost(url);
    addBasicAuth(requestBase, username, password);
    addHarvestHeaders(requestBase);

    try {
      Log.i(TAG, requestJSON.toString());
      requestBase.setEntity(new StringEntity(requestJSON.toString()));
      HttpResponse httpResponse = httpclient.execute(requestBase);
      String content = getContentFromHttpResponse(httpResponse);
      Log.i(TAG, "Response: " + content);
      TimerResponse timerResponse = getGson().fromJson(content, TimerResponse.class);
      return timerResponse;
    } catch (ClientProtocolException e) {
      Log.e(TAG, e.getMessage(), e);
    } catch (IOException e) {
      Log.e(TAG, e.getMessage(), e);
    }
    return null;
  }

  public static void addBasicAuthAndHarvestHeaders(TimeTapBaseActivity context, HttpRequestBase requestBase) {
    addHarvestHeaders(requestBase);
    addBasicAuth(requestBase, context.getUsernameFromPrefs(), context.getPasswordFromPrefs());
  }

  public static String getHarvestBase(TimeTapBaseActivity context) {
    String subdomain = context.getSubdomainFromPrefs();
    return getHarvestBase(subdomain);
  }

  public static String getHarvestBase(String subdomain) {
    return String.format("https://%s.harvestapp.com", subdomain);
  }

}
