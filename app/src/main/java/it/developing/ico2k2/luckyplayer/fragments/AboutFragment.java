package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.fragments.base.BaseFragment;

import static it.developing.ico2k2.luckyplayer.BuildConfig.VERSION_CODE;
import static it.developing.ico2k2.luckyplayer.BuildConfig.VERSION_NAME;

public class AboutFragment extends BaseFragment
{
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,Bundle savedInstance)
    {
        return inflater.inflate(R.layout.fragment_about,null);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view,savedInstanceState);
        ((AppCompatTextView)view.findViewById(R.id.about_label)).setText(getString(R.string.app_about,getString(R.string.app_name)));
        ((AppCompatTextView)view.findViewById(R.id.about_version)).setText(getString(R.string.app_about_version,VERSION_NAME,Integer.toString(VERSION_CODE)));
    }
}
