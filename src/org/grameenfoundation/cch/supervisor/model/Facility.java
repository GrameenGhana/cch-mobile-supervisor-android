package org.grameenfoundation.cch.supervisor.model;

import java.util.ArrayList;
import java.util.List;

public class Facility {
	int id;
	String name;
	String facilityType;
	String region;
	String subDistrict;

	List<Nurse> nurses = new ArrayList<Nurse>();
	List<Facility> facilities = new ArrayList<Facility>();
	List<Event> events = new ArrayList<Event>();
	List<Target> targets = new ArrayList<Target>();
	List<Course> courses = new ArrayList<Course>();
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
	public String getFacilityType() {
		return facilityType;
	}
	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getSubDistrict() {
		return subDistrict;
	}
	public void setSubDistrict(String subDistrict) {
		this.subDistrict = subDistrict;
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
	
	public Facility(int id, String name, String facilityType, String region,
			String subDistrict, List<Nurse> nurses, List<Facility> facilities,
			List<Event> events, List<Target> targets, List<Course> courses) {
		super();
		this.id = id;
		this.name = name;
		this.facilityType = facilityType;
		this.region = region;
		this.subDistrict = subDistrict;
		this.nurses = nurses;
		this.facilities = facilities;
		this.events = events;
		this.targets = targets;
		this.courses = courses;
	}
	public Facility(int id, String name, String facilityType, String region,
			String subDistrict) {
		super();
		this.id = id;
		this.name = name;
		this.facilityType = facilityType;
		this.region = region;
		this.subDistrict = subDistrict;
	}

}
