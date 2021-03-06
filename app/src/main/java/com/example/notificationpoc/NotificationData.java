/*package com.example.notificationpoc;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;


public class NotificationData {
    public enum CustomActionType {
        SNOOZE,
        DISMISS
    }

    public enum CustomAudioType {
        ALARM,
        NOTIFICATION,
        RINGTONE
    }

    public enum MessageSize {
        REGULAR,
        LARGE
    }

    private final String message;
    private final String title;
    private final Integer badgeCount;
    private final CustomActionType customAction;
    private final CustomAudioType customAudio;
    private final boolean includePicture;
    private final MessageSize messageSize;
    private final Integer timeoutMs;

    public NotificationData(RemoteMessage remoteMessage) {
        Map<String, String> remoteMessageData = remoteMessage.getData();
        this.message = remoteMessageData.get("message");

        this.title = remoteMessageData.getOrDefault("title", "Default notification title");
        this.badgeCount = remoteMessageData.containsKey("badgeCount") ?
                Integer.parseInt(remoteMessageData.get("badgeCount")) : null;
        this.customAction = remoteMessageData.containsKey("customAction") ?
                parseCustomAction(remoteMessageData.get("customAction")) : null;
        this.customAudio = remoteMessageData.containsKey("customAudio") ?
                parseCustomAudio(remoteMessageData.get("customAudio")) : null;
        this.timeoutMs = remoteMessageData.containsKey("timeoutMs") ?
                Integer.parseInt(remoteMessageData.get("timeoutMs")) : null;
        this.messageSize = remoteMessageData.containsKey("messageSize") ?
                parseMessageSize(remoteMessageData.get("messageSize")): null;
        this.includePicture = Boolean.parseBoolean(remoteMessageData.getOrDefault("includePicture", "false"));
    }

    private CustomActionType parseCustomAction(String customActionString) {
        switch (customActionString) {
            case "snooze":
                return CustomActionType.SNOOZE;

            case "dismiss":
                return CustomActionType.DISMISS;

            default:
                return null;
        }
    }

    private CustomAudioType parseCustomAudio(String customAudioString) {
        switch (customAudioString) {
            case "alarm":
                return CustomAudioType.ALARM;

            case "notification":
                return CustomAudioType.NOTIFICATION;

            case "ringtone":
                return CustomAudioType.RINGTONE;

            default:
                return null;
        }
    }

    private MessageSize parseMessageSize(String messageSize){
        switch (messageSize) {
            case "large":
                return MessageSize.LARGE;

            case "regular":
                return MessageSize.REGULAR;

            default:
                return null;
        }
    }

    public Notification createNotification(Context context) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context,
                helper.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(this.title)
                .setContentText(this.message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);

        if (this.customAction != null) {
            Intent customActionIntent = new Intent(context, NotificationButtonsHandler.class);
            PendingIntent customActionPendingIntent =
                    PendingIntent.getBroadcast(context, 0, customActionIntent, 0);

            String title = customAction == CustomActionType.DISMISS ? "Dismiss" : "Snooze";
            notificationBuilder.addAction(
                    android.R.drawable.ic_popup_reminder,
                    title,
                    customActionPendingIntent);
        }

        if (this.badgeCount != null) {
            notificationBuilder.setNumber(this.badgeCount);
        }

        if (this.timeoutMs != null) {
            notificationBuilder.setTimeoutAfter(this.timeoutMs);
        }

        if (this.customAudio != null) {
            Uri soundUri = null;

            switch (this.customAudio) {
                case ALARM:
                    soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    break;

                case NOTIFICATION:
                    soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    break;

                case RINGTONE:
                    soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    break;
            }

            notificationBuilder.setSound(soundUri);
        }

        if (this.messageSize == MessageSize.LARGE) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(this.message));
        }

        if (this.includePicture) {
            Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

            Canvas canvas  = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(Color.rgb(200, 0, 0));
            canvas.drawRect(10, 50, 90, 90, paint);

            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitmap));
        }

        return notificationBuilder.build();
    }

    public String getMessage() {
        return this.message;
    }

}*/