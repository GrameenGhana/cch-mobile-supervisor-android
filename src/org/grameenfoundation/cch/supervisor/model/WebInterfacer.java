package org.grameenfoundation.cch.supervisor.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.grameenfoundation.cch.supervisor.R;
import org.grameenfoundation.cch.supervisor.activity.MainActivity;
import org.grameenfoundation.cch.supervisor.application.CCHSupervisor;
import org.grameenfoundation.cch.supervisor.application.DbHelper;
import org.grameenfoundation.cch.supervisor.listener.SubmitListener;
import org.grameenfoundation.cch.supervisor.model.User;
import org.grameenfoundation.cch.supervisor.tasks.UpdateSupervisorInfoTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.format.DateFormat;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebInterfacer  {


}
