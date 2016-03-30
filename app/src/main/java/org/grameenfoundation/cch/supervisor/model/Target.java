package org.grameenfoundation.cch.supervisor.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Target implements Serializable {
	public int id;
	public int targetId;
	public String nurseId;
	public int target;
	public String type;
	public String category;
	public int achieved;
	public String justification;
	public String dueDate;
	public String startDate;
	public long time;
	public String status;
	public int facilityId;
	public int districtId;
	public String region;
	public String district;
	public String facility;

	public String getStartDate() {
		return formatDate(startDate);
	}

    public boolean completed() { return (achieved >= target); }

	public long getTime() {
		java.util.Date date = new java.util.Date();

		try {
			date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(startDate);
		} catch (Exception e) {
			return 0l;
		}

		return date.getTime();
	}

	public String formatDate(String strDate) {
		String format = "MMM dd, yyy ";
		java.util.Date date = new java.util.Date();

		try {
			date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(strDate);
		} catch (Exception e) {
			return "No-Date";
		}

		long milliSeconds = date.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public String getDueDate() {
		return formatDate(dueDate);
	}
}
