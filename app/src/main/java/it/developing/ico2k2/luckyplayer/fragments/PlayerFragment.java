package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.adapters.items.Song;
import it.developing.ico2k2.luckyplayer.fragments.base.BaseFragment;

public class PlayerFragment extends BaseFragment implements Player
{
    private boolean touchingBar = false;
    private Map<Integer,Object> cache = new HashMap<>();

    private static final int SETTING_PLAYING = 0x10;
    private static final int SETTING_TIME_PROGRESS = 0x11;
    private static final int SETTING_TIME_TOTAL = 0x12;
    private static final int SETTING_TITLE = 0x13;
    private static final int SETTING_SUBTITLE = 0x14;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,Bundle savedInstance)
    {
        return inflater.inflate(R.layout.fragment_player,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view,savedInstanceState);
        getPlayButton().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
                if(controller != null){
                    if(controller.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING)
                        controller.getTransportControls().pause();
                    else
                        controller.getTransportControls().play();
                    getTimeBar().setProgress((int)controller.getPlaybackState().getPosition());
                }
            }
        });
        getTimeBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
                touchingBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
                MediaControllerCompat controller = MediaControllerCompat.getMediaController(getActivity());
                controller.getTransportControls().seekTo(seekBar.getProgress());
                touchingBar = false;
            }
        });
        updatePlayState();
        updateTimeProgress();
        updateTimeTotal();
        updateText();
    }

    public void setPlaying(boolean playing)
    {
        cache.put(SETTING_PLAYING,playing);
        if(isViewCreated())
        {
            updatePlayState();
        }
    }

    public void setTimeProgress(long ms)
    {
        cache.put(SETTING_TIME_PROGRESS,ms);
        if(isViewCreated())
        {
            updateTimeProgress();
        }
    }

    public void setTimeTotal(long ms)
    {
        cache.put(SETTING_TIME_TOTAL,ms);
        if(isViewCreated())
        {
            updateTimeTotal();
        }
    }

    public void setTitleSubtitle(String title,String subtitle)
    {
        cache.put(SETTING_TITLE,title);
        cache.put(SETTING_SUBTITLE,subtitle);
        if(isViewCreated())
        {
            updateText();
        }
    }

    public ImageButton getPlayButton()
    {
        ImageButton result = null;
        if(getView() != null)
            result = getView().findViewById(R.id.player_play);
        return result;
    }

    public SeekBar getTimeBar()
    {
        SeekBar result = null;
        if(getView() != null)
            result = getView().findViewById(R.id.player_bar);
        return result;
    }

    public TextView getCurrentTimeLabel()
    {
        TextView result = null;
        if(getView() != null)
            result = getView().findViewById(R.id.player_time);
        return result;
    }

    public TextView getTotalTimeLabel()
    {
        TextView result = null;
        if(getView() != null)
            result = getView().findViewById(R.id.player_time_end);
        return result;
    }

    public TextView getTitleLabel()
    {
        TextView result = null;
        if(getView() != null)
            result = getView().findViewById(R.id.player_title);
        return result;
    }

    public TextView getSubtitleLabel()
    {
        TextView result = null;
        if(getView() != null)
            result = getView().findViewById(R.id.player_subtitle);
        return result;
    }

    void updatePlayState()
    {
        ImageButton play = getPlayButton();
        if(play != null)
        {
            if(isPlaying())
                play.setImageResource(R.drawable.ic_pause_material_dark);
            else
                play.setImageResource(R.drawable.ic_play_material_dark);
        }
    }

    void updateTimeProgress()
    {
        SeekBar bar = getTimeBar();
        if(bar != null)
        {
            int progress = 0;
            if(cache.containsKey(SETTING_TIME_PROGRESS))
                progress = ((Long)cache.get(SETTING_TIME_PROGRESS)).intValue();
            if(!touchingBar)
                bar.setProgress(progress);
            getCurrentTimeLabel().setText(Song.getSongTimeDescription(progress));
        }
    }

    void updateTimeTotal()
    {
        SeekBar bar = getTimeBar();
        if(bar != null)
        {
            int total = 0;
            if(cache.containsKey(SETTING_TIME_TOTAL))
                total = ((Long)cache.get(SETTING_TIME_TOTAL)).intValue();
            bar.setMax(total);
            getTotalTimeLabel().setText(Song.getSongTimeDescription(total));
        }
    }

    void updateText()
    {
        TextView title = getTitleLabel(),subtitle = getSubtitleLabel();
        if(title != null)
        {
            String text = "";
            if(cache.containsKey(SETTING_TITLE))
                text = (String) cache.get(SETTING_TITLE);
            title.setText(text);
        }
        if(subtitle != null)
        {
            String text = "";
            if(cache.containsKey(SETTING_SUBTITLE))
                text = (String) cache.get(SETTING_SUBTITLE);
            subtitle.setText(text);
        }
    }

    public boolean isPlaying()
    {
        boolean result = false;
        if(cache.containsKey(SETTING_PLAYING))
            result = (Boolean)cache.get(SETTING_PLAYING);
        return result;
    }

    public long getTimeProgress()
    {
        long result = 0;
        if(cache.containsKey(SETTING_TIME_PROGRESS))
            result = ((Long)cache.get(SETTING_TIME_PROGRESS));
        return result;
    }

    public long getTimeTotal()
    {
        long result = 0;
        if(cache.containsKey(SETTING_TIME_TOTAL))
            result = ((Long)cache.get(SETTING_TIME_TOTAL));
        return result;
    }

    public String getTitle()
    {
        String result = "";
        if(cache.containsKey(SETTING_TITLE))
            result = (String)cache.get(SETTING_TITLE);
        return result;
    }

    public String getSubtitle()
    {
        String result = "";
        if(cache.containsKey(SETTING_SUBTITLE))
            result = (String)cache.get(SETTING_SUBTITLE);
        return result;
    }
}
