package com.handstandtech.harvest.model;

import java.io.Serializable;
import java.util.Date;

public class TimeEntry implements Serializable {
  /**
   * Default Serialization UID
   */
  private static final long serialVersionUID = 1L;

  /**
   * 1394224,
   */
  private Long project_id;

  /**
   * Weymouth Design - Titleist - FI Portal,
   */
  private String project;
  /**
   * : 697504,
   */
  private Long task_id;
  /**
   * : Development,
   */
  private String task;
  /**
   * Chase Technology Consultants,
   */
  private String client;
  /**
   * 55962808,
   */
  private Long id;
  /**
   * : Worked on charts on the trade reports.,
   */
  private String notes;
  /**
   * : 2011-09-07T21:15:13Z,
   */
  private Date created_at;
  /**
   * 2011-09-07T21:15:13Z,
   */
  private Date updated_at;

  /**
   * : 7.25}
   */
  private Double hours;

  public TimeEntry() {

  }

  public Long getProject_id() {
    return project_id;
  }

  public void setProject_id(Long project_id) {
    this.project_id = project_id;
  }

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public Long getTask_id() {
    return task_id;
  }

  public void setTask_id(Long task_id) {
    this.task_id = task_id;
  }

  public String getTask() {
    return task;
  }

  public void setTask(String task) {
    this.task = task;
  }

  public String getClient() {
    return client;
  }

  public void setClient(String client) {
    this.client = client;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Double getHours() {
    return hours;
  }

  public void setHours(Double hours) {
    this.hours = hours;
  }

  public Date getCreated_at() {
    return created_at;
  }

  public void setCreated_at(Date created_at) {
    this.created_at = created_at;
  }

  public Date getUpdated_at() {
    return updated_at;
  }

  public void setUpdated_at(Date updated_at) {
    this.updated_at = updated_at;
  }

}
