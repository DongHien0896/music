package com.example.dong.music;

import java.util.List;

public interface ActionWithSongs {
    void loadSongs(List<ItemSong> songs);

    void updateSeekBar(int progress, int duration);

    void updateStatus(boolean isPlaying);

    void updateTime(String time);

}
