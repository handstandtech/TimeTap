package com.handstandtech.timetap.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handstandtech.harvest.model.TimerResponse;
import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.R;
import com.handstandtech.timetap.application.TimeTapApplication;
import com.handstandtech.timetap.task.AsyncTaskCallback;
import com.handstandtech.timetap.task.StartTimerTask;
import com.handstandtech.timetap.task.ToggleTimerTask;
import com.handstandtech.timetap.task.UpdateTimerTask;

public class TaskScreenActivity extends TimeTapBaseActivity {
  private static final int VOICE_RECOGNITION_REQUEST_CODE = 0;
  protected static final String RESUME_TIMER_TEXT = "Resume Timer";
  private static final String STOP_TIMER_TEXT = "Stop Timer";
  private static final int HELLO_ID = 1;

  private Handler runningTimerDisplayHandler = new Handler();

  public TaskScreenActivity() {
    super();
  }

  /** Called when the activity is first created. */
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task_screen);

    Bundle bundle = getIntent().getExtras();

    Boolean joinRunningTimer = bundle.getBoolean(Constants.PROP_JOIN_RUNNING_TIMER);
    Long projectId = bundle.getLong(Constants.PROP_PROJECT_ID);
    Long taskId = bundle.getLong(Constants.PROP_TASK_ID);

    getToggleTimerButton().setOnClickListener(stopTimerListener);
    addVoiceRecognitionButtonListener();
    addSaveClickHandler();
    addTextFocusHandler();

    getProjectNameText().setText("");
    getTaskNameText().setText("");
    getHoursText().setText("");

    final Date now = new Date();
    final TimeTapApplication timeTapApp = TaskScreenActivity.this.getTimeTap();

    if (joinRunningTimer) {
      TimerResponse timerResponse = timeTapApp.getCurrTimer();
      updateTextAndStartTimer(timerResponse, timeTapApp.getLocalStartTime());
    } else {
      // Stop Notification
      removeNotification();
      final ProgressDialog dialog = new ProgressDialog(this);
      dialog.setMessage("Starting Timer");
      dialog.setIndeterminate(true);
      dialog.setCancelable(true);
      dialog.show();

      // TODO do not start timer again if started
      Log.i(TAG, "Adding new time entry!");
      StartTimerTask timerTask = new StartTimerTask(TaskScreenActivity.this, "", "", projectId, taskId,
          new AsyncTaskCallback<TimerResponse>() {

            public void onTaskComplete(final TimerResponse timerResponse) {
              if (dialog.isShowing()) {
                dialog.dismiss();
              }

              // Handle a NULL Result
              if (timerResponse == null) {
                Toast.makeText(TaskScreenActivity.this, "ERROR: You do not have access to this project/task!",
                    Toast.LENGTH_LONG).show();
                Intent loginIntent = new Intent(TaskScreenActivity.this, ClientListActivity.class);
                startActivity(loginIntent);
              } else {
                TaskScreenActivity.this.getTimeTap().setCurrTimer(timerResponse);
                updateTextAndStartTimer(timerResponse, now.getTime());
              }
            }

          });
      timerTask.execute(null);
    }

  }

  protected void updateTextAndStartTimer(TimerResponse timerResponse, Long now) {
    getProjectNameText().setText(timerResponse.getProject());
    getTaskNameText().setText(timerResponse.getTask());
    getNotesEditText().setText(timerResponse.getNotes());
    getSaveButtonWrapper().setVisibility(View.GONE);

    TaskScreenActivity.this.getTimeTap().setLocalStartTime(now);
    runningTimerDisplayHandler.removeCallbacks(mUpdateTimeTask);
    runningTimerDisplayHandler.post(mUpdateTimeTask);
  }

  private void addTextFocusHandler() {
    EditText editText = getNotesEditText();
    editText.addTextChangedListener(new TextWatcher() {

      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      public void afterTextChanged(Editable s) {
        // Make Save Button Wrapper Visible
        getSaveButtonWrapper().setVisibility(View.VISIBLE);
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.drawable.write_task_tag_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    switch (id) {
    case R.id.menu_write_task_tag:
      try {
        TimerResponse currTimer = getTimeTap().getCurrTimer();
        String uri = "http://handstandtech.com/timetap?project=" + currTimer.getProject_id() + "&task="
            + currTimer.getTask_id();

        // Start intent to write tag
        Intent newIntent = new Intent(TaskScreenActivity.this, TagWriterActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PROP_URI, uri);
        newIntent.putExtras(bundle);
        startActivityForResult(newIntent, 0);

        // Toast.makeText(TaskScreenActivity.this, "TODO: Set tag to " + uri,
        // Toast.LENGTH_SHORT).show();
      } catch (Exception e) {
        Log.d(TAG, "Error writing tag", e);
        Toast.makeText(TaskScreenActivity.this, "Error writing tag.", Toast.LENGTH_SHORT).show();
      }
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  private void addSaveClickHandler() {
    final TimeTapBaseActivity context = TaskScreenActivity.this;
    Button saveButton = getSaveButton();
    // Register the onClick listener with the implementation above
    saveButton.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Updating Timer");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        // Hide Soft Keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getNotesEditText().getWindowToken(), 0);

        // Make Save Button Wrapper "Gone"
        getSaveButtonWrapper().setVisibility(View.GONE);

        TimerResponse currTimer = context.getTimeTap().getCurrTimer();
        Long localStartTime = context.getTimeTap().getLocalStartTime();
        String notes = getNotesEditText().getText().toString();

        Double elapsedHours = currTimer.getHours();
        if (localStartTime != null) {
          elapsedHours = getElapsedHoursDecimal();
        }

        UpdateTimerTask updateTimer = new UpdateTimerTask(context, currTimer.getId(), currTimer.getProject_id(),
            currTimer.getTask_id(), elapsedHours, notes, new AsyncTaskCallback<TimerResponse>() {

              public void onTaskComplete(TimerResponse result) {
                context.getTimeTap().setCurrTimer(result);
                Toast.makeText(context, "Timer Updated!", Toast.LENGTH_SHORT).show();
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
    Long localStartTime = getTimeTap().getLocalStartTime();
    if (localStartTime != null) {
      final Date now = new Date();
      long millisecondsPassed = (now.getTime() - localStartTime);
      return millisecondsPassed / new Double(TimeConstants.ONE_HOUR).doubleValue();
    } else {
      return null;
    }
  }

  private Runnable mUpdateTimeTask = new Runnable() {
    public void run() {

      DecimalFormat df = new DecimalFormat("#.##");
      Double elapsedHoursDecimal = getElapsedHoursDecimal();
      if (elapsedHoursDecimal != null) {
        String decimalString = df.format(elapsedHoursDecimal);
        if (decimalString.equals("0")) {
          decimalString = "0.00";
        }
        getHoursText().setText(decimalString);

        startNotificationBarItem(TaskScreenActivity.this.getTimeTap().getCurrTimer(), decimalString);

        runningTimerDisplayHandler.postDelayed(this, TimeConstants.ONE_SECOND);
      } else {
        runningTimerDisplayHandler.removeCallbacks(mUpdateTimeTask);
        TaskScreenActivity.this.finish();
      }
    }
  };

  private void startNotificationBarItem(TimerResponse timerResponse, String decimalString) {
    Context context = TaskScreenActivity.this.getApplicationContext();

    Intent notificationIntent = new Intent(TaskScreenActivity.this, TaskScreenActivity.class);
    Bundle bundle = new Bundle();
    bundle.putBoolean(Constants.PROP_JOIN_RUNNING_TIMER, true);
    notificationIntent.putExtras(bundle);
    PendingIntent contentIntent = PendingIntent.getActivity(TaskScreenActivity.this, 0, notificationIntent, 0);

    String contentTitle = "Timer Running " + decimalString + " Hours";
    String contentText = timerResponse.getProject() + " - " + timerResponse.getTask();
    Notification notification = new Notification(R.drawable.icon, "Timer Running", System.currentTimeMillis());
    notification.flags = Notification.FLAG_ONGOING_EVENT;
    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify(HELLO_ID, notification);
  }

  // /**
  // * Print time to 00:00:00 format
  // */
  // private String clockFormat(Long start) {
  // final Long now = new Date().getTime();
  // long millisecondsPassed = (now - start);
  // Log.d(TAG, "Now: " + new Date(now));
  // Log.d(TAG, "Start: " + new Date(start));
  // long totalTime = millisecondsPassed / TimeConstants.ONE_SECOND;
  // long hours = millisecondsPassed / (TimeConstants.ONE_SECOND * 60 * 60);
  // long minutes = totalTime / 60;
  // long seconds = totalTime % 60;
  // String hoursString = getString(hours);
  // String minutesString = getString(minutes);
  // String secondsString = getString(seconds);
  // return hoursString + ":" + minutesString + ":" + secondsString;
  // }
  // protected String getString(Long n) {
  // if (n < 10) {
  // return "0" + n;
  // } else {
  // return n.toString();
  // }
  // }

  private TextView getProjectNameText() {
    return (TextView) findViewById(R.id.project_name);
  }

  private LinearLayout getSaveButtonWrapper() {
    return (LinearLayout) findViewById(R.id.save_button_wrapper);
  }

  private Button getSaveButton() {
    return (Button) findViewById(R.id.save_button);
  }

  private Button getToggleTimerButton() {
    return (Button) findViewById(R.id.toggle_timer_button);
  }

  private TextView getHoursText() {
    return (TextView) findViewById(R.id.hours_text);
  }

  private TextView getTaskNameText() {
    return (TextView) findViewById(R.id.task_name);
  }

  private EditText getNotesEditText() {
    return (EditText) findViewById(R.id.notes_text);
  }

  private void addVoiceRecognitionButtonListener() {
    // Capture our button from layout
    ImageButton voiceButton = (ImageButton) findViewById(R.id.voice_recognition_button);
    // Register the onClick listener with the implementation above
    voiceButton.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        // Fire an intent to start the speech recognition activity.
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Record Task Notes");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
      }

    });
  }

  private OnClickListener stopTimerListener = new OnClickListener() {
    public void onClick(View v) {
      final TimeTapBaseActivity context = TaskScreenActivity.this;
      final ProgressDialog dialog = new ProgressDialog(context);
      dialog.setMessage("Stopping Timer");
      dialog.setIndeterminate(true);
      dialog.setCancelable(true);
      dialog.show();
      final TimerResponse currTimer = TaskScreenActivity.this.getTimeTap().getCurrTimer();
      ToggleTimerTask toggleTimerTask = new ToggleTimerTask(context, currTimer.getId(),
          new AsyncTaskCallback<TimerResponse>() {

            public void onTaskComplete(TimerResponse result) {
              if (dialog.isShowing()) {
                dialog.dismiss();
              }

              // Stop Notification
              removeNotification();

              // Stop the Timer Display
              runningTimerDisplayHandler.removeCallbacks(mUpdateTimeTask);

              Button toggleTimerButton = getToggleTimerButton();
              toggleTimerButton.setText(RESUME_TIMER_TEXT);

              getToggleTimerButton().setOnClickListener(resumeTimerListener);

              // Update currentTimer
              context.getTimeTap().setCurrTimer(result);
              context.getTimeTap().setLocalStartTime(null);
            }

          });
      toggleTimerTask.execute(null);
    }
  };

  private void removeNotification() {
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.cancel(HELLO_ID);
  }

  private OnClickListener resumeTimerListener = new OnClickListener() {
    public void onClick(View v) {
      final ProgressDialog dialog = new ProgressDialog(TaskScreenActivity.this);
      dialog.setMessage("Resuming Timer");
      dialog.setIndeterminate(true);
      dialog.setCancelable(true);
      dialog.show();

      final TimerResponse currTimer = TaskScreenActivity.this.getTimeTap().getCurrTimer();
      ToggleTimerTask toggleTimerTask = new ToggleTimerTask(TaskScreenActivity.this, currTimer.getId(),
          new AsyncTaskCallback<TimerResponse>() {

            public void onTaskComplete(TimerResponse result) {
              if (dialog.isShowing()) {
                dialog.dismiss();
              }

              if (result == null) {
                Toast.makeText(TaskScreenActivity.this, "An Error Occurred", Toast.LENGTH_LONG);
              } else {
                // Update currentTimer
                TaskScreenActivity.this.getTimeTap().setCurrTimer(result);

                // Restart the Timer Display
                runningTimerDisplayHandler.post(mUpdateTimeTask);
                Double hours = result.getHours();
                Long timePassed = (long) (hours * TimeConstants.ONE_HOUR);
                Long nowTime = new Date().getTime();
                TaskScreenActivity.this.getTimeTap().setLocalStartTime(nowTime - timePassed);
                Button toggleTimerButton = getToggleTimerButton();
                toggleTimerButton.setText(STOP_TIMER_TEXT);

                getToggleTimerButton().setOnClickListener(stopTimerListener);
              }

            }
          });
      toggleTimerTask.execute(null);
    }
  };

  /**
   * Handle the results from the recognition activity.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
      // Fill the list view with the strings the recognizer thought it could
      // have heard
      ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
      Toast.makeText(TaskScreenActivity.this, matches.toString(), Toast.LENGTH_LONG);

      if (matches != null && matches.size() > 0) {
        EditText notesText = (EditText) findViewById(R.id.notes_text);
        notesText.setText(matches.get(0));
      }
    }

    super.onActivityResult(requestCode, resultCode, data);
  }
}