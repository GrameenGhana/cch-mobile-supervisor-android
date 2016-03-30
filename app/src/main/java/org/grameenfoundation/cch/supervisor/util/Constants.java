package org.grameenfoundation.cch.supervisor.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class Constants {

    public static final int DEFAULT_SERVER_CONNECTION_TIMEOUT = 10000;
    public static final int DEFAULT_SERVER_RESPONSE_TIMEOUT = 20000;

    public static final String CCH_API_USER= "tracker";
    public static final String CCH_API_KEY = "dog";
    public static final String DEFAULT_SERVER = "http://188.226.189.149/cch/";
    public static final String DEFAULT_SERVER_OPPIA = DEFAULT_SERVER + "oppia/";
    public static final String DEFAULT_SERVER_YABR3 = DEFAULT_SERVER + "yabr3/";

    public static final String LOGIN_PATH = DEFAULT_SERVER_OPPIA + "api/v1/user/";
    public static final String CCH_TRACKER_SUBMIT_PATH = DEFAULT_SERVER_YABR3 + "api/v1/tracker";
    public static final String CCH_SUPERVISOR_API = DEFAULT_SERVER_YABR3 + "api/v1/supervisor";
    public static final String USER_AGENT = "CCH Supervisor Android: ";

    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormat.forPattern("HH:mm:ss");

    public static final int INTERVAL_UNITS_HOURS = 0;
    public static final int INTERVAL_UNITS_MINUTES = 1;
    public static final int INTERVAL_UNITS_SECONDS = 2;
}
