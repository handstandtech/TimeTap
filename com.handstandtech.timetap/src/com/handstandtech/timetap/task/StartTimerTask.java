package com.handstandtech.timetap.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.handstandtech.harvest.model.TimerResponse;
import com.handstandtech.timetap.Util;
import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public class StartTimerTask extends AbstractAsyncTask<Object, Integer, TimerResponse> {

  private String notes;
  private String hours;
  private Long task_id;
  private Long project_id;

  public StartTimerTask(TimeTapBaseActivity context, String notes, String hours, Long projectId, Long taskId,
      AsyncTaskCallback<TimerResponse> callback) {
    super(context, callback);
    this.notes = notes;
    this.hours = hours;
    this.project_id = projectId;
    this.task_id = taskId;
  }

  @Override
  protected TimerResponse doInBackground(Object... is) {
    JSONObject requestJSON = buildRequestJson();

    DefaultHttpClient httpclient = new DefaultHttpClient();
    String url = Util.getHarvestBase(context) + "/daily/add";
    HttpPost requestBase = new HttpPost(url);
    Util.addBasicAuthAndHarvestHeaders(context, requestBase);

    try {
      Log.i(TAG, requestJSON.toString());
      requestBase.setEntity(new StringEntity(requestJSON.toString()));
      HttpResponse httpResponse = httpclient.execute(requestBase);
      String content = Util.getContentFromHttpResponse(httpResponse);
      Log.i(TAG, "Response: " + content);
      return Util.getGson().fromJson(content, TimerResponse.class);
    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
    }
    return null;
  }

  private JSONObject buildRequestJson() {
    JSONObject requestJSON = new JSONObject();
    try {
      requestJSON.put("notes", notes);
      requestJSON.put("hours", hours);
      requestJSON.put("project_id", project_id);
      requestJSON.put("task_id", task_id);
      SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");
      requestJSON.put("spent_at", sdf.format(new Date()));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return requestJSON;
  }
}