package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.fragments.base.BaseFragment;

public class PlayerFragment extends BaseFragment implements Player
{

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,Bundle savedInstance)
    {
        return inflater.inflate(R.layout.fragment_player,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view,savedInstanceState);
    }

    public void setPlaying(boolean playing)
    {
        ImageButton play = getView().findViewById(R.id.player_play);
        if(play != null)
        {
            if(playing)
                play.setBackgroundResource(R.drawable.ic_pause_material_dark);
            else
                play.setBackgroundResource(R.drawable.ic_play_material_dark);
        }
    }

    public void setTimeProgress(int ms)
    {
        SeekBar bar = getView().findViewById(R.id.player_bar);
        if(bar != null)
        {
            bar.setProgress(ms);
        }
    }

    public void setTimeTotal(int ms)
    {
        SeekBar bar = getView().findViewById(R.id.player_bar);
        if(bar != null)
        {
            bar.setMax(ms);
        }
    }
}
