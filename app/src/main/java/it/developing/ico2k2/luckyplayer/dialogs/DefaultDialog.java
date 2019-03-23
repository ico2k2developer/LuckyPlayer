package it.developing.ico2k2.luckyplayer.dialogs;

import android.os.Build;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.view.MenuItem;

/**
 * Created by Ico on 10/03/2018.
 */

public class DefaultDialog extends AlertDialog.Builder
{

    public DefaultDialog(Context context, int theme)
    {
        super(context,theme);
        initialize(context);
    }

    public DefaultDialog(Context context)
    {
        super(context);
        initialize(context);
    }

    protected void initialize(Context context)
    {
        setCancelable(true);
    }

    public AlertDialog.Builder setTitleAndIcon(MenuItem item)
    {
        setTitle(item.getTitle());
        setIcon(item.getIcon());
        return this;
    }

    public AlertDialog.Builder setTitleAndIcon(Preference item)
    {
        setTitle(item.getTitle());
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1)
            setIcon(item.getIcon());
        return this;
    }

    @Override
    @RequiresApi(11)
    public AlertDialog.Builder setIcon(@DrawableRes int icon)
    {
        return super.setIcon(icon);
    }

    public AlertDialog.Builder setPositiveButton(CharSequence text)
    {
        return setPositiveButton(text,null);
    }

    public AlertDialog.Builder setNeutralButton(CharSequence text)
    {
        return setNeutralButton(text,null);
    }

    public AlertDialog.Builder setNegativeButton(CharSequence text)
    {
        return setNegativeButton(text,null);
    }

    public AlertDialog.Builder setPositiveButton(int resId)
    {
        return setPositiveButton(resId,null);
    }

    public AlertDialog.Builder setNeutralButton(int resId)
    {
        return setNeutralButton(resId,null);
    }

    public AlertDialog.Builder setNegativeButton(int resId)
    {
        return setNegativeButton(resId,null);
    }

}
