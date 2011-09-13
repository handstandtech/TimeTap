package com.handstandtech.timetap.activity;

import java.util.ArrayList;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.handstandtech.harvest.model.TimerResponse;
import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.R;
import com.handstandtech.timetap.Util;
import com.handstandtech.timetap.task.AsyncTaskCallback;
import com.handstandtech.timetap.task.StartTimerTask;
import com.handstandtech.timetap.task.UpdateTimerTask;

public class TaskScreenActivity extends TimeTapBaseActivity {
  protected static final String PROP_IMAGE = "image";
  private static final int VOICE_RECOGNITION_REQUEST_CODE = 0;

  private Long projectId;

  private Handler mHandler = new Handler();

  private Long taskId;
  private Long mStartTime;
  private TimerResponse currTimer;

  public TaskScreenActivity() {
    super();
  }

  /** Called when the activity is first created. */
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_screen);

    Bundle bundle = getIntent().getExtras();
    this.projectId = bundle.getLong(Constants.PROP_PROJECT);
    this.taskId = bundle.getLong(Constants.PROP_TASK);

    addStopTimerButtonListener();
    addVoiceRecognitionButtonListener();
    addSaveClickHandler();

    getProjectNameText().setText("");
    getTaskNameText().setText("");
    getHoursText().setText("");

    final ProgressDialog dialog = new ProgressDialog(this);
    dialog.setMessage("Starting Timer");
    dialog.setIndeterminate(true);
    dialog.setCancelable(false);
    dialog.show();

    // TODO do not reload this after cached
    Log.i(TAG, "Adding new time entry!");
    StartTimerTask timerTask = new StartTimerTask(TaskScreenActivity.this, "Started from TimeTap.", "", projectId,
        taskId, new AsyncTaskCallback<TimerResponse>() {

          public void onTaskComplete(final TimerResponse timerResponse) {
            // Handler Error?

            Toast.makeText(TaskScreenActivity.this, "Timer Started!\n\n" + Util.getGson().toJson(timerResponse),
                Toast.LENGTH_LONG).show();
            TaskScreenActivity.this.currTimer = timerResponse;
            getProjectNameText().setText(timerResponse.getProject());
            getTaskNameText().setText(timerResponse.getTask());
            // getHoursText().setText(timerResponse.getHours().toString());

            TaskScreenActivity.this.currTimer = timerResponse;
            if (dialog.isShowing()) {
              dialog.dismiss();
            }
            mStartTime = timerResponse.getCreated_at().getTime();
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, TimeConstants.ONE_SECOND);
          }
        });
    timerTask.execute(null);

  }

  private void addSaveClickHandler() {
    // TODO Auto-generated method stub
    Button saveButton = (Button) findViewById(R.id.save_button);
    // Register the onClick listener with the implementation above
    saveButton.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        final ProgressDialog dialog = new ProgressDialog(TaskScreenActivity.this);
        dialog.setMessage("Updating Timer");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        UpdateTimerTask updateTimer = new UpdateTimerTask(TaskScreenActivity.this, currTimer.getId(), currTimer
            .getProject_id(), currTimer.getTask_id(), getElapsedHoursDecimal(), getNotes(),
            new AsyncTaskCallback<TimerResponse>() {

              public void onTaskComplete(TimerResponse result) {
                // TODO Auto-generated method stub
                TaskScreenActivity.this.currTimer = result;
                Toast.makeText(TaskScreenActivity.this, "Timer Updated", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing()) {
                  dialog.dismiss();
                }
              }
            });
        updateTimer.execute(null);
      }

    });
  }

  protected Double getElapsedHoursDecimal() {
    final Date now = new Date();
    long millisecondsPassed = (now.getTime() - mStartTime);
    return millisecondsPassed / new Double(TimeConstants.ONE_HOUR).doubleValue();
  }

  private Runnable mUpdateTimeTask = new Runnable() {
    public void run() {
      final Date now = new Date();
      long millisecondsPassed = (now.getTime() - mStartTime);
      // Log.d(TAG, "Millis: " + millis);
      long totalTime = millisecondsPassed / TimeConstants.ONE_SECOND;

      long hours = millisecondsPassed / (TimeConstants.ONE_SECOND * 60 * 60);
      // Log.d(TAG, "Seconds: " + secondsTotal);
      long minutes = totalTime / 60;
      // Log.d(TAG, "Minutes: " + minutes);
      long seconds = totalTime % 60;
      // Log.i(TAG, "NOW: " + now);
      // Log.d(TAG, minutes + ":" + totalTime);

      String hoursString = getString(hours);
      String minutesString = getString(minutes);
      String secondsString = getString(seconds);
      getHoursText().setText(hoursString + ":" + minutesString + ":" + secondsString);

      mHandler.postDelayed(this, TimeConstants.ONE_SECOND);
    }
  };

  private TextView getProjectNameText() {
    return (TextView) findViewById(R.id.project_name);
  }

  protected String getString(Long n) {
    if (n < 10) {
      return "0" + n;
    } else {
      return n.toString();
    }
  }

  private TextView getHoursText() {
    return (TextView) findViewById(R.id.hours_text);
  }

  private TextView getTaskNameText() {
    return (TextView) findViewById(R.id.task_name);
  }

  private String getNotes() {
    EditText notesText = (EditText) findViewById(R.id.notes_text);
    return notesText.getText().toString();
  }

  private void addVoiceRecognitionButtonListener() {
    // Capture our button from layout
    ImageButton voiceButton = (ImageButton) findViewById(R.id.voice_recognition_button);
    // Register the onClick listener with the implementation above
    voiceButton.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        startVoiceRecognitionActivity();
      }

    });
  }

  private void addStopTimerButtonListener() {

    // Capture our button from layout
    Button stopTimerButton = (Button) findViewById(R.id.stop_timer_button);
    // Register the onClick listener with the implementation above
    stopTimerButton.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        Toast.makeText(TaskScreenActivity.this, "Stop Timer Here!!!", Toast.LENGTH_SHORT).show();

        Intent showActivityIntent = new Intent(TaskScreenActivity.this, HomeActivity.class);
        TaskScreenActivity.this.startActivity(showActivityIntent);
      }

    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.drawable.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
    case R.id.menu_info:
      // Info
      Toast toast = Toast.makeText(this, "Created by Handstand Technologies", Toast.LENGTH_LONG);
      toast.setGravity(Gravity.CENTER, 0, 0);
      toast.show();
      return true;
    case R.id.menu_logout:
      // Logout
      Log.i(TAG, "Logging out current user.");
      Editor preferencesEditor = preferences.edit();
      preferencesEditor.putString(Constants.PREF_USERNAME, null);
      preferencesEditor.putString(Constants.PREF_PASSWORD, null);
      preferencesEditor.putString(Constants.PREF_SUBDOMAIN, null);
      preferencesEditor.commit();
      Intent loginIntent = new Intent(this, LoginActivity.class);
      startActivity(loginIntent);
      return true;
    case R.id.menu_refresh:
      // Refresh
      startActivity(getIntent());
      finish();
      return true;

    default:
      return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    super.onSaveInstanceState(savedInstanceState);
  }

  /**
   * Fire an intent to start the speech recognition activity.
   */
  private void startVoiceRecognitionActivity() {
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Record Task Notes");
    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
  }

  /**
   * Handle the results from the recognition activity.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
      // Fill the list view with the strings the recognizer thought it could
      // have heard
      ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
      // mList.setAdapter(new ArrayAdapter<String>(this,
      // android.R.layout.simple_list_item_1,
      // matches));
      Toast.makeText(TaskScreenActivity.this, matches.toString(), Toast.LENGTH_LONG);

      if (matches != null && matches.size() > 0) {
        EditText notesText = (EditText) findViewById(R.id.notes_text);
        notesText.setText(matches.get(0));
      }
    }

    super.onActivityResult(requestCode, resultCode, data);
  }
}