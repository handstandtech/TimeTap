package com.handstandtech.timetap.activity;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handstandtech.timetap.R;

public class AbstractListActivity extends TimeTapBaseActivity {

  protected LinearLayout getListLinearLayout() {
    return (LinearLayout) findViewById(R.id.task_list);
  }

  protected LinearLayout getLoadingPanel() {
    return (LinearLayout) findViewById(R.id.loading_panel);
  }

  protected ScrollView getContentScrollPanel() {
    return (ScrollView) findViewById(R.id.home_content);
  }

  
  public TextView getRedListHeader() {
    return (TextView) findViewById(R.id.red_list_sub_header);
  }
}