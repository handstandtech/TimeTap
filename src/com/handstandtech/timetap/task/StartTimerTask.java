package com.handstandtech.timetap.task;

import android.os.AsyncTask;
import android.util.Log;

import com.handstandtech.harvest.impl.DailyResponse;
import com.handstandtech.harvest.model.TimerResponse;
import com.handstandtech.timetap.Util;
import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public class StartTimerTask extends AsyncTask<Object, Integer, TimerResponse> {

  private static final String TAG = "TimeTap";
  private TimeTapBaseActivity context;
  private AsyncTaskCallback<TimerResponse> callback;
  private String notes;
  private String hours;
  private Long taskId;
  private Long projectId;

  public StartTimerTask(TimeTapBaseActivity context, String notes, String hours, Long projectId, Long taskId,
      AsyncTaskCallback<TimerResponse> callback) {
    super();
    this.notes = notes;
    this.hours = hours;
    this.projectId = projectId;
    this.taskId = taskId;
    this.context = context;
    this.callback = callback;
  }

  @Override
  protected TimerResponse doInBackground(Object... is) {
    Log.e(TAG, "doInBackground");
    return Util.addNewTimeEntry(notes, hours, projectId, taskId, context);
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
    Log.e(TAG, "onProgessUpdate: " + progress[0]);
  }

  @Override
  protected void onPostExecute(TimerResponse items) {
    Log.e(TAG, "Finished.");
    callback.onTaskComplete(items);
  }
}