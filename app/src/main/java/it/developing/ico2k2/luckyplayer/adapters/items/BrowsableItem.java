package it.developing.ico2k2.luckyplayer.adapters.items;

import java.util.ArrayList;
import java.util.Collection;

public abstract class BrowsableItem<M extends MediaItem> extends MediaItem
{
    private final ArrayList<M> children;

    public BrowsableItem(String title)
    {
        super(title);
        children = new ArrayList<>();
    }

    public BrowsableItem(String title,Collection<M> items)
    {
        super(title);
        children = new ArrayList<>(items);
    }

    public BrowsableItem(String title,int size)
    {
        super(title);
        children = new ArrayList<>(size);
    }

    public int getCount()
    {
        return children.size();
    }

    @Override
    public boolean isBrowsable()
    {
        return true;
    }

    @Override
    public boolean isPlayable()
    {
        return false;
    }

    public MediaItem[] getItems()
    {
        return children.toArray(new MediaItem[0]);
    }
}
