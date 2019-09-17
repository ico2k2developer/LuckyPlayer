package it.developing.ico2k2.luckyplayer.adapters.base;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;

public abstract class BaseAdapter<VH extends ViewHandle> extends RecyclerView.Adapter<VH>
{
    private ViewHandle.OnItemClickListener clickListener;
    private ViewHandle.OnItemLongClickListener longClickListener;
    private ViewHandle.OnItemContextMenuListener contextListener;

    @Override
    @CallSuper
    public void onBindViewHolder(@NonNull VH holder,int position)
    {
        holder.setOnClickListener(clickListener,position);
        holder.setOnLongClickListener(longClickListener,position);
        holder.setOnContextMenuListener(contextListener,position);
    }

    public void setOnItemClickListener(final ViewHandle.OnItemClickListener listener)
    {
        clickListener = listener;
    }

    public void setOnItemLongClickListener(final ViewHandle.OnItemLongClickListener listener)
    {
        longClickListener = listener;
    }

    public void setOnItemContextMenuListener(final ViewHandle.OnItemContextMenuListener listener)
    {
        contextListener = listener;
    }
}
