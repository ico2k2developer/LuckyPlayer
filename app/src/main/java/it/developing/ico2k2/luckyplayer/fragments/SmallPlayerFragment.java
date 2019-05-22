package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.fragments.base.BaseFragment;

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
}
