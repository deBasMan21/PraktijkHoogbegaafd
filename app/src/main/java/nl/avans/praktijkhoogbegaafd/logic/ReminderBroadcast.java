package nl.avans.praktijkhoogbegaafd.logic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import nl.avans.praktijkhoogbegaafd.MainActivity;
import nl.avans.praktijkhoogbegaafd.R;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, MainActivity.class);

        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, app.CHANNEL_1_ID).setAutoCancel(true).setSmallIcon(R.mipmap.ic_phr_stars_icon).setContentText("Vul je intensiteiten in!").setContentTitle("Praktijk Hoogbegaafd").setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent);

        notificationManager.notify(100, builder.build());
    }


}
