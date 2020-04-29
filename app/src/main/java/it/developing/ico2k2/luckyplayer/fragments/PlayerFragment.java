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

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.adapters.Song;
import it.developing.ico2k2.luckyplayer.fragments.base.BaseFragment;

public class PlayerFragment extends BaseFragment implements Player
{
    private boolean playing = false,touchingBar = false;
    private long progress = 0,total = 1;

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
    }

    public void setPlaying(boolean playing)
    {
        this.playing = playing;
        updatePlayState();
    }

    public boolean isPlaying()
    {
        return playing;
    }

    public void setTimeProgress(long ms)
    {
        progress = ms;
        updateTimeProgress();
    }

    public void setTimeTotal(long ms)
    {
        total = ms;
        updateTimeTotal();
    }

    public void setTitle(String title)
    {
        TextView text = getTitleLabel();
        if(text != null)
            text.setText(title);
    }

    public void setSubtitle(String subtitle)
    {
        TextView text = getSubtitleLabel();
        if(text != null)
            text.setText(subtitle);
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

    protected void updatePlayState()
    {
        ImageButton play = getPlayButton();
        if(play != null)
        {
            if(playing)
                play.setImageResource(R.drawable.ic_pause_material_dark);
            else
                play.setImageResource(R.drawable.ic_play_material_dark);
        }
    }

    protected void updateTimeProgress()
    {
        SeekBar bar = getTimeBar();
        if(bar != null)
        {
            if(!touchingBar)
                bar.setProgress((int)progress);
            getCurrentTimeLabel().setText(Song.getSongTimeDescription(progress));
        }
    }

    protected void updateTimeTotal()
    {
        SeekBar bar = getTimeBar();
        if(bar != null)
        {
            bar.setMax((int)total);
            getTotalTimeLabel().setText(Song.getSongTimeDescription(total));
        }
    }
}
