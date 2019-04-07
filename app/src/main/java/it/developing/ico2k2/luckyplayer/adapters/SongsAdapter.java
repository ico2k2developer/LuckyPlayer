package it.developing.ico2k2.luckyplayer.adapters;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.widget.AppCompatTextView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.adapters.base.BaseAdapter;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;

public class SongsAdapter extends BaseAdapter<SongsAdapter.SongHandle>
{
    public static final String DESCRIPTION_FORMAT = "%s1, %s2";

    public static final String TIME_FORMAT_MS = "%s1 ms";
    public static final String TIME_FORMAT_SEC = "%s1 s";
    public static final String TIME_FORMAT_MIN = "%s1:%s2";
    public static final String TIME_FORMAT_HOUR = "%s1:%s2:%s3";

    private ArrayList<Song> songs;
    private ArrayList<Integer> indexes;
    private Ordering order = Ordering.NO_ORDER;
    private boolean showIndexes = false;

    public SongsAdapter()
    {
        songs = new ArrayList<>();
        indexes = new ArrayList<>();
    }

    public SongsAdapter(int size)
    {
        songs = new ArrayList<>(size);
        indexes = new ArrayList<>(size);
    }

    public void addAll(Collection<? extends Song> collection)
    {
        songs.addAll(collection);
    }

    public void add(Song song)
    {
        songs.add(song);
    }

    public Song get(int index)
    {
        Song result;
        if(order == Ordering.NO_ORDER)
            result = songs.get(index);
        else
            result = songs.get(indexes.get(index));
        return result;
    }

    public void clear()
    {
        songs.clear();
    }

    public void remove(Song song)
    {
        songs.remove(song);
    }

    public void remove(int index)
    {
        songs.remove(index);
    }

    public void setOrder(Ordering newOrder)
    {
        order = newOrder;
        if(order == Ordering.NO_ORDER)
            indexes.clear();
    }

    public Ordering getOrder()
    {
        return order;
    }

    public void setShowIndexes(boolean show)
    {
        showIndexes = show;
    }

    public boolean getShowIndexes()
    {
        return showIndexes;
    }

