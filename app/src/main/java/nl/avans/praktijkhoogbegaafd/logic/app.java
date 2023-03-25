package nl.avans.praktijkhoogbegaafd.logic;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.room.Room;

import java.util.Calendar;

import nl.avans.praktijkhoogbegaafd.dal.FeelingsDB;

public class app extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Room.databaseBuilder(this, FeelingsDB.class, "feelingsDB");
        InfoEntityManager iem = new InfoEntityManager(this);
    }
}
