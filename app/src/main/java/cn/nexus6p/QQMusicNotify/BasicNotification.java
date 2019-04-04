package cn.nexus6p.QQMusicNotify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.session.MediaSession;
import android.util.Log;

import static android.app.Notification.FLAG_FOREGROUND_SERVICE;
import static android.app.Notification.FLAG_NO_CLEAR;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

class BasicNotification {

    Context context;
    CharSequence titleString;
    CharSequence textString;
    int iconID;
    boolean statue = true;
    String mediaSessionTag;
    Intent preSongIntent;
    Intent nextSongIntent;
    Intent playIntent;
    Bitmap bitmap;
    Intent contentIntent;
    Context serverContext;
    ClassLoader classLoader;
    int intentRequestID = 0;
    Boolean hasExtraAction = false;
    Intent extraActionIntent;
    int extraActionIcon;

    BasicNotification (ClassLoader mClassLoader) {
        if (mClassLoader==null) {
            Log.e("QQMusicNotify","ClassLoader should not be null");
            return;
        }
        classLoader = mClassLoader;
    }

    Notification build() {
        MediaSession mediaSession = new MediaSession(serverContext==null?context:serverContext,mediaSessionTag);
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(titleString)
                .setContentText(textString)
                .setSmallIcon(iconID)
                .setOngoing(statue)
                .setCategory(Notification.CATEGORY_STATUS)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .addAction(android.R.drawable.ic_media_previous,"后退", PendingIntent.getBroadcast(context, 0, preSongIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(statue?android.R.drawable.ic_media_pause:android.R.drawable.ic_media_play,statue?"暂停":"播放",PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(android.R.drawable.ic_media_next, "前进",PendingIntent.getBroadcast(context, 0, nextSongIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                .setStyle(new Notification.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1, 2)
                )
                .setLargeIcon(bitmap);
        if (hasExtraAction) builder.addAction(extraActionIcon,"桌面歌词",PendingIntent.getBroadcast(context, 0, extraActionIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        if (bitmap == null) builder.setColor(Color.BLACK);
        Notification notification = builder.build();
        if (statue) notification.flags = FLAG_FOREGROUND_SERVICE | FLAG_NO_CLEAR;
        contentIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        notification.contentIntent = PendingIntent.getActivity(context, intentRequestID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return notification;
    }
}