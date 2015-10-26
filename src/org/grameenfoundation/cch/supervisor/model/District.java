package org.grameenfoundation.cch.supervisor.model;

import java.util.ArrayList;
import java.util.List;

public class District {

	int id;
	String name;
public District() {
	// TODO Auto-generated constructor stub
}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Nurse> getNurses() {
		return nurses;
	}

	public void setNurses(List<Nurse> nurses) {
		this.nurses = nurses;
	}

	public List<Facility> getFacilities() {
		return facilities;
	}

	public void setFacilities(List<Facility> facilities) {
		this.facilities = facilities;
	}

	List<Nurse> nurses = new ArrayList<Nurse>();
	List<Facility> facilities = new ArrayList<Facility>();
	private List<Event> events = new ArrayList<Event>();
	List<Target> targets = new ArrayList<Target>();
	public List<Target> getTargets() {
		return targets;
	}
	public void setTargets(List<Target> targets) {
		this.targets = targets;
	}

	private List<Course> courses = new ArrayList<Course>();

	public District(int id, String name, List<Nurse> nurses,
			List<Facility> facilities) {
		super();
		this.id = id;
		this.name = name;
		this.nurses = nurses;
		this.facilities = facilities;
	}

	public District(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	List<Course> getCourses() {
		return courses;
	}
	void setCourses(List<Course> courses) {
		this.courses = courses;
	}
	List<Event> getEvents() {
		return events;
	}
	void setEvents(List<Event> events) {
		this.events = events;
	}

}
