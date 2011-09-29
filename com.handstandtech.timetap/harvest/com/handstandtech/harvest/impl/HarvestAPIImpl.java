package com.handstandtech.harvest.impl;

public class HarvestAPIImpl {

	private String domain;
	private String email;
	private String password;

	@SuppressWarnings("unused")
	private HarvestAPIImpl() {

	}

	public HarvestAPIImpl(String domain, String email, String password) {
		this.domain = domain;
		this.email = email;
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
