package com.handstandtech.timetap.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.handstandtech.harvest.impl.DailyResponse;
import com.handstandtech.harvest.model.TimerResponse;
import com.handstandtech.timetap.Constants;
import com.handstandtech.timetap.Util;
import com.handstandtech.timetap.activity.TimeTapBaseActivity;

public class TimeTapApplication extends Application {

  public static final String TAG = "TimeTap";
  private DailyResponse dailyResponse;

  private Long localStartTime;
  private TimerResponse currTimer;

  public TimeTapApplication() {
    super();
  }

  public DailyResponse getDailyResponse() {
    if (dailyResponse == null) {
      dailyResponse = getDailyResponseFromPrefs();
    }
    return dailyResponse;
  }

  public void setDailyResponse(DailyResponse dailyResponse) {
    setDailyResponseInPrefs(dailyResponse);
    this.dailyResponse = dailyResponse;
  }

  private DailyResponse getDailyResponseFromPrefs() {
    String json = getStringFromPrefs(Constants.PREF_DAILY_RESPONSE, null);
    return Util.getGson().fromJson(json, DailyResponse.class);
  }

  private String getStringFromPrefs(String key, String defaultValue) {
    SharedPreferences preferences = getSharedPreferences(TAG, android.content.Context.MODE_PRIVATE);
    return preferences.getString(key, defaultValue);
  }

  private void setDailyResponseInPrefs(DailyResponse dailyResponse) {
    String json = Util.getGson().toJson(dailyResponse);
    putStringInPrefs(Constants.PREF_DAILY_RESPONSE, json);
  }

  public void setLocalStartTime(Long localStartTime) {
    setLocalStartTimeInPrefs(localStartTime);
    this.localStartTime = localStartTime;
  }

  public TimerResponse getCurrTimer() {
    // TODO Actually get this from Harvest, but this is fine for now.
    if (currTimer == null) {
      currTimer = getCurrTimerFromPrefs();
    }
    return currTimer;
  }

  public Long getLocalStartTime() {
    // TODO Actually get this from Harvest, but this is fine for now.
    if (localStartTime == null) {
      localStartTime = getLocalStartTimeFromPrefs();
    }
    return localStartTime;
  }

  public void setCurrTimer(TimerResponse currTimer) {
    setCurrTimerInPrefs(currTimer);
    this.currTimer = currTimer;
  }

  private void setCurrTimerInPrefs(TimerResponse currTimer) {
    String json = Util.getGson().toJson(currTimer);
    putStringInPrefs(Constants.PREF_CURR_TIMER, json);
  }

  private void setLocalStartTimeInPrefs(Long localStartTime) {
    String timeString = longToString(localStartTime);
    putStringInPrefs(Constants.PREF_LOCAL_START_TIME, timeString);
  }

  /**
   * Stores a String Value in the Preferences
   * 
   * @param key
   * @param str
   */
  private void putStringInPrefs(String key, String str) {
    SharedPreferences preferences = getSharedPreferences(TAG, android.content.Context.MODE_PRIVATE);
    Editor editor = preferences.edit();
    editor.putString(key, str);
    editor.commit();
  }

  private TimerResponse getCurrTimerFromPrefs() {
    String json = getStringFromPrefs(Constants.PREF_CURR_TIMER, null);
    return Util.getGson().fromJson(json, TimerResponse.class);
  }

  private Long getLocalStartTimeFromPrefs() {
    String timeString = getStringFromPrefs(Constants.PREF_LOCAL_START_TIME, null);
    return parseStringToLong(timeString);
  }

  /**
   * Converts a Long to a String hand handles possible errors which might occur.
   * 
   * @param time
   * @return
   */
  private String longToString(Long time) {
    if (time == null) {
      return null;
    } else {
      return time.toString();
    }
  }

  /**
   * Converts String to Long and hancles possible errors which might occur.
   * 
   * @param timeString
   * @return
   */
  private Long parseStringToLong(String timeString) {
    Long time = null;
    try {
      time = Long.parseLong(timeString);
    } catch (Exception e) {
      // Do nothing, time is already null
    }
    return time;
  }

  public boolean isTimerRunning() {
    if (getLocalStartTime() != null) {
      return true;
    } else {
      return false;
    }
  }
  
  public boolean isPreviouslyLoggedIn(TimeTapBaseActivity context) {
    String username = context.getUsernameFromPrefs();
    String password = context.getPasswordFromPrefs();
    String subdomain = context.getSubdomainFromPrefs();
    if (username != null && password != null && subdomain != null) {
      return true;
    }
    return false;
  }
}
