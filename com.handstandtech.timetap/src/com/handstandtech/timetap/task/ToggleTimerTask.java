package com.handstandtech.timetap.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.handstandtech.harvest.model.TimerResponse;
import com.handstandtech.timetap.Util;
import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public class ToggleTimerTask extends AbstractAsyncTask<Object, Integer, TimerResponse> {

  private Long timerId;

  public ToggleTimerTask(TimeTapBaseActivity context, Long timerEntryId, AsyncTaskCallback<TimerResponse> callback) {
    super(context, callback);
    this.timerId = timerEntryId;
  }

  @Override
  protected TimerResponse doInBackground(Object... is) {
    HttpClient httpclient = new DefaultHttpClient();
    String url = Util.getHarvestBase(context) + "/daily/timer/" + timerId;
    HttpRequestBase requestBase = new HttpGet(url);
    Util.addBasicAuthAndHarvestHeaders(context, requestBase);
    try {
      HttpResponse httpResponse = httpclient.execute(requestBase);
      String content = Util.getContentFromHttpResponse(httpResponse);
      return Util.getGson().fromJson(content, TimerResponse.class);
    } catch (Exception e) {
      requestBase.abort();
      Log.e(TAG, e.getMessage(), e);
    }
    return null;
  }
}