package com.example.codebox.gisbms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by codebox on 26/8/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i("Notifi : ",remoteMessage.getData().get("title"));
        UserInfo.getInstance(getApplicationContext()).saveEmergencyLocation(
                remoteMessage.getData().get("body"),
                remoteMessage.getData().get("lat"),
                remoteMessage.getData().get("lng"));


        String textContent = remoteMessage.getData().get("body") + " (" + remoteMessage.getData().get("lat") +","+ remoteMessage.getData().get("lng")+")";

//        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this,EmergencyMap.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(remoteMessage.getData().get("title"));
        notificationBuilder.setContentText(textContent);
        notificationBuilder.setColor(getColor(R.color.emergencyNotification));
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(Uri.parse("android.resource://com.example.codebox.gisbms/"+R.raw.warning));
        notificationBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 });
        notificationBuilder.setSmallIcon(R.drawable.siren);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());

    }
}
