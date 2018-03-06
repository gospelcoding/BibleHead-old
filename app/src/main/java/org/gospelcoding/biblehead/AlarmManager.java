package org.gospelcoding.biblehead;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

import static org.gospelcoding.biblehead.SettingsActivity.NOTIFICATIONS;
import static org.gospelcoding.biblehead.VerseListActivity.LEARNED_A_VERSE;
import static org.gospelcoding.biblehead.VerseListActivity.SHARED_PREFS_TAG;


public class AlarmManager {

    public static void setAlarmIfNecessary(Context context){
        SharedPreferences values = context.getSharedPreferences(SHARED_PREFS_TAG, 0);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if(values.getBoolean(LEARNED_A_VERSE, false) && settings.getBoolean(NOTIFICATIONS, true))
            setAlarm(context);
    }

    public static void setAlarm(Context context){
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar alarmCal = nextCalendarAtTime(6, 30);
        alarmManager.setInexactRepeating(android.app.AlarmManager.RTC_WAKEUP,
                alarmCal.getTimeInMillis(),
                android.app.AlarmManager.INTERVAL_DAY,
                alarmIntent(context));
        Log.d("BH Alarm", "Set Alarm for " + alarmCal.getTime().toString());
    }

    private static PendingIntent alarmIntent(Context context){
        Intent intent = new Intent(context, ReviewNotifier.class);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private static Calendar nextCalendarAtTime(int hour, int minute){
        Calendar now = Calendar.getInstance();
        Calendar rVal = Calendar.getInstance();
        rVal.set(Calendar.HOUR_OF_DAY, hour);
        rVal.set(Calendar.MINUTE, minute);
        if(now.getTimeInMillis() > rVal.getTimeInMillis())
            rVal.add(Calendar.DAY_OF_MONTH, 1);
        return rVal;
    }

    public static void cancelAlarm(Context context){
        android.app.AlarmManager alarmManager = (android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent(context));
        Log.d("BH Alarm", "Cancelled alarm");
    }
}
