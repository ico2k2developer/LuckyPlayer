package it.developing.ico2k2.luckyplayer.adapters.lib;

import android.view.ContextMenu;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHandle extends RecyclerView.ViewHolder
{
    public ViewHandle(View view)
    {
        super(view);
    }

    public void setOnClickListener(final OnItemClickListener listener,final int position)
    {
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(listener != null)
                    listener.onItemClick(ViewHandle.this,position);
            }
        });
    }

    public void setOnLongClickListener(final OnItemLongClickListener listener,final int position)
    {
        itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                boolean result = false;
                if(listener != null)
                    result = listener.onItemLongClick(ViewHandle.this,position);
                return result;
            }
        });
    }

    public void setOnContextMenuListener(final OnItemContextMenuListener listener,final int position)
    {
        itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
            @Override
            public void onCreateContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo){
                if(listener != null)
                    listener.onContextMenu(menu,v,menuInfo,position);
            }
        });
    }

    public interface OnItemClickListener
    {
        void onItemClick(ViewHandle handle,int position);
    }

    public interface OnItemLongClickListener
    {
        boolean onItemLongClick(ViewHandle handle,int position);
    }

    public interface OnItemContextMenuListener
    {
        void onContextMenu(ContextMenu menu,View v,ContextMenu.ContextMenuInfo menuInfo,int position);
    }
}
