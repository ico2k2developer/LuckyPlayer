package it.developing.ico2k2.luckyplayer.activities.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import it.developing.ico2k2.luckyplayer.DataManager;
import it.developing.ico2k2.luckyplayer.LuckyPlayer;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.MainActivity;

import static it.developing.ico2k2.luckyplayer.Keys.KEY_INITIALIZED;
import static it.developing.ico2k2.luckyplayer.Keys.KEY_THEME;

public abstract class BaseActivity extends AppCompatActivity
{
    protected static final int THEME_DEFAULT = R.style.Theme_Dark_Red;

    private int currentTheme;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        currentTheme = setTheme(getDataManager().getInt(KEY_THEME),THEME_DEFAULT);
        super.onCreate(savedInstanceState);
        setKitKatStatusBarColor(getColorPrimaryDark());
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH)
        {
            if(getNavigationBarColored())
                getWindow().setNavigationBarColor(getNavigationBarDefaultColor());
        }
        Log.d("UWUWU",getClass().getName() + ": created");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d("UWUWU",getClass().getName() + ": resumed");
        if(getDataManager().getBoolean(KEY_INITIALIZED,false))
        {
            int theme = getDataManager().getInt(KEY_THEME);
            if(theme != currentTheme)
            {
                if(onThemeChanged(currentTheme,theme))
                {
                    Log.d("UWUWU","Restarting " + getClass().getName()+ " because of theme change");
                    startActivity(getIntent());
                    finish();
                }

            }
            else
            {
                if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) !=  PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            }

        }
        else
        {
            if(onNoDataFound())
            {
                startActivity(new Intent(this,MainActivity.class));
                finish();
            }
        }

    }

    public boolean onThemeChanged(@StyleRes int oldTheme,@StyleRes int newTheme)
    {
        Log.d("UWUWU",getClass().getName() + ": theme has changed! " + Integer.toString(oldTheme) + " to " + Integer.toString(newTheme));
        return true;
    }

    public boolean onNoDataFound()
    {
        Log.d("UWUWU",getClass().getName() + ": no data found!");
        return true;
    }

    protected View getContentView()
    {
        View result = findViewById(android.R.id.content);
        if(result == null)
            result = getWindow().getDecorView().findViewById(android.R.id.content);
        return ((ViewGroup)result).getChildAt(0);
    }

    protected LuckyPlayer getLuckyPlayer()
    {
        return (LuckyPlayer)getApplication();
    }

    protected DataManager getDataManager()
    {
        return getLuckyPlayer().getDataManager();
    }

    protected int setTheme(@StyleRes int theme,@StyleRes int exceptionTheme)
    {
        boolean success = theme != 0;
        if(success)
        {
            try
            {
                setTheme(theme);
            }
            catch(Exception e)
            {
                success = false;
            }
        }
        if(!success)
        {
            setTheme(exceptionTheme);
            theme = exceptionTheme;
        }
        return theme;
    }

    public void setClipboard(String label,String text,boolean showToast)
    {
        ClipboardManager manager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        manager.setPrimaryClip(ClipData.newPlainText(label,text));
        if(showToast)
            Toast.makeText(this,R.string.copied,Toast.LENGTH_SHORT).show();
    }

    public void setClipboard(String label,String text)
    {
        setClipboard(label,text,false);
    }

    protected int getColorAccent()
    {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent,value,true);
        return ContextCompat.getColor(this,value.resourceId);
    }

    protected int getColorPrimary()
    {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary,value,true);
        return ContextCompat.getColor(this,value.resourceId);
    }

    protected int getColorPrimaryDark()
    {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark,value,true);
        return ContextCompat.getColor(this,value.resourceId);
    }

    @RequiresApi(21)
    protected int getNavigationBarDefaultColor()
    {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(R.attr.navigationBarDefaultColor,value,true);
        return ContextCompat.getColor(this,value.resourceId);
    }

    @RequiresApi(21)
    protected int getNavigationBarPlayingColor()
    {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(R.attr.navigationBarPlayingColor,value,true);
        return ContextCompat.getColor(this,value.resourceId);
    }

    @RequiresApi(21)
    protected boolean getNavigationBarColored()
    {
        boolean result;
        TypedArray a = getTheme().obtainStyledAttributes(new int[] {R.attr.navigationBarColored});
        result = a.getBoolean(0,false);
        return result;
    }

    @TargetApi(19)
    protected void setTranslucentStatusBar(boolean translucent)
    {
        if(translucent)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        else
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @TargetApi(19)
    protected void setKitKatStatusBarColor(int color)
    {
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT || Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT_WATCH)
        {
            setTranslucentStatusBar(true);
            //layout.setPaddingRelative(0,context.getResources().getDimensionPixelSize(context.getResources().getIdentifier("status_bar_height","dimen","android")),0,0);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setTintColor(color);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean result = true;

        switch(id)
        {
            case android.R.id.home:
            {
                onBackPressed();
                break;
            }
            default:
            {
                result = super.onOptionsItemSelected(item);
            }
        }
        return result;
    }
}
