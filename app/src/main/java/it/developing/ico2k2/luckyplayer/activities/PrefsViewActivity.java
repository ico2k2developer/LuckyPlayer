package it.developing.ico2k2.luckyplayer.activities;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;
import it.developing.ico2k2.luckyplayer.adapters.base.BaseAdapter;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;

import static it.developing.ico2k2.luckyplayer.Utils.TAG_LOGS;

public class PrefsViewActivity extends BaseActivity
{
    PrefsViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs_view);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        RecyclerView list = findViewById(R.id.list);
        list.setHasFixedSize(false);
        list.setNestedScrollingEnabled(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PrefsViewAdapter(getMainSharedPreferences());
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    class PrefsViewAdapter extends BaseAdapter<PrefsViewAdapter.PrefHandle>
    {
        SharedPreferences prefs;
        ArrayList<Data> data;

        PrefsViewAdapter(SharedPreferences preferences)
        {
            Log.d(TAG_LOGS,"Creating adapter");
            prefs = preferences;
            data = new ArrayList<>(prefs.getAll().size());
            update();
        }

        class Data
        {
            String title,text;
        }

        class PrefHandle extends ViewHandle
        {
            TextView title,text;

            PrefHandle(LinearLayout parent)
            {
                super(parent);
                title = new TextView(parent.getContext());
                text = new TextView(parent.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
                title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT));
                params = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.MATCH_PARENT);
                params.weight = 1;
                text.setLayoutParams(params);
                text.setPadding(10,0,0,0);
                text.setGravity(Gravity.RIGHT);
                text.setTypeface(Typeface.MONOSPACE);
                parent.addView(title);
                parent.addView(text);
            }
        }

        void update()
        {
            Log.d(TAG_LOGS,"Updating adapter");
            Map<String,?> rawData = prefs.getAll();
            data.clear();
            data.ensureCapacity(rawData.size());
            Data d;
            Object v;
            for(String key : rawData.keySet())
            {
                d = new Data();
                v = rawData.get(key);
                d.title = "[" + v.getClass().getSimpleName() + "]" + key;
                try
                {
                    d.text = v.toString();
                }
                catch(Exception e)
                {
                    d.text = null;
                }
                data.add(d);
            }
            Collections.sort(data,new Comparator<Data>(){
                @Override
                public int compare(Data o1,Data o2){
                    return o1.title.compareTo(o2.title);
                }
            });
        }

        @Override
        public int getItemCount()
        {
            Log.d(TAG_LOGS,"Data size is " + data.size());
            return data.size();
        }

        @Override
        @NonNull
        public PrefHandle onCreateViewHolder(@NonNull ViewGroup parent,int viewType)
        {
            Log.d(TAG_LOGS,"Creating view");
            LinearLayout layout = new LinearLayout(parent.getContext());
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            Log.d(TAG_LOGS,"Created view");
            return new PrefHandle(layout);
        }

        @Override
        public void onBindViewHolder(@NonNull PrefHandle holder,int position)
        {
            Log.d(TAG_LOGS,"Binding view");
            super.onBindViewHolder(holder,position);
            Data d = data.get(position);
            holder.title.setText(d.title);
            if(d.text == null)
                holder.text.setVisibility(View.GONE);
            else
            {
                holder.text.setText(d.text);
                holder.text.setVisibility(View.VISIBLE);
            }
            Log.d(TAG_LOGS,"Bound view");
        }
    }
}
