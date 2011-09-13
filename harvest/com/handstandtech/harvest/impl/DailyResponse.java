package com.handstandtech.harvest.impl;

import java.io.Serializable;
import java.util.List;

import com.handstandtech.harvest.model.Project;
import com.handstandtech.harvest.model.TimeEntry;

public class DailyResponse implements Serializable {

  /**
   * Default Serialization UID
   */
  private static final long serialVersionUID = 1L;
  private List<TimeEntry> day_entries;
  private List<Project> projects;

  // TODO Custom Date Deserializer/Serializer
  // private Date for_date;

  public DailyResponse() {
    // TODO Auto-generated constructor stub
  }

  public List<TimeEntry> getDay_entries() {
    return day_entries;
  }

  public void setDay_entries(List<TimeEntry> day_entries) {
    this.day_entries = day_entries;
  }

  public List<Project> getProjects() {
    return projects;
  }

  public void setProjects(List<Project> projects) {
    this.projects = projects;
  }

}
