package it.developing.ico2k2.luckyplayer.adapters.layoutmanagers;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class BaseLayoutManager extends LinearLayoutManager
{
    private boolean isScrollEnabled = true;

    public BaseLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically()
    {
        return isScrollEnabled && super.canScrollVertically();
    }

    @Override
    public boolean canScrollHorizontally()
    {
        return isScrollEnabled && super.canScrollHorizontally();
    }
}
