package it.developing.ico2k2.luckyplayer.activities;

import android.media.AudioManager;

public class BasePlayingActivity extends BaseActivity
{
    @Override
    public void onResume()
    {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
}
