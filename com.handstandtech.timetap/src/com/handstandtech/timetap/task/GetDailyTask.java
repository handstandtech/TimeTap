package com.handstandtech.timetap.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.handstandtech.harvest.impl.DailyResponse;
import com.handstandtech.timetap.Util;
import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public class GetDailyTask extends AbstractAsyncTask<Object, Integer, DailyResponse> {

  public GetDailyTask(TimeTapBaseActivity context, AsyncTaskCallback<DailyResponse> callback) {
    super(context, callback);
  }

  @Override
  protected  DailyResponse doInBackground(Object... is) {
    HttpClient httpclient = new DefaultHttpClient();
    String url = Util.getHarvestBase(context) + "/daily";
    HttpRequestBase requestBase = new HttpGet(url);
    Util.addBasicAuthAndHarvestHeaders(context, requestBase);
    try {
      HttpResponse httpResponse = httpclient.execute(requestBase);
      String content = Util.getContentFromHttpResponse(httpResponse);
      DailyResponse dailyResponse = Util.getGson().fromJson(content, DailyResponse.class);
      return dailyResponse;
    } catch (Exception e) {
      requestBase.abort();
      Log.e(TAG, e.getMessage(), e);
    }
    return null;
  }
  
  @Override
  protected void onPostExecute(DailyResponse items) {
    callback.onTaskComplete(items);
  }
}