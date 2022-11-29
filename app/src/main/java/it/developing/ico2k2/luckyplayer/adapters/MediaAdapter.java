package it.developing.ico2k2.luckyplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.LayoutInflaterCompat;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Collection;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.adapters.items.MediaItem;

public class MediaAdapter<M extends MediaItem,H extends MediaAdapter.MediaItemHandle>
        extends BaseAdapter<H>
{
    protected static class MediaItemHandle extends ViewHandle
    {
        private final TextView title,subtitle;

        protected MediaItemHandle(View view)
        {
            super(view);
            title = view.findViewById(R.id.song_title);
            subtitle = view.findViewById(R.id.song_subtitle);
        }

        protected TextView getTitleView()
        {
            return title;
        }

        protected TextView getSubtitleView()
        {
            return subtitle;
        }
    }

    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return (H)new MediaItemHandle(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_song,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull H holder,int position)
    {
        super.onBindViewHolder(holder,position);
        M item = elements.get(position);
        holder.getTitleView().setText(item.getTitle());
        holder.getSubtitleView().setText(item.getSubtitle());
    }

    private final ArrayList<M> elements;

    public MediaAdapter()
    {
        elements = new ArrayList<>();
    }

    public MediaAdapter(int size)
    {
        elements = new ArrayList<>(size);
    }

    public MediaAdapter(Collection<M> items)
    {
        elements = new ArrayList<>(items);
    }

    @Override
    public int getItemCount()
    {
        return elements.size();
    }

    public void add(M item)
    {
        elements.add(item);
    }

    public void addAll(Collection<? extends M> items)
    {
        elements.addAll(items);
    }

    public void removeAll()
    {
        elements.clear();
    }
}
