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

import androidx.core.app.NotificationCompat;

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

    public void setReminder(long timeInMillis)
    {
        Intent _intent = new Intent(_context, ReminderBroadcast.class);
        PendingIntent _pendingIntent = PendingIntent.getBroadcast(_context, 0, _intent, 0);

        AlarmManager _alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        _alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, _pendingIntent);
    }

}