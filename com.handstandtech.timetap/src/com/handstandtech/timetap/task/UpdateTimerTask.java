package com.handstandtech.timetap.task;

import com.handstandtech.harvest.model.TimerResponse;
import com.handstandtech.timetap.Util;
import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public class UpdateTimerTask extends AbstractAsyncTask<Object, Integer, TimerResponse> {
  private Long timer_id;
  private Long project_id;
  private Long task_id;
  private Double elapsedHours;
  private String notes;

  public UpdateTimerTask(TimeTapBaseActivity context, Long timer_id, Long project_id, Long task_id,
      Double elapsedHoursDecimal, String notes, AsyncTaskCallback<TimerResponse> callback) {
    super(context, callback);
    this.timer_id = timer_id;
    this.project_id = project_id;
    this.task_id = task_id;
    this.elapsedHours = elapsedHoursDecimal;
    this.notes = notes;
  }

  @Override
  protected TimerResponse doInBackground(Object... is) {
    return Util.updateTimer(context, timer_id, project_id, task_id, elapsedHours, notes);
  }
}