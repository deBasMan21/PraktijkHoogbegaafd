package nl.avans.praktijkhoogbegaafd.logic;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

import nl.avans.praktijkhoogbegaafd.R;

public class app extends Application {
    public static final String CHANNEL_1_ID = "channel1";


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("App started");
        createNotificationChannels();
        sendNotification();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,"Channel 1",NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("This is channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    public void sendNotification(){
        Intent intent = new Intent(app.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long time2 = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);


        long time = calendar.getTimeInMillis();
        long solidTime = 1619706900;

        alarmManager.set(AlarmManager.RTC_WAKEUP, calculateMsToEleven() * 10000, pendingIntent);


    }

    public long calculateMsToEleven(){
        long time = System.currentTimeMillis();
        int minutesTillHour = 60 - LocalTime.now().getMinute();
        int hoursTillEleven = 11 - (LocalTime.now().getHour() + 2);
        if(hoursTillEleven < 0){
            hoursTillEleven = (hoursTillEleven * -1) + 12;
        }
        int minutestillEleven = minutesTillHour + (hoursTillEleven * 60);
        int secondsTillEleven = minutestillEleven * 60;
        time = System.currentTimeMillis() + (secondsTillEleven * 1000);
        return time;
    }

}
