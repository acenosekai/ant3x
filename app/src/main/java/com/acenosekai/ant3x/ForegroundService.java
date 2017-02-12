package com.acenosekai.ant3x;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.acenosekai.ant3x.factory.sing.Playback;
import com.acenosekai.ant3x.model.Song;

import io.realm.Realm;

/**
 * Created by Acenosekai on 1/8/2017.
 * Rock On
 */

public class ForegroundService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    private static ForegroundService foregroundService;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void buildNotification() {
        Realm r = Realm.getInstance(App.realmConfig());
        Song s = r.where(Song.class).equalTo("directory", Playback.getInstance().getNowPlaying()).findFirst();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        RemoteViews notificationView = new RemoteViews(this.getPackageName(), R.layout.notification);

        // And now, building and attaching the Play button.
        Intent buttonPlayIntent = new Intent(this, NotificationPlayButtonHandler.class);
        buttonPlayIntent.putExtra("action", "togglePause");

        PendingIntent buttonPlayPendingIntent = pendingIntent.getBroadcast(this, 0, buttonPlayIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_button_play, buttonPlayPendingIntent);

        // And now, building and attaching the Skip button.
        Intent buttonSkipIntent = new Intent(this, NotificationSkipButtonHandler.class);
        buttonSkipIntent.putExtra("action", "skip");

        PendingIntent buttonSkipPendingIntent = pendingIntent.getBroadcast(this, 0, buttonSkipIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_button_skip, buttonSkipPendingIntent);

        // And now, building and attaching the Skip button.
        Intent buttonPrevIntent = new Intent(this, NotificationPrevButtonHandler.class);
        buttonPrevIntent.putExtra("action", "prev");

        PendingIntent buttonPrevPendingIntent = pendingIntent.getBroadcast(this, 0, buttonPrevIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_button_prev, buttonPrevPendingIntent);

        // And now, building and attaching the Close button.
        Intent buttonCloseIntent = new Intent(this, NotificationCloseButtonHandler.class);
        buttonCloseIntent.putExtra("action", "close");

        PendingIntent buttonClosePendingIntent = pendingIntent.getBroadcast(this, 0, buttonCloseIntent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_button_close, buttonClosePendingIntent);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(s.getTitle())
                .setTicker(s.getTitle())
                .setContentText(s.getArtist())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContent(notificationView)
                .setOngoing(true).build();

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);
        r.close();
    }

    private Playback.PlaybackOnLoad playbackOnLoad;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null) {
            foregroundService = this;
            command(intent.getAction());
            return START_STICKY;
        }

        return START_NOT_STICKY;
    }

    private void command(String command) {
        switch (command) {
            case Constants.ACTION.START:
            case Constants.ACTION.PLAY:
                playbackOnLoad = Playback.getInstance().addPlaybackOnLoad(p -> {
                    buildNotification();
                });
                Playback.getInstance().play();
                Toast.makeText(this, "Start Service", Toast.LENGTH_SHORT).show();
                App.i("Received Start Foreground Intent ");
                break;
            case Constants.ACTION.PAUSE:
                Playback.getInstance().pause();
                break;
            case Constants.ACTION.NEXT:
                Playback.getInstance().next();
                break;
            case Constants.ACTION.PREV:
                Playback.getInstance().prev();
                break;
            case Constants.ACTION.STOP:
                Playback.getInstance().stop();
                Playback.getInstance().removePlaybackOnLoad(playbackOnLoad);
                Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();
                App.i("Received Stop Foreground Intent");
                this.stopForeground(true);
                this.stopSelf();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        App.i("In onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    /**
     * Called when user clicks the "play/pause" button on the on-going system Notification.
     */
    public static class NotificationPlayButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Playback.getInstance().isPlaying()) {
                foregroundService.command(Constants.ACTION.PAUSE);
            } else {
                foregroundService.command(Constants.ACTION.PLAY);
            }

        }
    }

    /**
     * Called when user clicks the "skip" button on the on-going system Notification.
     */
    public static class NotificationSkipButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            foregroundService.command(Constants.ACTION.NEXT);
        }
    }

    /**
     * Called when user clicks the "previous" button on the on-going system Notification.
     */
    public static class NotificationPrevButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            foregroundService.command(Constants.ACTION.PREV);
        }
    }

    /**
     * Called when user clicks the "close" button on the on-going system Notification.
     */
    public static class NotificationCloseButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            foregroundService.command(Constants.ACTION.STOP);
        }
    }
}
