package com.example.dong.music;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServiceMedia extends Service {

    public static WeakReference<Activity> weakReference;
    public static ActionWithSongs actionWithSong;
    public static List<ItemSong> songs;
    public static MediaPlayer mMediaSong = new MediaPlayer();
    private int mCurentSong = -1;
    private Handler mHandler = new Handler();
    private Notification mNotificationSong;

    public static Seek seek = new Seek() {
        @Override
        public void seek(int progress) {
            mMediaSong.seekTo(progress);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case Constants.ACTION_PLAY_NEW:
                mCurentSong = intent.getIntExtra(Constants.POSITION_SONG,
                        Constants.DEFAULT_POSITION_SONG);
                playSong(mCurentSong);
                mNotificationSong = creatNotification(songs.get(mCurentSong));
                startForeground(Constants.ID, mNotificationSong);
                break;
            case Constants.ACTION_PLAY:
                play();
                updateNotification(songs.get(mCurentSong));
                break;
            case Constants.ACTION_PREVIOUS:
                previous();
                updateNotification(songs.get(mCurentSong));
                break;
            case Constants.ACTION_NEXT:
                nextSong();
                updateNotification(songs.get(mCurentSong));
                break;
            case Constants.ACTION_PAUSE:
                if (mMediaSong != null) {
                    if (mMediaSong.isPlaying())
                        mMediaSong.pause();
                }
                break;
            case Constants.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return START_STICKY;
    }

    public Notification creatNotification(ItemSong song) {
        Intent intentPlay = new Intent(this, ServiceMedia.class);
        intentPlay.setAction(Constants.ACTION_PLAY);
        PendingIntent pdIntentPlay = PendingIntent.getService(this, 0,
                intentPlay, 0);

        Intent intentNext = new Intent(this, ServiceMedia.class);
        intentNext.setAction(Constants.ACTION_NEXT);
        PendingIntent pdIntentNext = PendingIntent.getService(this, 0,
                intentNext, 0);

        Intent intentPre = new Intent(this, ServiceMedia.class);
        intentPre.setAction(Constants.ACTION_PREVIOUS);
        PendingIntent pdIntendPrev = PendingIntent.getService(this, 0,
                intentPre, 0);

        Intent intent = new Intent(this, ServiceMedia.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Constants.ACTION_CANCEL);
        PendingIntent dismissIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mNotiBuild = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setContentTitle(song.getNameSong())
                .setContentText(song.getNameSinger())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker(song.getNameSong())
                .setAutoCancel(true)
                .setContentIntent(dismissIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(songs.get(mCurentSong).getNameSinger()))
                .addAction(R.drawable.ic_cancel, getString(R.string.cancel), dismissIntent)
                .addAction(R.drawable.ic_prev, getString(R.string.previous), pdIntendPrev)
                .addAction(R.drawable.ic_pause, getString(R.string.pause), pdIntentPlay)
                .addAction(R.drawable.ic_next, getString(R.string.next), pdIntentNext);

        return mNotiBuild.build();
    }

    public void updateNotification(ItemSong song) {
        Intent intentNext = new Intent(this, ServiceMedia.class);
        intentNext.setAction(Constants.ACTION_NEXT);
        PendingIntent pdIntentNext = PendingIntent.getService(this, 0,
                intentNext, 0);

        Intent intentPre = new Intent(this, ServiceMedia.class);
        intentPre.setAction(Constants.ACTION_PREVIOUS);
        PendingIntent pdIntendPrev = PendingIntent.getService(this, 0,
                intentPre, 0);

        Intent intentPlay = new Intent(this, ServiceMedia.class);
        intentPlay.setAction(Constants.ACTION_PLAY);
        PendingIntent pdIntentPlay = PendingIntent.getService(this, 0,
                intentPlay, 0);

        Intent intent = new Intent(this, ServiceMedia.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Constants.ACTION_CANCEL);
        PendingIntent dismissIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mNotiBuild = new NotificationCompat.Builder(this, Constants.CHANNEL_ID)
                .setContentTitle(song.getNameSong())
                .setContentText(song.getNameSinger())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker(song.getNameSong())
                .setAutoCancel(true)
                .setContentIntent(dismissIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(songs.get(mCurentSong).getNameSinger()))
                .addAction(R.drawable.ic_cancel, getString(R.string.cancel), dismissIntent)
                .addAction(R.drawable.ic_prev, getString(R.string.previous), pdIntendPrev);
        if (mMediaSong.isPlaying()) {
            mNotiBuild.addAction(R.drawable.ic_pause, getString(R.string.pause), pdIntentPlay);
        } else {
            mNotiBuild.addAction(R.drawable.ic_play, getString(R.string.play), pdIntentPlay);
        }
        mNotiBuild.addAction(R.drawable.ic_next, getString(R.string.next), pdIntentNext);
        mNotificationSong = mNotiBuild.build();
        NotificationManagerCompat.from(this).notify(Constants.ID, mNotificationSong);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int timeBegin = mMediaSong.getCurrentPosition();
            long minuteBegin = TimeUnit.MILLISECONDS.toMinutes(timeBegin);
            long secondBegin = TimeUnit.MILLISECONDS.toSeconds(timeBegin) - TimeUnit.MINUTES.toSeconds(minuteBegin);
            actionWithSong.updateTime(String.format(Constants.FORMAT, minuteBegin, secondBegin));
            mHandler.postDelayed(this, Constants.TIME_DELAY);
            actionWithSong.updateSeekBar(timeBegin, mMediaSong.getDuration());
        }
    };

    private void nextSong() {
        if (mCurentSong < songs.size() - 1) {
            playSong(++mCurentSong);
        } else {
            mCurentSong = 0;
            playSong(mCurentSong);
        }
    }

    private void previous() {
        if (mCurentSong > 0) {
            playSong(--mCurentSong);
        } else {
            mCurentSong = songs.size() - 1;
            playSong(mCurentSong);
        }
    }

    private void play() {
        if (mMediaSong != null) {
            if (mMediaSong.isPlaying())
                mMediaSong.pause();
            else mMediaSong.start();
        }
        mHandler.postDelayed(runnable, Constants.TIME_DELAY);
        actionWithSong.updateStatus(mMediaSong.isPlaying());
    }

    private void playSong(int position) {
        MainActivity.songAdapter.setCurrentPosition(position);
        MainActivity.songAdapter.notifyDataSetChanged();
        if (mMediaSong.isPlaying()) {
            mMediaSong.stop();
            mMediaSong.release();
            mMediaSong = null; // dùng xong xóa,,,,
            mHandler.removeCallbacks(runnable);
        }
        try {
            mMediaSong = new MediaPlayer(); // Mỗi lần dùng mới thì phải tạo lại...
            mMediaSong.setDataSource(songs.get(position).getPath());
            mMediaSong.prepare();
            mMediaSong.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mHandler.postDelayed(runnable, 100);
    }

}
