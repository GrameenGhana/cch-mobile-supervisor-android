package org.grameenfoundation.cch.supervisor.model;

import android.text.format.DateFormat;
import android.util.Log;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Event implements Serializable{

	public int id;
	public long eventId;
	public String nurseId;
	public String title;
	public String location;
	public String type;
	public String justification;
	public String comments;
	public String status;
	public Long start;
	public Long end;
	public int facilityId;
	public int districtId;
	public String region;
	public String district;
	public String facility;
    public String nurse;


	public String scheduleText(boolean showDay) {
		return  "Scheduled by " + nurse + " on " + longToDate(this.start, showDay);
	}

    public String getCategory() {
        if (isFuture()) { return "Future";
        } else if (isTomorrow()) { return "Tomorrow";
        } else if (isToday()) { return "Today";
        } else if (isYesterday()) { return "Yesterday";
        } else if (isPastThisMonth()) { return "Past This Month";
        } else if (isPastLastMonth()) { return "Past Last Month";
		} else if (isPast()) { return "In the Past";
        } else { return "Unknown Period"; }
    }

    public int getCategoryId() {
        if (isFuture()) { return 6;
        } else if (isTomorrow()) { return 5;
        } else if (isToday()) { return 4;
        } else if (isYesterday()) { return 3;
        } else if (isPastThisMonth()) { return 2;
        } else if (isPastLastMonth()) { return 1;
		} else if (isPast()) { return 7;
        } else { return 8; }
    }

	public boolean isComplete() { return status.equalsIgnoreCase("complete"); }
	public boolean isInComplete() { return status.equalsIgnoreCase("incomplete"); }
	public boolean isPending() { return (!isComplete() && !isInComplete()); }

	public boolean isToday() {
		long milliSeconds = this.start;
		String today = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(new Date(System.currentTimeMillis()));
		return (DateFormat.format("MM/dd/yyyy", new Date(milliSeconds)).toString().equals(today));
	}

	public boolean isTomorrow() {
		long milliSeconds = this.start;
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		String tomorrow = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(new Date(c.getTimeInMillis()));
		return (DateFormat.format("MM/dd/yyyy", new Date(milliSeconds)).toString().equals(tomorrow));
	}

	public boolean isFuture() {
		long milliSeconds = this.start;
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.add(Calendar.DATE, 2);
		return (milliSeconds >= c.getTimeInMillis());
	}
	
	public boolean isYesterday() {
		LocalDate previous = new LocalDate(this.start);
		LocalDate now = new LocalDate().minusDays(1);

		return  (now.getDayOfMonth() == previous.getDayOfMonth())
				&& (now.getMonthOfYear() == previous.getMonthOfYear()
				&& (now.getYear() == previous.getYear()));
			
	}
	
	public boolean isPastThisMonth() {
		LocalDate previous = new LocalDate(this.start);
		LocalDate now = new LocalDate().minusDays(2);

		return  (previous.getDayOfMonth() <= now.getDayOfMonth())
				&& (previous.getMonthOfYear() == now.getMonthOfYear())
				&& (previous.getYear() == now.getYear());
	
	}
	
	public boolean isPastLastMonth() {
		boolean result = false;

		LocalDate previous = new LocalDate(this.start);
		LocalDate lastMonth = new LocalDate().minusMonths(1);
				
		if((previous.getMonthOfYear()==lastMonth.getMonthOfYear()) && (previous.getYear()==lastMonth.getYear())){
			result = true;
		}
	
		return result;
	}

	public boolean isPast() {
		long milliSeconds = this.start;
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		return (milliSeconds < c.getTimeInMillis());
	}

	private String longToDate(long date, boolean showDay) {
		try {
            String dFormat = (showDay) ? "MMM dd, yyyy" : "yyyy-MM-dd HH:mm:ss";
			SimpleDateFormat toDate = new SimpleDateFormat(dFormat, Locale.US);
			return toDate.format(new Date(date));
		} catch (Exception e) {
			Log.d("Events Model", "Exception longToDate : " + e.getLocalizedMessage());
		}

		return "";
	}
}
