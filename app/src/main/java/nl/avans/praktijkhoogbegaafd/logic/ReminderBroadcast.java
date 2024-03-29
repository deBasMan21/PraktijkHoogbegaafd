package nl.avans.praktijkhoogbegaafd.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class ReminderBroadcast extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        NotificationUtils _notificationUtils = new NotificationUtils(context);
        NotificationCompat.Builder _builder = _notificationUtils.setNotification("Praktijk Hoogbegaafd", "Herinnering om je intensiteiten in te vullen");
        _notificationUtils.getManager().notify(101, _builder.build());
    }
}