package it.developing.ico2k2.luckyplayer.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.developing.ico2k2.luckyplayer.R;

public final class Utils
{
    public static int getThemeFromName(String name) throws NoSuchFieldException,IllegalAccessException
    {
        name = name.replace(" ","_");
        return R.style.class.getField(name).getInt(R.style.class);
    }



    public static ArrayList<Map<String,String>> adapterMapsFromAdapterList(ArrayList<String> formats,String listTitle)
    {
        ArrayList<Map<String,String>> result = new ArrayList<>(formats.size());
        for(String a : formats)
        {
            Map<String,String> map = new HashMap<>();
            map.put(listTitle,a);
            result.add(map);
        }
        return result;
    }
}
