package org.grameenfoundation.cch.supervisor.model;

public class User {

	private String username;
	private String email;
	private String password;
	private String passwordAgain;
	private String firstname;
	private String lastname;
	private String api_key;
	private String sup_info = "";
	private boolean scoringEnabled = true;
	private int points = 0;
	private int badges = 0;
	private boolean passwordRight = true;
	
	
	public boolean hasSupervisorInfo()
	{
		return (this.sup_info.equals("")) ? false : true;
	}
	
	
	public String getSupervisorInfo() {
		return this.sup_info;
	}
	public void setSupervisorInfo(String info) {
		this.sup_info = info;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
	public String getPasswordAgain() {
		return passwordAgain;
	}
	public void setPasswordAgain(String passwordAgain) {
		this.passwordAgain = passwordAgain;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getApi_key() {
		return api_key;
	}
	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}
	public String getDisplayName() {
		return firstname + " " + lastname;
	}

	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public int getBadges() {
		return badges;
	}
	public void setBadges(int badges) {
		this.badges = badges;
	}
	public boolean isScoringEnabled() {
		return scoringEnabled;
	}
	public void setScoringEnabled(boolean scoringEnabled) {
		this.scoringEnabled = scoringEnabled;
	}
	
	public void setPasswordRight(boolean v) {
		this.passwordRight = v;
	}
	public boolean isPasswordRight() {
		return this.passwordRight;
	}
}
