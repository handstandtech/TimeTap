package com.handstandtech.harvest.model;

import java.io.Serializable;
import java.util.List;

public class Project implements Serializable {
  /**
   * Default Serialization UID
   */
  private static final long serialVersionUID = 1L;

  private String name;
  private Boolean show_budget_to_all;

  private Boolean billable;
  private Boolean active;

  private String notes;

  private Long id;
  private Long client_id;
  /** Optional total budget in hours */
  private Double budget;
  private List<Task> tasks;

  // Dates

  /** ---------- */
  // private Date earliest_record_at;
  // private Date created_at;
  // private Date over_budget_notified_at;
  // private Date updated_at;

  // hint-earliest-record-at2010-12-02,
  // budget_bynone,
  // basecamp_idnull,
  // active_task_assignments_count4,
  /** Optional Project Code */
  // code,
  // notify_when_over_budgetfalse,
  // cost_budgetnull,
  // latest_record_at2011-06-13,
  // highrise_deal_idnull,
  // feesnull,
  // cost_budget_include_expensesfalse,
  // hourly_ratenull,
  // estimate_bynone,
  // bill_bynone,
  // hint-latest-record-at2011-06-13,
  // cache_version2300774614,
  // active_user_assignments_count1,
  // over_budget_notification_percentage80.0,

  // estimatenull

  public Project() {

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Boolean getShow_budget_to_all() {
    return show_budget_to_all;
  }

  public void setShow_budget_to_all(Boolean show_budget_to_all) {
    this.show_budget_to_all = show_budget_to_all;
  }

  public Boolean getBillable() {
    return billable;
  }

  public void setBillable(Boolean billable) {
    this.billable = billable;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getClient_id() {
    return client_id;
  }

  public void setClient_id(Long client_id) {
    this.client_id = client_id;
  }

  public Double getBudget() {
    return budget;
  }

  public void setBudget(Double budget) {
    this.budget = budget;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public List<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
  }
}
