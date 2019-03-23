package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import it.developing.ico2k2.luckyplayer.R;

public class SmallPlayerFragment extends Fragment
{

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,Bundle savedInstance)
    {
        return inflater.inflate(R.layout.player_small_layout,null);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState)
    {
        CardView cardView = view.findViewById(R.id.player_card_view);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            TypedValue value = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.cardViewBackgroundColor,value,true);
            cardView.setBackgroundColor(ContextCompat.getColor(getContext(),value.resourceId));
        }
    }
}
