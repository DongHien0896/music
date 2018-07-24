package com.example.dong.music;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class TaskLoadSongs extends AsyncTask<Void, Void, List<ItemSong>> {

    private Context mContext;
    private ActionWithSongs mLoadSongs;
    private List<ItemSong> mSongs = new ArrayList<>();

    public TaskLoadSongs(Context context, ActionWithSongs loadSongs){
        this.mContext = context;
        this.mLoadSongs = loadSongs;
    }

    @Override
    protected List<ItemSong> doInBackground(Void... voids) {
        Uri uri = MediaStore.Files.getContentUri(Constants.EXTERNAL);
        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        mSongs.clear();
        int idxPath = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        int idxSong = cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String path = cursor.getString(idxPath);
            if (path.contains(Constants.MP3)) {
                if (path.contains(Constants.SPACE)) {
                    String informSong = cursor.getString(idxSong);
                    String[] name = informSong.split(Constants.SPACE);
                    String nameSong = name[Constants.ZERO];
                    String nameSinger = Constants.EMPTY;
                    if (name.length == Constants.TWO) {
                        nameSinger += name[Constants.ONE];
                    }
                    mSongs.add(new ItemSong(nameSong, nameSinger, path));
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        return mSongs;
    }

    @Override
    protected void onPostExecute(List<ItemSong> songs) {
        super.onPostExecute(songs);
        mLoadSongs.loadSongs(mSongs);
    }

}
