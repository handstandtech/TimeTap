package com.handstandtech.harvest.model;

import java.io.Serializable;
import java.util.Date;

public class TimerResponse implements Serializable {

  /**
   * Default Serialization UID
   */
  private static final long serialVersionUID = 1L;

  private Date timer_started_at;
  private Long project_id;
  private String project;
  private Long task_id;
  private String task;
  private String client;
  private Long id;
  private String notes;
  /**
   * Display String for the Time Started
   */
  private String started_at;
  /**
   * Display String for the Time Ended
   */
  private String ended_at;
  private Date created_at;
  private Date updated_at;
  private Double hours;

  public TimerResponse() {

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

  public Date getTimer_started_at() {
    return timer_started_at;
  }

  public void setTimer_started_at(Date timer_started_at) {
    this.timer_started_at = timer_started_at;
  }

  public String getStarted_at() {
    return started_at;
  }

  public void setStarted_at(String started_at) {
    this.started_at = started_at;
  }

  public String getEnded_at() {
    return ended_at;
  }

  public void setEnded_at(String ended_at) {
    this.ended_at = ended_at;
  }

}
