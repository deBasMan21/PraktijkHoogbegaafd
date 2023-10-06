package nl.avans.praktijkhoogbegaafd.logic;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import nl.avans.praktijkhoogbegaafd.R;

public class NotificationUtils extends ContextWrapper
{
    public static String CHANNEL_ID = "Reminder channel";
    public static String TIMELINE_CHANNEL_NAME = "Timeline notification";

    private NotificationManager _notificationManager;
    private Context _context;

    public NotificationUtils(Context base)
    {
        super(base);
        _context = base;
        createChannel();
    }

    public NotificationCompat.Builder setNotification(String title, String body)
    {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_phr_stars_icon_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private void createChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, TIMELINE_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager()
    {
        if(_notificationManager == null)
        {
            _notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return _notificationManager;
    }

    public void setReminders() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            setRepeatingReminder(10, 0);
            setRepeatingReminder(16, 0);
            setRepeatingReminder(20, 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void setRepeatingReminder(int hour, int minute)
    {
        Intent _intent = new Intent(_context, ReminderBroadcast.class);
        PendingIntent _pendingIntent = PendingIntent.getBroadcast(_context, 1, _intent, PendingIntent.FLAG_MUTABLE);

        AlarmManager _alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar cal = Calendar.getInstance();
        //notification to start tomorrow
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        long time = cal.getTimeInMillis();

        long interval = 86400000;

        _alarmManager.setRepeating(AlarmManager.RTC, time, interval, _pendingIntent);
    }

    public void cancelReminders() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent updateServiceIntent = new Intent(this, ReminderBroadcast.class);

        for(int i = 0; i < 24; i++) {
            PendingIntent pendingUpdateIntent = PendingIntent.getService(this, i, updateServiceIntent, PendingIntent.FLAG_MUTABLE);

            try {
                alarmManager.cancel(pendingUpdateIntent);
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }
}