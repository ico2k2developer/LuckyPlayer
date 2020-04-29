package it.developing.ico2k2.luckyplayer.adapters;

import android.os.Bundle;
import android.provider.MediaStore;
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
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;

public class SongsAdapter extends BaseAdapter<SongsAdapter.SongHandle>
{
    private ArrayList<Song> songs;
    private ArrayList<Integer> indexes;
    private boolean showIndexes = false;
    private OrderType order = OrderType.NONE;

    public enum OrderType
    {
        NONE,
        ALPHABETICAL,
        TRACKN,
    }

    public SongsAdapter()
    {
        songs = new ArrayList<>();
    }

    public SongsAdapter(int size)
    {
        songs = new ArrayList<>(size);
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
            for(Song ignored : songs)
            {
                indexes.add(i);
                i++;
            }
        }
    }

    @CallSuper
    public void ensureCapacity(int size)
    {
        songs.ensureCapacity(size);
        if(order != OrderType.NONE)
            indexes.ensureCapacity(size);
    }

    public void addAll(Collection<? extends Song> collection)
    {
        if(order != OrderType.NONE)
        {
            int i = getItemCount();
            for(Song ignored : collection)
            {
                indexes.add(i);
                i++;
            }
        }
        songs.addAll(collection);
    }

    public void add(Song song)
    {
        if(order != OrderType.NONE)
        {
            indexes.add(getItemCount());
        }
        songs.add(song);
    }

    public Song get(int index)
    {
        if(order != OrderType.NONE)
            index = indexes.get(index);
        return songs.get(index);
    }

    public void clear()
    {
        if(order != OrderType.NONE)
            indexes.clear();
        songs.clear();
    }

    public void remove(Song song)
    {
        remove(songs.indexOf(song));
    }

    public void remove(int index)
    {
        if(order != OrderType.NONE)
            indexes.remove((Integer)index);
        songs.remove(index);
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
    public SongHandle onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout)LayoutInflater.from(
                parent.getContext()).inflate(R.layout.list_item_song,
                parent,
                false);
        return new SongHandle(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SongHandle holder,int position)
    {
        super.onBindViewHolder(holder,position);
        Song song = get(position);
        Bundle extras = song.getDescription().getExtras();
        if(showIndexes)
        {
            holder.index.setVisibility(View.VISIBLE);
            holder.index.setText(extras.getInt(MediaStore.Audio.AudioColumns.TRACK));
        }
        else
            holder.index.setVisibility(View.GONE);
        holder.title.setText(song.getDescription().getTitle());
        holder.description.setText(song.getDescription().getSubtitle());
        holder.time.setText(Song.getSongTimeDescription(extras.getLong(MediaStore.MediaColumns.DURATION)));
        holder.time.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount()
    {
        return songs.size();
    }

    static class SongHandle extends ViewHandle{
        // each data item is just a string in this case
        AppCompatTextView title,description,time,index;
        //ImageView cover;

        SongHandle(ConstraintLayout layout)
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
                        return songs.get(o1).getDescription().getTitle().toString().compareTo(
                                songs.get(o2).getDescription().getTitle().toString());
                    }
                });
                break;
            }
            case TRACKN:
            {
                Collections.sort(indexes,new Comparator<Integer>(){
                    @Override
                    public int compare(Integer o1,Integer o2){
                        return songs.get(o1).getDescription().getExtras()
                                .getString(MediaStore.Audio.AudioColumns.TRACK,"0")
                                .compareTo(songs.get(o2).getDescription().getExtras()
                                .getString(MediaStore.Audio.AudioColumns.TRACK,"0"));
                    }
                });
                break;
            }
            case NONE:
            {
                int i = 0;
                for(Song ignored : songs)
                {
                    indexes.set(i,i);
                    i++;
                }
                break;
            }
        }
    }

    /*public interface OnSongClickListener
    {
        void onSongClick(SongHandle songHandle,int position);
    }

    public interface OnSongLongClickListener
    {
        boolean onSongLongClick(SongHandle songHandle,int position);
    }

    public interface OnContextMenuListener
    {
        void onContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo,int position);
    }*/
}