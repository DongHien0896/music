package com.example.dong.music;

public class ItemSong {

    private String mNameSong;
    private String mNameSinger;
    private String mPath;

    public ItemSong(String namesong, String nameSinger, String path) {
        this.mNameSong = namesong;
        this.mNameSinger = nameSinger;
        this.mPath = path;
    }

    public String getNameSong() {
        return mNameSong;
    }

    public void setNameSong(String nameSong) {
        this.mNameSong = nameSong;
    }

    public String getNameSinger() {
        return mNameSinger;
    }

    public void setNameSinger(String nameSinger) {
        this.mNameSinger = nameSinger;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

}
