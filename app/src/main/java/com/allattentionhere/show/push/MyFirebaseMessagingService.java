package com.allattentionhere.show.push;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.allattentionhere.show.R;
import com.allattentionhere.show.activity.LoginActivity;
import com.allattentionhere.show.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by krupenghetiya on 10/02/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload. sent from API
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload. Sent from console
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        Map<String, String> map = remoteMessage.getData();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Log.d(TAG, entry.getKey() + "/" + entry.getValue());
        }
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager
                .getRunningTasks(Integer.MAX_VALUE);
        boolean isActivityFound = false;

        if (services.get(0).topActivity.getPackageName().toString()
                .equalsIgnoreCase(getPackageName().toString())) {
            isActivityFound = true;
        }
        Log.d("k9_topact", services.get(0).topActivity.getClassName());

        if (!isActivityFound) {
            //app in backgrnd
            //check for params in map and show notification accordingly
            if (map.get("image") == null) {
                //text notify
                sendDefaultNotification(map.get("title") + "", map.get("body") + "", map.get("activity") + "");
            } else {
                //image notify
                getImageBitmapAndSendNotification(map.get("title") + "", map.get("body") + "", map.get("activity") + "",map.get("image"));
            }
        }

    }

    private void getImageBitmapAndSendNotification(String title, String body, String activity, String image) {


//        sendImageNotification(title,body,bitmap,activity);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param body FCM message body received.
     */

    private void sendDefaultNotification(String title, String body, String activity) {
        if (title.isEmpty()) title = getResources().getString(R.string.app_name);
        Intent intent = getIntentForPush(activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "notification");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());

    }

    private void sendImageNotification(String title, String body, Bitmap bitmap, String activity) {
        try {
            Intent intent = getIntentForPush(activity);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "notification");
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(resultPendingIntent);

            NotificationCompat.BigPictureStyle bigPicStyle = new NotificationCompat.BigPictureStyle();
            bigPicStyle.bigPicture(bitmap);
            bigPicStyle.setBigContentTitle(title);
            bigPicStyle.setSummaryText(body);
            mBuilder.setStyle(bigPicStyle);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Math.abs(((int) Math.random()) % 10000) + 1 /* ID of notification */, mBuilder.build());

            Log.d("k9_intentdata", "tried ");

        } catch (Exception ignored) {
            Log.d("k9_intentdata", "e= " + ignored.toString());

        }
    }

    private Intent getIntentForPush(String activity) {
        switch (activity) {
            case "MainActivity":
                return new Intent(this, MainActivity.class);
            default:
                return new Intent(this, LoginActivity.class);
        }
    }
}