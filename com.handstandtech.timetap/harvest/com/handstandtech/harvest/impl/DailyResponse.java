package com.handstandtech.harvest.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.handstandtech.harvest.model.Project;
import com.handstandtech.harvest.model.Task;
import com.handstandtech.harvest.model.TimeEntry;

public class DailyResponse implements Serializable {

  /**
   * Default Serialization UID
   */
  private static final long serialVersionUID = 1L;
  private List<TimeEntry> day_entries;
  private List<Project> projects;

  public DailyResponse() {
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

  public Map<String, List<Project>> getClientToProjectsMap() {
    Map<String, List<Project>> map = new HashMap<String, List<Project>>();
    for (Project project : projects) {
      String client = project.getClient();
      List<Project> currList = map.get(client);
      if (currList == null) {
        currList = new ArrayList<Project>();
      }
      currList.add(project);
      map.put(client, currList);
    }
    return map;
  }

  public List<String> getClientNamesInAlphabeticalOrder() {
    Map<String, List<Project>> map = getClientToProjectsMap();
    List<String> clientNames = new ArrayList<String>(map.keySet());
    Collections.sort(clientNames);
    return clientNames;
  }

  public List<Task> getTasksForProject(Long projectId) {
    Project project = getProjectForId(projectId);
    if (project != null) {
      return project.getTasks();
    }
    return null;
  }

  public Project getProjectForId(Long projectId) {
    for (Project project : projects) {
      if (projectId.longValue() == project.getId().longValue()) {
        return project;
      }
    }
    return null;
  }
}
