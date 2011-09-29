package com.handstandtech.timetap.task;

import android.os.AsyncTask;

import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public abstract class AbstractAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
  protected static final String TAG = "TimeTap";
  protected TimeTapBaseActivity context;
  protected AsyncTaskCallback<Result> callback;

  public AbstractAsyncTask(TimeTapBaseActivity context, AsyncTaskCallback<Result> callback) {
    super();
    this.context = context;
    this.callback = callback;
  }

  @Override
  protected void onPostExecute(Result result) {
    callback.onTaskComplete(result);
  }
}
