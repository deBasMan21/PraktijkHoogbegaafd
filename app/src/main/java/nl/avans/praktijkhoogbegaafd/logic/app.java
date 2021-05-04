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
import androidx.room.Room;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

import nl.avans.praktijkhoogbegaafd.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;
import nl.avans.praktijkhoogbegaafd.dal.FeelingsDB;

public class app extends Application {
    public static final String CHANNEL_1_ID = "channel1";


    @Override
    public void onCreate() {
        super.onCreate();

        Room.databaseBuilder(this, FeelingsDB.class, "feelingsDB");
        InfoEntityManager iem = new InfoEntityManager(this);


        System.out.println("App started");
        createNotificationChannels();
        if(iem.getInfo().isParentalControl()){
            sendNotification(16);
        } else{
            sendNotification(11);
            sendNotification(16);
            sendNotification(20);
        }
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,"Channel 1",NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("This is channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    public void sendNotification(int hour){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long test = calendar.getTimeInMillis();
        long test2 = System.currentTimeMillis();
        Intent intent = new Intent(getApplicationContext(), ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }


}
