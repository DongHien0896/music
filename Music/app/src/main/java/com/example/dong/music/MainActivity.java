package com.example.dong.music;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SongAdapter.InterfaceSong {

    private RecyclerView mRecyclerSong;
    private TextView mTextTimeStart;
    private TextView mTextTimeEnd;
    private SeekBar mSeekBarTime;
    private Button mButtonShuffleOn;
    private Button mButtonShuffleOff;
    private Button mButtonPreviuos;
    private Button mButtonPlay;
    private Button mButtonPause;
    private Button mButtonNext;
    private Button mButtonRepeatOff;
    private Button mButtonRepeatOne;
    private Button mButtonRepeatAll;
    private List<ItemSong> mSongs;
    public static SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkPermission(this);
        setEvent();
    }

    private void setComponent() {
        mSongs = new ArrayList<>();
        songAdapter = new SongAdapter(this, this);
        mRecyclerSong.setOnClickListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        mRecyclerSong.setLayoutManager(manager);
        new TaskLoadSongs(this, mActionWithSongs).execute();
    }

    private ActionWithSongs mActionWithSongs = new ActionWithSongs() {
        @Override
        public void loadSongs(List<ItemSong> songs) {
            mSongs = songs;
            ServiceMedia.songs = songs;
            mRecyclerSong.setAdapter(songAdapter);
            songAdapter.notifyDataSetChanged();
        }

        @Override
        public void updateSeekBar(int progress, int duration) {
            setSeek(duration);
            mSeekBarTime.setProgress(progress);
        }

        @Override
        public void updateStatus(boolean isPlaying) {

        }

        @Override
        public void updateTime(String time) {
            mTextTimeStart.setText(time);
        }
    };

    public void checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog(Constants.MESSAGE, context, Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
            } else {
                setComponent();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setComponent();
                } else {
                    Toast.makeText(this, Constants.MESSAGE_DENIED, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    public void showDialog(final String msg, final Context context, final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(Constants.TITLE_DIALOG);
        alertBuilder.setMessage(msg + Constants.MESSAGE_DIALOG);
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void setEvent() {
        mButtonShuffleOff.setOnClickListener(this);
        mButtonShuffleOn.setOnClickListener(this);
        mButtonPreviuos.setOnClickListener(this);
        mButtonPlay.setOnClickListener(this);
        mButtonPause.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mButtonRepeatOff.setOnClickListener(this);
        mButtonRepeatOne.setOnClickListener(this);
        mButtonRepeatAll.setOnClickListener(this);
    }

    private void initView() {
        mRecyclerSong = findViewById(R.id.recyclerview_songs);
        mTextTimeStart = findViewById(R.id.text_time_start);
        mTextTimeEnd = findViewById(R.id.text_time_end);
        mSeekBarTime = findViewById(R.id.seekbar_time);
        mButtonShuffleOn = findViewById(R.id.button_shuffle_on);
        mButtonShuffleOff = findViewById(R.id.button_shuffle_off);
        mButtonPreviuos = findViewById(R.id.button_previuos);
        mButtonPlay = findViewById(R.id.button_play);
        mButtonPause = findViewById(R.id.button_pause);
        mButtonNext = findViewById(R.id.button_next);
        mButtonRepeatOff = findViewById(R.id.button_repeat_off);
        mButtonRepeatOne = findViewById(R.id.button_repeat_1);
        mButtonRepeatAll = findViewById(R.id.button_repeat_all);
    }

    private void createService(String action) {
        Intent intent = new Intent();
        intent.setClass(this, ServiceMedia.class);
        intent.setAction(action);
        startService(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_shuffle_off:
                shufferOff();
                break;
            case R.id.button_shuffle_on:
                shuffer();
                break;
            case R.id.button_previuos:
                createService(Constants.ACTION_PREVIOUS);
                playPreviuos();
                break;
            case R.id.button_play:
                createService(Constants.ACTION_PLAY);
                playing();
                break;
            case R.id.button_pause:
                createService(Constants.ACTION_PAUSE);
                pauseSong();
                break;
            case R.id.button_next:
                createService(Constants.ACTION_NEXT);
                playNext();
                break;
            case R.id.button_repeat_1:
                mButtonRepeatOne.setVisibility(View.INVISIBLE);
                mButtonRepeatAll.setVisibility(View.VISIBLE);
                break;
            case R.id.button_repeat_off:
                mButtonRepeatOff.setVisibility(View.INVISIBLE);
                mButtonRepeatOne.setVisibility(View.VISIBLE);
                break;
            case R.id.button_repeat_all:
                break;
            default:
                break;
        }
    }

    private void playing() {
        mButtonPlay.setVisibility(View.GONE);
        mButtonPause.setVisibility(View.VISIBLE);
    }

    private void pauseSong() {
        mButtonPlay.setVisibility(View.VISIBLE);
        mButtonPause.setVisibility(View.GONE);
    }

    private void playNext() {
        mButtonPlay.setVisibility(View.GONE);
        mButtonPause.setVisibility(View.VISIBLE);
    }

    private void playPreviuos() {
        mButtonPlay.setVisibility(View.GONE);
        mButtonPause.setVisibility(View.VISIBLE);
    }

    private void shuffer() {
        Collections.shuffle(mSongs);
        mButtonShuffleOn.setVisibility(View.VISIBLE);
        mButtonShuffleOff.setVisibility(View.GONE);
    }

    private void shufferOff() {
        mButtonShuffleOn.setVisibility(View.GONE);
        mButtonShuffleOff.setVisibility(View.VISIBLE);
    }

    @Override
    public int getCount() {
        return mSongs == null ? 0 : mSongs.size();
    }

    @Override
    public ItemSong getItemSong(int position) {
        return mSongs.get(position);
    }

    @Override
    public void onClick(int position) {
        ServiceMedia.weakReference = new WeakReference<Activity>(MainActivity.this);
        ServiceMedia.actionWithSong = mActionWithSongs;
        Intent intent = new Intent(MainActivity.this, ServiceMedia.class);
        intent.setAction(Constants.ACTION_PLAY_NEW);
        intent.putExtra(Constants.POSITION_SONG, position);
        startService(intent);
        play(position);
    }

    private void play(int position) {
        songAdapter.setCurrentPosition(position);
        songAdapter.notifyDataSetChanged();
        mButtonPlay.setVisibility(View.INVISIBLE);
        mButtonPause.setVisibility(View.VISIBLE);
    }

    private void setSeek(int duration) {
        long minuteEnd = TimeUnit.MILLISECONDS.toMinutes(duration);
        long secondEnd = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minuteEnd);
        mTextTimeEnd.setText(String.format(Constants.FORMAT, minuteEnd, secondEnd));
        mSeekBarTime.setMax(duration);
        mSeekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    ServiceMedia.seek.seek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
