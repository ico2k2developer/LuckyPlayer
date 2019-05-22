package it.developing.ico2k2.luckyplayer.fragments.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment
{
    public interface OnFragmentInitialized
    {
        void onInitialized(@NonNull View view);
    }

    private OnFragmentInitialized onInitialized;
    private boolean initialized = false;

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState)
    {
        if(onInitialized != null)
            onInitialized.onInitialized(view);
        initialized = true;
    }

    public void setOnFragmentInitialized(OnFragmentInitialized onFragmentInitialized)
    {
        if(initialized)
            onFragmentInitialized.onInitialized(getView());
        else
            onInitialized = onFragmentInitialized;
    }
}
