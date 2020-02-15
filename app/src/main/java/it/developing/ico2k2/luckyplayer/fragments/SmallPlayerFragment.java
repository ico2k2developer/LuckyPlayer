package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import it.developing.ico2k2.luckyplayer.R;

public class SmallPlayerFragment extends PlayerFragment implements Player
{

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,Bundle savedInstance)
    {
        return inflater.inflate(R.layout.fragment_player_small,null);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view,savedInstanceState);
        CardView cardView = view.findViewById(R.id.player_card_view);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            TypedValue value = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.cardViewBackgroundColor,value,true);
            cardView.setBackgroundColor(ContextCompat.getColor(getContext(),value.resourceId));
        }
    }

    @Override
    protected void updatePlayState()
    {
        ImageButton play = getPlayButton();
        if(play != null)
        {
            TypedValue typedValue = new TypedValue();
            if(isPlaying())
                getActivity().getTheme().resolveAttribute(R.attr.ic_pause,typedValue,true);
            else
                getActivity().getTheme().resolveAttribute(R.attr.ic_play,typedValue,true);
            play.setImageResource(typedValue.resourceId);
        }
    }
}
