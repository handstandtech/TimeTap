package com.handstandtech.timetap.activity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handstandtech.harvest.impl.DailyResponse;
import com.handstandtech.harvest.model.Project;
import com.handstandtech.harvest.model.Task;
import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.R;
import com.handstandtech.timetap.SerializationUtil;
import com.handstandtech.timetap.task.AsyncTaskCallback;
import com.handstandtech.timetap.task.GetDailyTask;

public class HomeActivity extends TimeTapBaseActivity {
  protected static final String PROP_IMAGE = "image";

  private DailyResponse items = null;

  public HomeActivity() {
    super();

  }

  /**
   * Prevent User from going back to the Login Screen
   */
  @Override
  public void onBackPressed() {
    return;
  }

  /** Called when the activity is first created. */
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);

    Log.i(TAG, "onCreate");

    if (savedInstanceState != null) {
      byte[] itemsBytes = savedInstanceState.getByteArray(Constants.PROP_ITEMS);
      items = (DailyResponse) SerializationUtil.getObjectFromBytes(itemsBytes);
      if (items != null) {
        addItems(items);
      } else {
        callGetItems();
      }
    } else {
      callGetItems();
    }
  }

  private void callGetItems() {

    final LinearLayout loadingPanel = (LinearLayout) findViewById(R.id.loading_panel);

    GetDailyTask getItems = new GetDailyTask(this, new AsyncTaskCallback<DailyResponse>() {

      public void onTaskComplete(DailyResponse result) {
        HomeActivity.this.items = result;
        addItems(result);
        ScrollView contentPanel = (ScrollView) findViewById(R.id.home_content);
        contentPanel.setVisibility(View.VISIBLE);
        loadingPanel.setVisibility(View.GONE);
      }
    });
    loadingPanel.setVisibility(View.VISIBLE);
    getItems.execute(null);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.i(TAG, "On Activity Result");
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
    savedInstanceState.putByteArray(Constants.PROP_ITEMS, SerializationUtil.getBytes(items));
  }
  

  public void addItems(DailyResponse items) {

    final LinearLayout taskVerticalListLinearLayout = (LinearLayout) findViewById(R.id.task_list);

    // TODO HEREHEHRHEEHEHEHREHREH
    for (final Project project : items.getProjects()) {
      Log.i(TAG, project.getName());
      for (final Task task : project.getTasks()) {
        Log.i(TAG, task.getName());
        LinearLayout newItemView = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item,
            taskVerticalListLinearLayout, false);
        TextView projectTextView = (TextView) newItemView.findViewById(R.id.project_text);
        TextView taskTextView = (TextView) newItemView.findViewById(R.id.task_text);
        projectTextView.setText(project.getName());
        taskTextView.setText(task.getName());

        newItemView.setOnClickListener(new OnClickListener() {

          public void onClick(View v) {
            Intent newIntent = new Intent(HomeActivity.this, TaskScreenActivity.class);
            Bundle bundle = new Bundle();
            bundle.putLong(Constants.PROP_PROJECT, project.getId());
            bundle.putLong(Constants.PROP_TASK, task.getId());
            newIntent.putExtras(bundle);
            startActivityForResult(newIntent, 0);
          }
        });

        taskVerticalListLinearLayout.addView(newItemView, taskVerticalListLinearLayout.getChildCount());
      }

    }
  }
}