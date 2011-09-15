package com.handstandtech.harvest.model;

import java.io.Serializable;
import java.util.List;

public class Project implements Serializable {
  /**
   * Default Serialization UID
   */
  private static final long serialVersionUID = 1L;

  private Long id;
  private String client;
  private String name;
  private List<Task> tasks;

  public Project() {

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
  }

  public String getClient() {
    return client;
  }

  public void setClient(String client) {
    this.client = client;
  }
}
