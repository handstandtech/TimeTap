package com.handstandtech.harvest.model;

import java.io.Serializable;

public class Task implements Serializable {

  /**
   * Default Serialization UID
   */
  private static final long serialVersionUID = 1L;

  private Boolean billible;
  private Long id;
  private String name;

  public Task() {

  }

  public Boolean getBillible() {
    return billible;
  }

  public void setBillible(Boolean billible) {
    this.billible = billible;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
