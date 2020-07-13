package com.example.shiningstar;

import android.app.Notification;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessagingService extends FirebaseMessagingService {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showNotification(String title, String message){

        Notification builder =new Notification.Builder(this)
                .setSmallIcon(R.drawable.shiningggg)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.shiningggg))
                .setContentText(message)
                .setChannelId("Notify")
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(message))
                .build();

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(999, builder);

    }

}
