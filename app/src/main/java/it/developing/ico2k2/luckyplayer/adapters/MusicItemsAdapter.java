package it.developing.ico2k2.luckyplayer.adapters;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.adapters.base.BaseAdapter;
import it.developing.ico2k2.luckyplayer.adapters.items.MusicItem;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;

public class MusicItemsAdapter extends BaseAdapter<MusicItemsAdapter.MusicItemHandle>
{
    private ArrayList<MusicItem> items;
    private ArrayList<Integer> indexes;
    private boolean showIndexes = false;
    private OrderType order = OrderType.NONE;

    public enum OrderType
    {
        NONE,
        ALPHABETICAL,
        TRACKN,
    }

    public MusicItemsAdapter()
    {
        items = new ArrayList<>();
    }

    public MusicItemsAdapter(int size)
    {
        items = new ArrayList<>(size);
    }

    public void setOrder(OrderType orderType)
    {
        order = orderType;
        if(order == OrderType.NONE)
        {
            if(indexes != null)
                indexes.clear();
        }
        else
        {
            if(indexes == null)
                indexes = new ArrayList<>(getItemCount());
            else
            {
                indexes.clear();
                indexes.ensureCapacity(getItemCount());
            }
            int i = 0;
            for(MusicItem ignored : items)
            {
                indexes.add(i);
                i++;
            }
        }
    }

    @CallSuper
    public void ensureCapacity(int size)
    {
        items.ensureCapacity(size);
        if(order != OrderType.NONE)
            indexes.ensureCapacity(size);
    }

    public void addAll(Collection<? extends MusicItem> collection)
    {
        if(order != OrderType.NONE)
        {
            int i = getItemCount();
            for(MusicItem ignored : collection)
            {
                indexes.add(i);
                i++;
            }
        }
        items.addAll(collection);
    }

    public void add(MusicItem item)
    {
        if(order != OrderType.NONE)
        {
            indexes.add(getItemCount());
        }
        items.add(item);
    }

    public MusicItem get(int index)
    {
        if(order != OrderType.NONE)
            index = indexes.get(index);
        return items.get(index);
    }

    public void clear()
    {
        if(order != OrderType.NONE)
            indexes.clear();
        items.clear();
    }

    public void remove(MusicItem item)
    {
        remove(items.indexOf(item));
    }

    public void remove(int index)
    {
        if(order != OrderType.NONE)
            indexes.remove((Integer)index);
        items.remove(index);
    }

    public void setShowIndexes(boolean show)
    {
        showIndexes = show;
    }

    public boolean getShowIndexes()
    {
        return showIndexes;
    }

    @Override
    @NonNull
    public MusicItemHandle onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout)LayoutInflater.from(
                parent.getContext()).inflate(R.layout.list_item_song,
                parent,
                false);
        return new MusicItemHandle(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicItemHandle holder,int position)
    {
        super.onBindViewHolder(holder,position);
        MusicItem item = get(position);
        Bundle extras = item.getDescription().getExtras();
        if(showIndexes)
        {
            holder.index.setVisibility(View.VISIBLE);
            holder.index.setText(extras.getInt(MediaStore.Audio.AudioColumns.TRACK));
        }
        else
            holder.index.setVisibility(View.GONE);
        holder.title.setText(item.getDescription().getTitle());
        String description = item.getTextDescription();
        if(TextUtils.isEmpty(description))
            holder.description.setVisibility(View.GONE);
        else
        {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(description);
        }
        holder.description.setText(item.getTextDescription());
        holder.time.setText(item.getTimeDescription());
        holder.time.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    static class MusicItemHandle extends ViewHandle{
        // each data item is just a string in this case
        AppCompatTextView title,description,time,index;
        //ImageView cover;

        MusicItemHandle(ConstraintLayout layout)
        {
            super(layout);
            title = layout.findViewById(R.id.itemTitle);
            description = layout.findViewById(R.id.itemDescription);
            time = layout.findViewById(R.id.itemDescription2);
            index = layout.findViewById(R.id.itemN);
            //cover = layout.findViewById(R.id.itemIcon);
        }
    }

    public void reorder()
    {
        switch(order)
        {
            case ALPHABETICAL:
            {
                Collections.sort(indexes,new Comparator<Integer>(){
                    @Override
                    public int compare(Integer o1,Integer o2){
                        return items.get(o1).getDescription().getTitle().toString().compareTo(
                                items.get(o2).getDescription().getTitle().toString());
                    }
                });
                break;
            }
            case TRACKN:
            {
                Collections.sort(indexes,new Comparator<Integer>(){
                    @Override
                    public int compare(Integer o1,Integer o2){
                        return items.get(o1).getDescription().getExtras()
                                .getString(MediaStore.Audio.AudioColumns.TRACK,"0")
                                .compareTo(items.get(o2).getDescription().getExtras()
                                .getString(MediaStore.Audio.AudioColumns.TRACK,"0"));
                    }
                });
                break;
            }
            case NONE:
            {
                int i = 0;
                for(MusicItem ignored : items)
                {
                    indexes.set(i,i);
                    i++;
                }
                break;
            }
        }
    }
}