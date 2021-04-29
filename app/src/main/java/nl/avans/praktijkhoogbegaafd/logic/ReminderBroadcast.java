package nl.avans.praktijkhoogbegaafd.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import nl.avans.praktijkhoogbegaafd.R;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, app.CHANNEL_1_ID).setSmallIcon(R.mipmap.ic_phr_stars_icon).setContentText("Vul je intensiteiten in!").setContentTitle("Praktijk Hoogbegaafd").setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());
    }


}