    public void reorder()
    {
        indexes.clear();
        if(order != Ordering.NO_ORDER)
        {
            int a;
            for(a = 0; a < songs.size(); a++)
                indexes.add(a,a);
            switch(order)
            {
                case ALPHABETICAL:
                {
                    Collections.sort(indexes,new Comparator<Integer>(){
                        @Override
                        public int compare(Integer o1,Integer o2){
                            return songs.get(o1).title.compareTo(songs.get(o2).title);
                        }
                    });
                    break;
                }
                case INDEXES:
                {
                    Collections.sort(indexes,new Comparator<Integer>(){
                        @Override
                        public int compare(Integer o1,Integer o2){
                            return songs.get(o1).index - songs.get(o2).index;
                        }
                    });
                    break;
                }
            }
        }
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
        Song song;
        if(order == Ordering.NO_ORDER)
            song = songs.get(position);
        else
            song = songs.get(indexes.get(position));
        if(showIndexes)
        {
            holder.index.setVisibility(View.VISIBLE);
            holder.index.setText(Integer.toString(song.index));
        }
        else
            holder.index.setVisibility(View.GONE);
        holder.title.setText(song.title);
        holder.description.setText(song.getDescription());
        holder.time.setText(song.getTimeDescription());

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongHandle extends ViewHandle{
        // each data item is just a string in this case
        AppCompatTextView title,description,time,index;
        ImageView cover;

        SongHandle(ConstraintLayout layout)
        {
            super(layout);
            title = layout.findViewById(R.id.itemTitle);
            description = layout.findViewById(R.id.itemDescription);
            time = layout.findViewById(R.id.itemDescription2);
            index = layout.findViewById(R.id.itemN);
            cover = layout.findViewById(R.id.itemIcon);
        }
    }

    public static class Song implements Parcelable
    {
        public static final String PATH_KEY = "path";
        public static final String INDEX_KEY = "track number";
        public static final String TITLE_KEY = "title";
        public static final String ARTIST_KEY = "artist";
        //public static final String ALBUMARTIST_KEY = "albumArtist";
        public static final String ALBUM_KEY = "album";
        //public static final String ALBUMART_KEY = "albumart";
        //public static final String YEAR_KEY = "year";
        public static final String TIME_KEY = "ms";

        private String path,title,artist,album;
        private int index;
        private long time;

        public final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>(){
            public Song createFromParcel(Parcel in){
                return new Song(in,getClass().getClassLoader());
            }

            public Song[] newArray(int size)
            {
                return new Song[size];
            }
        };

        public int describeContents()
        {
            return 0;
        }

        public void writeToParcel(Parcel dest,int flags)
        {
            Bundle bundle = new Bundle();
            bundle.putString(PATH_KEY,path);
            bundle.putString(TITLE_KEY,title);
            bundle.putString(ARTIST_KEY,artist);
            //bundle.putString(ALBUMARTIST_KEY,albumartist);
            bundle.putString(ALBUM_KEY,album);
            bundle.putInt(INDEX_KEY,index);
            //bundle.putInt(YEAR_KEY,year);
            bundle.putLong(TIME_KEY,time);
            //bundle.putInt(LISTINDEX_KEY,listIndex);
            dest.writeBundle(bundle);
        }

        public Song(Song song)
        {
            path = song.path;
            title = song.title;
            album = song.album;
            artist = song.artist;
            index = song.index;
            time = song.time;
        }

        public Song(Parcel parcel,ClassLoader classLoader)
        {
            this();
            Bundle bundle = parcel.readBundle(classLoader);
            bundle.setClassLoader(classLoader);
            path = bundle.getString(PATH_KEY);
            title = bundle.getString(TITLE_KEY);
            artist = bundle.getString(ARTIST_KEY);
            //albumartist = bundle.getString(ALBUMARTIST_KEY);
            album = bundle.getString(ALBUM_KEY);
            index = bundle.getInt(INDEX_KEY);
            //year = bundle.getInt(YEAR_KEY);
            time = bundle.getLong(TIME_KEY);
            //listIndex = bundle.getInt(LISTINDEX_KEY);
        }

        public Song(String path)
        {
            this.path = path;
        }

        private Song()
        {

        }

        public String getPath()
        {
            return path;
        }

        public Song setTitle(String title){
            this.title = title;
            return this;
        }

        public String getTitle(){
            return title;
        }

        public Song setArtist(String artist){
            this.artist = artist;
            return this;
        }

        public String getArtist(){
            return artist;
        }

        public Song setAlbum(String album){
            this.album = album;
            return this;
        }

        public String getAlbum(){
            return album;
        }

        public Song setIndex(int index){
            this.index = index;
            return this;
        }

        public int getIndex(){
            return index;
        }

        public Song setTime(long time){
            this.time = time;
            return this;
        }

        public long getTime(){
            return time;
        }

        public String getDescription()
        {
            return getSongDescription(album,artist);
        }

        public String getTimeDescription()
        {
            return getSongTimeDescription(time);
        }

        @Override
        public boolean equals(Object song)
        {
            boolean result;
            if(song instanceof Song)
                result = path.equals(((Song)song).getPath());
            else
                result = super.equals(song);
            return result;
        }
    }

    public enum Ordering
    {
        ALPHABETICAL,
        INDEXES,
        NO_ORDER,
    }

    public interface OnSongClickListener
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
    }

    public static String getSongTimeDescription(long ms)
    {
        String result;
        if(ms < 1000)
            result = TIME_FORMAT_MS.replace("%s1",Long.toString(ms));
        else if(ms < 60000)
            result = TIME_FORMAT_SEC.replace("%s1",Long.toString(ms / 1000));
        else if(ms < 3600000)
        {
            int min = (int)(ms / 1000 / 60);
            int sec = (int)(ms / 1000 - min * 60);
            result = TIME_FORMAT_MIN
                    .replace("%s1",Integer.toString(min))
                    .replace("%s2",Integer.toString(sec));
        }
        else
        {
            int hour = (int)(ms / 1000 / 60 / 24);
            int min = (int)(ms / 1000 / 60 - hour * 60);
            int sec = (int)(ms / 1000 - hour * 60 * 60 - min * 60);
            result = TIME_FORMAT_HOUR
                    .replace("%s1",Integer.toString(hour))
                    .replace("%s2",Integer.toString(min))
                    .replace("%s3",Integer.toString(sec));
        }
        return result;
    }

    public static String getSongDescription(String album,String artist)
    {
        return DESCRIPTION_FORMAT.replace("%s1",album).replace("%s2",artist);
    }
}