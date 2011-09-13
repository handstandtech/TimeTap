package com.handstandtech.timetap.task;

import android.os.AsyncTask;
import android.util.Log;

import com.handstandtech.harvest.impl.DailyResponse;
import com.handstandtech.harvest.model.TimerResponse;
import com.handstandtech.timetap.Util;
import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public class UpdateTimerTask extends AsyncTask<Object, Integer, TimerResponse> {

  private static final String TAG = "TimeTap";
  private TimeTapBaseActivity context;
  private AsyncTaskCallback<TimerResponse> callback;
  private Long timer_id;
  private Long project_id;
  private Long task_id;
  private Double elapsedHours;
  private String notes;

  public UpdateTimerTask(TimeTapBaseActivity context, Long timer_id, Long project_id, Long task_id, Double elapsedHoursDecimal, String notes,
      AsyncTaskCallback<TimerResponse> callback) {
    super();
    this.context=context;
    this.timer_id = timer_id;
    this.project_id = project_id;
    this.task_id = task_id;
    this.elapsedHours = elapsedHoursDecimal;
    this.notes = notes;
    this.callback = callback;
  }

  @Override
  protected TimerResponse doInBackground(Object... is) {
    Log.e(TAG, "doInBackground");
    return Util.updateTimer(context, timer_id, project_id, task_id, elapsedHours, notes);
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
  protected void onPostExecute(TimerResponse item) {
    Log.e(TAG, "Finished.");
    callback.onTaskComplete(item);
  }
}