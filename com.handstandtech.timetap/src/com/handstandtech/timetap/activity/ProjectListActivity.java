package com.handstandtech.timetap.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handstandtech.harvest.impl.DailyResponse;
import com.handstandtech.harvest.model.Project;
import com.handstandtech.harvest.model.Task;
import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.R;

public class ProjectListActivity extends AbstractListActivity {

  private String clientName;

  public ProjectListActivity() {
    super();

  }

  /** Called when the activity is first created. */
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_layout);

    Bundle bundle = getIntent().getExtras();
    this.clientName = bundle.getString(Constants.PROP_CLIENT_NAME);
    

    getRedListHeader().setText(clientName);

    DailyResponse items = getTimeTap().getDailyResponse();
    if (items != null) {
      addItems(items);
    } else {
      Toast.makeText(this, "ERROR, NULL!!!", Toast.LENGTH_LONG).show();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.i(TAG, "On Activity Result");
  }


  public void addItems(DailyResponse dailyResponse) {

    final LinearLayout taskVerticalListLinearLayout = getListLinearLayout();

    // TODO Add Clients instead of Project/Task
    List<Project> projects = dailyResponse.getClientToProjectsMap().get(clientName);

    for (final Project project : projects) {
      LinearLayout newItemView = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item,
          taskVerticalListLinearLayout, false);
      TextView projectTextView = (TextView) newItemView.findViewById(R.id.project_text);
      TextView taskTextView = (TextView) newItemView.findViewById(R.id.task_text);
      projectTextView.setText(project.getName());
      
      List<Task> tasks = dailyResponse.getTasksForProject(project.getId());
      int numTasks = tasks.size();
      if (numTasks == 1) {
        // Singular
        taskTextView.setText(tasks.size() + " Task Available");
      } else {
        // Plural
        taskTextView.setText(tasks.size() + " Tasks Available");
      }

      newItemView.setOnClickListener(new OnClickListener() {

        public void onClick(View v) {
          Intent newIntent = new Intent(ProjectListActivity.this, TaskListActivity.class);
          Bundle bundle = new Bundle();
          bundle.putLong(Constants.PROP_PROJECT_ID, project.getId());
          bundle.putString(Constants.PROP_PROJECT_NAME, project.getName());
          newIntent.putExtras(bundle);
          startActivityForResult(newIntent, 0);
        }
      });

      taskVerticalListLinearLayout.addView(newItemView, taskVerticalListLinearLayout.getChildCount());
    }
  }
}