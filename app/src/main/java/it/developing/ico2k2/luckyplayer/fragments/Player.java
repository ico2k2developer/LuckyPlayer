package it.developing.ico2k2.luckyplayer.fragments;

import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public interface Player
{
    void setPlaying(boolean playing);
    void setTimeProgress(long ms);
    void setTimeTotal(long ms);
    void setTitleSubtitle(String title,String subtitle);
    ImageButton getPlayButton();
    SeekBar getTimeBar();
    TextView getTitleLabel();
    TextView getSubtitleLabel();
    TextView getCurrentTimeLabel();
    TextView getTotalTimeLabel();
}
