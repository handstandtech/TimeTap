package com.handstandtech.harvest.model;

public class User {

	private Boolean admin;
	private Boolean timestamp_timers;
	private Long id;
	private String timezone;
	private String email;

	public User() {

	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public Boolean getTimestamp_timers() {
		return timestamp_timers;
	}

	public void setTimestamp_timers(Boolean timestamp_timers) {
		this.timestamp_timers = timestamp_timers;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
