package it.developing.ico2k2.luckyplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ico on 12/09/2017.
 */

public class DataManager
{
    private SharedPreferences prefs;
    private Semaphore guard;
    private SharedPreferences.OnSharedPreferenceChangeListener internListener;
    private long waitingTime = 500;

    private static final TimeUnit waitingUnit = TimeUnit.MILLISECONDS;

    public DataManager(Context context)
    {
        this(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public DataManager(SharedPreferences preferences)
    {
        prefs = preferences;
        guard = new Semaphore(1,true);
    }

    public void setWaitingTime(long waitingTime)
    {
        this.waitingTime = waitingTime;
    }

    public SharedPreferences getSharedPreferences()
    {
        return prefs;
    }

    public interface OnValueChangeListener
    {
        void OnValueChanged(String key);
    }

    public boolean contains(String key)
    {
        return prefs.contains(key);
    }

    public void clearAll()
    {
        prefs.edit().clear().apply();
    }

    public Map<String,?> getAll()
    {
        return prefs.getAll();
    }

    public void setOnValueChangeListener(final OnValueChangeListener listener)
    {
        internListener = new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
            {
                listener.OnValueChanged(key);
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(internListener);
    }

    public void unsetOnValueChangeListener()
    {
        prefs.unregisterOnSharedPreferenceChangeListener(internListener);
    }

    public boolean putStringMap(String key,Map<String,String> data)
    {
        boolean result = false;
        try
        {
            if(result = (!TextUtils.isEmpty(key) && data != null))
            {
                ArrayList<String> keys = new ArrayList<>(data.keySet());
                if(result = putStringList(key,keys))
                {
                    for(String a : keys)
                    {
                        if(!(result = putString(key + a,data.get(a))))
                            break;
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public boolean putStringList(String key,ArrayList<String> stringList)
    {
        boolean result = false;
        boolean acquired = false;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(result = (acquired && key != null && !key.equals("") && stringList != null))
            {
                String[] array = stringList.toArray(new String[0]);
                prefs.edit().putString(key, TextUtils.join("‚‗‚",array)).apply();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(acquired)
            guard.release();
        return result;
    }

    public Map<String,String> getStringMap(String key)
    {
        Map<String,String> map = new HashMap<>();
        try
        {
            if(key != null && !key.equals(""))
            {
                ArrayList<String> keys = getStringList(key);
                for(String a : keys)
                {
                    map.put(a,getString(key+a));
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return map;
    }

    public ArrayList<String> getStringList(String key)
    {
        ArrayList<String> array;
        boolean acquired = false;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(acquired && key != null && !key.equals(""))
            {
                array = new ArrayList<String>(Arrays.asList(TextUtils.split(prefs.getString(key,""), "‚‗‚")));
            }
            else
                array = new ArrayList<>();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            array = new ArrayList<>();
        }
        if(acquired)
            guard.release();
        return array;
    }

    public boolean putString(String key, String value)
    {
        boolean result = false;
        boolean acquired = false;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(result = (acquired && key != null && !key.equals("")))
            {
                prefs.edit().putString(key,value).apply();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(acquired)
            guard.release();
        return result;
    }

    public void appendString(String key,String text)
    {
        putString(key,getString(key) + text);
    }

    public String getString(String key)
    {
        String string = "";
        boolean acquired = false;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(acquired && key != null && !key.equals(""))
            {
                string = prefs.getString(key,"");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(acquired)
            guard.release();
        return string;
    }

    public boolean putBoolean(String key, boolean value)
    {
        boolean result = false;
        boolean acquired = false;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(result = (acquired && key != null && !key.equals("")))
                prefs.edit().putBoolean(key,value).apply();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(acquired)
            guard.release();
        return result;
    }

    public boolean getBoolean(String key, boolean defaultValue)
    {
        boolean value = defaultValue,acquired = false;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(acquired && key != null && !key.equals(""))
            {
                value = prefs.getBoolean(key,defaultValue);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(acquired)
            guard.release();
        return value;
    }

    public boolean putInt(String key, int value)
    {
        boolean result = false;
        boolean acquired = false;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(acquired && key != null && !key.equals(""))
            {
                prefs.edit().putInt(key,value).apply();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(acquired)
            guard.release();
        return result;
    }

    public int getInt(String key)
    {
        int value = 0;
        boolean acquired = false;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(acquired && key != null && !key.equals(""))
            {
                value = prefs.getInt(key,0);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(acquired)
            guard.release();
        return value;
    }

    public boolean putLong(String key, long value)
    {
        boolean result = false;
        boolean acquired = false;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(result = (acquired && key != null && !key.equals("")))
            {
                prefs.edit().putLong(key,value).apply();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(acquired)
            guard.release();
        return result;
    }

    public long getLong(String key)
    {
        long value = 0;
        boolean acquired = false;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(acquired && key != null && !key.equals(""))
            {
                value = prefs.getLong(key,0);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(acquired)
            guard.release();
        return value;
    }

    public boolean putIntList(String key, ArrayList<Integer> intList)
    {
        boolean result = false;
        boolean acquired = false;
        ArrayList<String> array;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(result = (acquired && key != null && !key.equals("") && intList != null))
            {
                array = new ArrayList<>();
                for(int a : intList)
                {
                    array.add(Integer.toString(a));
                }
                result = result && putStringList(key,array);
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if(acquired)
            guard.release();
        return result;
    }

    public ArrayList<Integer> getIntList(String key)
    {
        ArrayList<Integer> array = new ArrayList<>();
        boolean acquired = false;
        try
        {
            acquired = guard.tryAcquire(waitingTime,waitingUnit);
            if(acquired && key != null && !key.equals(""))
            {
                array = new ArrayList<>();
                ArrayList<String> stringList;
                stringList = getStringList(key);
                for(String a : stringList)
                {
                    array.add(Integer.parseInt(a));
                }
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        if(acquired)
            guard.release();
        return array;
    }

    /*public enum LogLevel
    {
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        ASSERT
    }

    public String log(LogLevel level,String key,String text)
    {
        if(level == null)
            level = LogLevel.DEBUG;
        switch(level)
        {
            case VERBOSE:
            {
                Log.v(key,text);
                break;
            }
            case DEBUG:
            {
                Log.d(key,text);
                break;
            }
            case INFO:
            {
                Log.i(key,text);
                break;
            }
            case WARNING:
            {
                Log.w(key,text);
                break;
            }
            case ERROR:
            {
                Log.e(key,text);
                break;
            }
            case ASSERT:
            {
                Log.wtf(key,text);
                break;
            }
        }
        SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy, hh:mm:ss ",Locale.getDefault());
        String log = date.format(Calendar.getInstance(Locale.getDefault()).getTime());
        log += level.name().substring(0,1) + ":\n" + text + "\n\n";
        appendString(key,log);
        return log;
    }

    public void log(String key,String text)
    {
        log(LogLevel.DEBUG,key,text);
    }*/
}
