package it.developing.ico2k2.luckyplayer.fragments.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.security.PublicKey;

public class BaseFragment extends Fragment
{
    private boolean viewCreated = false;

    @CallSuper
    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState)
    {
        viewCreated = true;
    }

    public boolean isViewCreated()
    {
        return viewCreated;
    }
}
