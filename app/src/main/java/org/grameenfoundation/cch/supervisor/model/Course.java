package org.grameenfoundation.cch.supervisor.model;

import java.io.Serializable;

public class Course implements Serializable
{
	public long id;
	public long courseId;
	public String nurseId;
	public String title;
	public String ksa;
	public int quiz_status;
	public int score;
	public int attempts;
	public long percentageCompletion;
	public String timeTaken;
	public String last_accessed;
	public int facilityId;
	public int districtId;
	public String region;
	public String district;
	public String facility;

	public String statsText() {
        return "Final: "+ ((quiz_status==1 && !isInprogress()) ? score+"%" : "not taken");
	}

    public String lastAccessed() {
        return "Attempts: " + attempts +
               "    % Complete: " + percentageCompletion + "%" +
               "    Time Spent: " + timeTaken +
               "    Last accessed: "+last_accessed;
    }

    public String getCategory() {
        if (isPassed()) { return "Passed";
        } else if (isEligible()) { return "Eligible";
        } else if (isInprogress()) { return "In Progress";
        } else { return "Unknown Status"; }
    }

    public int getCategoryId() {
        if (isPassed()) { return 1;
        } else if (isEligible()) { return 2;
        } else if (isInprogress()) { return 3;
        } else { return 4; }
    }

    public boolean isInprogress() { return ksa.equalsIgnoreCase("in progress"); }
    public boolean isEligible() { return ksa.equalsIgnoreCase("eligible"); }
    public boolean isPassed() { return ksa.equalsIgnoreCase("passed"); }

    public void setKSAPassed() { this.ksa="Passed"; }
    public void setKSAEligible() { this.ksa="Eligible"; }
}



