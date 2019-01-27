package com.example.ps.music;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.ps.music.model.Song;

/**
 * Created by poorya on 9/22/2018.
 */

public class NotificationService extends Service {

    public static onServiceStateChange onServiceStateChange;
    public static boolean isServiceRun;
    private Song song;
    RemoteViews bigViews;
    RemoteViews views;
    boolean isplay;
    PendingIntent pendingIntent;
    NotificationManager man;
    NotificationCompat.Builder notibuilder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.song = intent.getParcelableExtra("song");
        String LOG_TAG = "NotificationService";
//
//        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
//            isServiceRun = true;
//            showNotification();
//            bigViews.setImageViewResource(R.id.ib_service_play,
//                    R.drawable.ic_pause_service);
//            isplay = true;
//            updateNotification();
//
//
//        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
//
//            onServiceStateChange.onChangeTrack("previous");
//
//
//        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
//
//            if (isplay) {
//                onServiceStateChange.onServiceChange(true);
//                bigViews.setImageViewResource(R.id.ib_service_play,
//                        R.drawable.ic_play_button_service);
//                isplay = false;
//                updateNotification();
//            } else {
//                onServiceStateChange.onServiceChange(false);
//                bigViews.setImageViewResource(R.id.ib_service_play,
//                        R.drawable.ic_pause_service);
//                isplay = true;
//            }
//
//
//        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
//
//            onServiceStateChange.onChangeTrack("next");
//
//
//        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
//
//            if (PlaySongOnline.isOncreate){
//                onServiceStateChange.onStopService();
//            }
//            stopForeground(true);
//            stopSelf();
//        }

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
//            isServiceRun = true;
            showNotification();
//            bigViews.setImageViewResource(R.id.ib_service_play,
//                    R.drawable.ic_pause_service);
//            isplay = true;
//            updateNotification();
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked Previous");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked Play");
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
            Log.i(LOG_TAG, "Clicked Next");
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {
// Using RemoteViews isSeekbarTouch bind custom layouts into Notification
        views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        bigViews = new RemoteViews(getPackageName(),
                R.layout.notification_service_expanded);

// showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.iv_service_trackImage, View.GONE);

//        if (!Image.isValidImage(song.getSongImage())){
//            bigViews.setImageViewBitmap(R.id.iv_service_trackImage,
//                    Constants.getDefaultAlbumArt(this,song.getSongImage()));
//        }else {
//
//
////            try {
////                URL url = new URL(song.getSongImage());
////                Uri uri = Uri.parse(url.toURI().toString());
////                bigViews.setImageViewUri(R.id.iv_service_trackImage,uri);
////            } catch (MalformedURLException e) {
////                e.printStackTrace();
////            } catch (URISyntaxException e) {
////                e.printStackTrace();
////            }
//
//        }
        Intent notificationIntent = new Intent(this, PlaySongOnline.class);
//        notificationIntent.setAction(Intent.ACTION_MAIN);
//        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, NotificationService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.ib_service_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.ib_service_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.ib_service_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.ib_service_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.ib_service_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.ib_service_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.ib_service_close, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.ib_service_close, pcloseIntent);


        views.setTextViewText(R.id.tv_service_trackName, "Song Title");
//        bigViews.setTextViewText(R.id.tv_service_trackName, song.getSongName());

        views.setTextViewText(R.id.tv_service_artistName, "Artist Name");
//        bigViews.setTextViewText(R.id.tv_service_artistName, song.getArtist());

//        bigViews.setTextViewText(R.id.tv_service_albumName, song.getAlbumName());


//        Notification status = new Notification.Builder(this).build();
//        status.contentView = views;
//        status.bigContentView = bigViews;
//        status.flags = Notification.FLAG_ONGOING_EVENT;
//        status.icon = R.mipmap.ic_launcher;
//        status.contentIntent = pendingIntent;
//        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
        setNotification(views, bigViews, pendingIntent);
    }

    public void setNotification(RemoteViews view, RemoteViews bigView, PendingIntent intent) {
        man = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notibuilder = new NotificationCompat.Builder(this);

        notibuilder.setCustomContentView(view)
                .setCustomBigContentView(bigView)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent).setContent(view);
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notibuilder.build());

    }

    private void updateNotification() {

        man.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notibuilder.build());
    }

    public interface onServiceStateChange {
        void onServiceChange(boolean isPlaying);
        void onStopService();
        void onChangeTrack(String tracState);
    }

}
