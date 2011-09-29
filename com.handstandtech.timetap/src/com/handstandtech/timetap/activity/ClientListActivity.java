package com.handstandtech.timetap.activity;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.R;
import com.handstandtech.timetap.task.AsyncTaskCallback;
import com.handstandtech.timetap.task.GetDailyTask;

public class ClientListActivity extends AbstractListActivity {

  public ClientListActivity() {
    super();
  }

  /**
   * Prevent User from going back to the Login Screen
   */
  @Override
  public void onBackPressed() {
    // return;

    new AlertDialog.Builder(this).setMessage("Exit TimeTap?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
          }
        }).setNegativeButton("No", null).show();
  }

  /** Called when the activity is first created. */
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_layout);

    Intent intent = getIntent();
    if (intent != null) {
      Bundle bundle = getIntent().getExtras();
      if (bundle != null) {
        Boolean forceRefresh = bundle.getBoolean(Constants.PROP_FORCE_RELOAD);
        // Put as Null
        if (forceRefresh != null) {
          if (forceRefresh.booleanValue() == true) {
            getTimeTap().setDailyResponse(null);
          }
        }
      }
    }

    getRedListHeader().setText("CLIENTS");

    getContentScrollPanel().setVisibility(View.VISIBLE);
    getLoadingPanel().setVisibility(View.GONE);

    DailyResponse items = getTimeTap().getDailyResponse();
    if (items != null) {
      addItems(items);
    } else {
      callGetItems();
    }
  }

  private void callGetItems() {
    final LinearLayout loadingPanel = getLoadingPanel();
    final ScrollView contentPanel = getContentScrollPanel();

    contentPanel.setVisibility(View.GONE);
    loadingPanel.setVisibility(View.VISIBLE);

    GetDailyTask getItems = new GetDailyTask(this, new AsyncTaskCallback<DailyResponse>() {

      public void onTaskComplete(DailyResponse result) {
        getTimeTap().setDailyResponse(result);
        addItems(result);
        contentPanel.setVisibility(View.VISIBLE);
        loadingPanel.setVisibility(View.GONE);
      }
    });
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
      Toast toast = Toast.makeText(this, "TimeTap, created by Handstand Technologies", Toast.LENGTH_LONG);
      toast.setGravity(Gravity.CENTER, 0, 0);
      toast.show();
      return true;
    case R.id.menu_logout:
      //TODO FIX ERROR HERE
      // Logout
      Log.i(TAG, "Logging out current user.");

      // Reset All User Based Data
      setUsernameInPrefs(null);
      setPasswordInPrefs(null);
      setSubdomainInPrefs(null);
      getTimeTap().setDailyResponse(null);
      getTimeTap().setLocalStartTime(null);
      getTimeTap().setCurrTimer(null);
      Intent loginIntent = new Intent(this, LoginActivity.class);
      startActivity(loginIntent);
      return true;
    case R.id.menu_refresh:
      // Refresh
      // TODO Actually make this refresh, right now just uses cache
      Intent newIntent = new Intent(ClientListActivity.this, ClientListActivity.class);
      Bundle bundle = new Bundle();
      bundle.putBoolean(Constants.PROP_FORCE_RELOAD, true);
      newIntent.putExtras(bundle);
      startActivityForResult(newIntent, 0);
      return true;

    default:
      return super.onOptionsItemSelected(item);
    }
  }

  public void addItems(DailyResponse dailyResponse) {

    final LinearLayout taskVerticalListLinearLayout = getListLinearLayout();
    // TODO Add Clients instead of Project/Task
    for (final String clientName : dailyResponse.getClientNamesInAlphabeticalOrder()) {
      LinearLayout newItemView = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item,
          taskVerticalListLinearLayout, false);
      TextView projectTextView = (TextView) newItemView.findViewById(R.id.project_text);
      TextView taskTextView = (TextView) newItemView.findViewById(R.id.task_text);
      projectTextView.setText(clientName);
      List<Project> projects = dailyResponse.getClientToProjectsMap().get(clientName);
      int numProjects = projects.size();
      if (numProjects == 1) {
        // Singular
        taskTextView.setText(projects.size() + " Project Available");
      } else {
        // Plural
        taskTextView.setText(projects.size() + " Projects Available");
      }

      newItemView.setOnClickListener(new OnClickListener() {

        public void onClick(View v) {
          Intent newIntent = new Intent(ClientListActivity.this, ProjectListActivity.class);
          Bundle bundle = new Bundle();
          bundle.putString(Constants.PROP_CLIENT_NAME, clientName);
          newIntent.putExtras(bundle);
          startActivityForResult(newIntent, 0);
        }
      });

      taskVerticalListLinearLayout.addView(newItemView, taskVerticalListLinearLayout.getChildCount());
    }
  }
}