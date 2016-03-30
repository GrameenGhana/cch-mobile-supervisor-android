package org.grameenfoundation.cch.supervisor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Nurse implements Serializable {
	public int id;
	public String nurseId;
	public String name;
	public String title;
	public String role;
	public int ischn;
	public String group;
	public String status;
	public String gender;
	public String phoneNumber;
	public String primaryFacility;
	public int facilityId;
	public int districtId;
	public String region;
	public String district;
	public String facility;

	public List<Course> courses = new ArrayList<>();
	public List<Event> events = new ArrayList<>();
	public List<Target> targets = new ArrayList<>();

    public int coursesPassed = 0;
    public int coursesEligible = 0;
    public int coursesInProgress = 0;

	public boolean isSupervisor() {
		return !this.role.toLowerCase().contains("nurse");
	}

    public boolean hasPassedCourses() {
        return (coursesPassed > 0);
    }

    public boolean hasEligibleCourses() {
        return (coursesEligible > 0);
    }

    public boolean hasInProgressCourses() {
        return (coursesInProgress > 0);
    }

	public String KSAText() {
		return coursesInProgress+" In Progress    "+coursesEligible+ " Eligible    "+ coursesPassed +" Passed";
	}

    public String statsText() {
        return courses.size()+" Courses    "+events.size()+ " Events";
    }
}
