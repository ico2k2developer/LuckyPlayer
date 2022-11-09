package it.developing.ico2k2.luckyplayer.preference;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PreferenceManager implements SharedPreferences
{
    private static final Map<String, PreferenceManager> map = new HashMap<>();

    private final SharedPreferences prefs;

    private PreferenceManager(Context context, String name)
    {
        prefs = context.getApplicationContext().getSharedPreferences(name,Context.MODE_PRIVATE);
    }

    public synchronized static void removeInstance(String name)
    {
        map.remove(name);
    }

    public synchronized static PreferenceManager getInstance(Context context, String name)
    {
        PreferenceManager result;
        if(map.containsKey(name))
            result = map.get(name);
        else
        {
            result = new PreferenceManager(context,name);
            map.put(name,result);
        }
        return result;
    }

    @Override
    public Map<String, ?> getAll() {
        return prefs.getAll();
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        return prefs.getString(key,defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return prefs.getStringSet(key,defValues);
    }

    @Override
    public int getInt(String key, int defValue) {
        return prefs.getInt(key,defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return prefs.getLong(key,defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return prefs.getFloat(key,defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return prefs.getBoolean(key,defValue);
    }

    @Nullable
    public String getString(String key) {
        return getString(key,null);
    }

    @Nullable
    public Set<String> getStringSet(String key) {
        return getStringSet(key,null);
    }

    public int getInt(String key) {
        return getInt(key,0);
    }

    public long getLong(String key) {
        return getLong(key,0);
    }

    public float getFloat(String key) {
        return getFloat(key,0);
    }

    @Override
    public boolean contains(String key) {
        return prefs.contains(key);
    }

    @Override
    public Editor edit() {
        return prefs.edit();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}