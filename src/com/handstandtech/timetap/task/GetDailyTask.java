package com.handstandtech.timetap.task;

import android.os.AsyncTask;
import android.util.Log;

import com.handstandtech.harvest.impl.DailyResponse;
import com.handstandtech.timetap.Util;
import com.handstandtech.timetap.activity.HomeActivity;
import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public class GetDailyTask extends AsyncTask<Object, Integer, DailyResponse> {

  private static final String TAG = "TimeTap";
  private TimeTapBaseActivity activityContext;
  private AsyncTaskCallback<DailyResponse> callback;

  public GetDailyTask(HomeActivity context, AsyncTaskCallback<DailyResponse> callback) {
    super();
    this.activityContext = context;
    this.callback = callback;
  }

  @Override
  protected  DailyResponse doInBackground(Object... is) {
    Log.e(TAG, "doInBackground");
    return Util.getProjects(activityContext);
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
  protected void onPostExecute(DailyResponse items) {
    Log.e(TAG, "Finished.");
    callback.onTaskComplete(items);
  }
}