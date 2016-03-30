package org.grameenfoundation.cch.supervisor.model;

import java.io.Serializable;

public class User implements Serializable {

	private String username;
	private String password;
	private String first_name;
	private String last_name;
    private String role;
	private String api_key;
	private String sup_info = "";
	private boolean passwordRight = true;

	public String getSupervisorInfo() { return this.sup_info; }
	public void setSupervisorInfo(String info) { this.sup_info = info; }
	public boolean hasSupervisorInfo() { return (!this.sup_info.equals("")); }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public String getFirstName() { return first_name; }
	public void setFirstName(String first) { this.first_name = first; }

    public String getDisplayName() { return first_name + " " + last_name; }
	public String getLastName() { return last_name; }
	public void setLastName(String last) { this.last_name = last; }

    public String getRole() {
        return (role == null) ? "Supervisor" : role;
    }
    public void setRole(String role) { this.role = role; }

	public String getApi_key() { return api_key; }
	public void setApi_key(String api_key) { this.api_key = api_key; }

	public void setPasswordRight(boolean v) {
		this.passwordRight = v;
	}
	public boolean isPasswordRight() {
		return this.passwordRight;
	}
}
