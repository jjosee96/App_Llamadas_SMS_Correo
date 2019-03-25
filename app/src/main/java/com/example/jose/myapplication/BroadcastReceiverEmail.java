package com.example.jose.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

public class BroadcastReceiverEmail extends BroadcastReceiver {

    Context cntxt;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Email Received", Toast.LENGTH_LONG).show();
        showNotification(context);
    }

    private void showNotification(Context context) {


        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
               // .setSmallIcon(R.drawable.YOUR_APP_icon)
                //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.YOUR_APP_icon))
                .setTicker(res.getString(R.string.Proyecto))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(res.getString(R.string.Proyecto))
                .setContentText(res.getString(R.string.Proyecto));
        Notification n = builder.getNotification();
        nm.notify(1, n);
    }


}
