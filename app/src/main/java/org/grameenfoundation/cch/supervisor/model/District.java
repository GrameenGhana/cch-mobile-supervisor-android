package org.grameenfoundation.cch.supervisor.model;

import java.io.Serializable;
import java.util.ArrayList;

public class District implements Serializable {

	public int id;
    public int districtId;
    public int regionId;
	public String name;
    public String region;
    public ArrayList<Facility> facilities;

    /* Counts variables */
    public int cntFacilities;
    public int cntSupervisors;
    public int cntNurses;
    public int cntEvents;
    public int cntTargets;

    public String statsText() {
        return cntFacilities + " Facilities    " +  cntNurses + " Nurses    " + cntSupervisors + " Supervisors";
    }

    public String facilityStatsText() {
        return cntFacilities + " Facilities    ";
    }

    public String staffStatsText() {
        return  cntNurses + " Nurses    " + cntSupervisors + " Supervisors    ";
    }
}
