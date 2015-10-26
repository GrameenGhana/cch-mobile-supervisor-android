package org.grameenfoundation.cch.supervisor.model;

import java.util.ArrayList;
import java.util.List;

public class Nurse {

	int nurseId;
	String username;
	String surname;
	String othernames;
	String gender;
	String role;
	String userGroup;
	String status;
	String myFacility;
	
	
	List<Event> events = new ArrayList<Event>();
	List<Target> targets = new ArrayList<Target>();
	List<Course> courses = new ArrayList<Course>();
	
	
	public int getNurseId() {
		return nurseId;
	}
	public void setNurseId(int nurseId) {
		this.nurseId = nurseId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getOthernames() {
		return othernames;
	}
	public void setOthernames(String othernames) {
		this.othernames = othernames;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUserGroup() {
		return userGroup;
	}
	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMyFacility() {
		return myFacility;
	}
	public void setMyFacility(String myFacility) {
		this.myFacility = myFacility;
	}
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	public List<Target> getTargets() {
		return targets;
	}
	public void setTargets(List<Target> targets) {
		this.targets = targets;
	}
	public List<Course> getCourses() {
		return courses;
	}
	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}
	public Nurse(int nurseId, String username, String surname,
			String othernames, String gender, String role, String userGroup,
			String status, String myFacility, List<Event> events,
			List<Target> targets, List<Course> courses) {
		super();
		this.nurseId = nurseId;
		this.username = username;
		this.surname = surname;
		this.othernames = othernames;
		this.gender = gender;
		this.role = role;
		this.userGroup = userGroup;
		this.status = status;
		this.myFacility = myFacility;
		this.events = events;
		this.targets = targets;
		this.courses = courses;
	}
	
	public Nurse(int nurseId, String username, String surname,
			String othernames, String gender, String role, String userGroup,
			String status,String myfacility) {
		super();
		this.nurseId = nurseId;
		this.username = username;
		this.surname = surname;
		this.othernames = othernames;
		this.gender = gender;
		this.role = role;
		this.userGroup = userGroup;
		this.status = status;
		this.myFacility = myfacility;
	}
	
	
	String getName(){
		
		return othernames+" "+surname;
	}
	
}
