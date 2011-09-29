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
import com.handstandtech.harvest.model.Task;
import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.R;

public class TaskListActivity extends AbstractListActivity {

  private Long projectId;

  private String projectName;

  public TaskListActivity() {
    super();

  }

  /** Called when the activity is first created. */
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_layout);

    Bundle bundle = getIntent().getExtras();
    this.projectId = bundle.getLong(Constants.PROP_PROJECT_ID);
    this.projectName = bundle.getString(Constants.PROP_PROJECT_NAME);

    getRedListHeader().setText(projectName);

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
    List<Task> tasks = dailyResponse.getTasksForProject(projectId);

    for (final Task task : tasks) {
      LinearLayout newItemView = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item,
          taskVerticalListLinearLayout, false);
      TextView projectTextView = (TextView) newItemView.findViewById(R.id.project_text);
      TextView taskTextView = (TextView) newItemView.findViewById(R.id.task_text);
      projectTextView.setText(task.getName());
      taskTextView.setText("Click to Start Timer");

      newItemView.setOnClickListener(new OnClickListener() {

        public void onClick(View v) {
          Intent newIntent = new Intent(TaskListActivity.this, TaskScreenActivity.class);
          Bundle bundle = new Bundle();
          bundle.putLong(Constants.PROP_PROJECT_ID, projectId);
          bundle.putLong(Constants.PROP_TASK_ID, task.getId());
          newIntent.putExtras(bundle);
          startActivityForResult(newIntent, 0);
        }
      });

      taskVerticalListLinearLayout.addView(newItemView, taskVerticalListLinearLayout.getChildCount());
    }
  }
}