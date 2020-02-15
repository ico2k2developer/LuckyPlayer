package it.developing.ico2k2.luckyplayer.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.adapters.base.BaseAdapter;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;
import it.developing.ico2k2.luckyplayer.services.PlayService;
import it.developing.ico2k2.luckyplayer.services.PlayService.OrderType;

import static android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE;
import static it.developing.ico2k2.luckyplayer.Keys.TAG_LOGS;

public class SongsAdapter extends BaseAdapter<SongsAdapter.SongHandle>
{
    public static final String DESCRIPTION_FORMAT = "%s1, %s2";

    public static final String TIME_FORMAT_MS = "%s1 ms";
    public static final String TIME_FORMAT_SEC = "%s1 s";
    public static final String TIME_FORMAT_MIN = "%s1:%s2";
    public static final String TIME_FORMAT_HOUR = "%s1:%s2:%s3";

    private Context context;
    private ArrayList<Song> songs;
    private ArrayList<Integer> indexes;
    private PlayService.OrderType order = PlayService.OrderType.NONE;
    private ViewType view = ViewType.SONGS;
    private boolean showIndexes = false;

    public enum ViewType
    {
        SONGS,
        ALBUMS,
        ARTISTS,
        YEARS,
    }

    public static void trimToAlbums(List<Song> songs,List<Integer> indexes)
    {
        ArrayList<String> albums = new ArrayList<>(songs.size());
        String album;
        int a = 0;
        for(SongsAdapter.Song song : songs)
        {
            album = song.getAlbum();
            if(!albums.contains(album))
            {
                albums.add(album);
                indexes.add(a);
            }
            a++;
        }
        albums.clear();
        albums.trimToSize();
    }

    public static void trimToArtists(List<Song> songs,List<Integer> indexes)
    {
        ArrayList<String> artists = new ArrayList<>(songs.size());
        String artist;
        int a = 0;
        for(SongsAdapter.Song song : songs)
        {
            artist = song.getArtist();
            if(!artists.contains(artist))
            {
                artists.add(artist);
                indexes.add(a);
            }
            a++;
        }
        artists.clear();
        artists.trimToSize();
    }

    public static void trimToYears(List<Song> songs,List<Integer> indexes)
    {
        ArrayList<Integer> years = new ArrayList<>(songs.size());
        int year,a = 0;
        for(SongsAdapter.Song song : songs)
        {
            year = song.getYear();
            if(!years.contains(year))
            {
                years.add(year);
                indexes.add(a);
            }
            a++;
        }
        years.clear();
        years.trimToSize();
    }

    public static String getAlbumDescription(Context context,List<Song> songs,List<Integer> indexes,int position)
    {
        int songsCount = 0;
        String album = songs.get(indexes.get(position)).getAlbum();
        for(Song song : songs)
        {
            if(song.getAlbum().equals(album))
                songsCount++;
        }
        return context.getString(R.string.description_album)
                .replace("%s1",songs.get(indexes.get(position)).getArtist())
                .replace("%s2",Integer.toString(songsCount));
    }

    public static String getArtistDescription(Context context,List<Song> songs,List<Integer> indexes,int position)
    {
        int songsCount = 0;
        ArrayList<String> albums = new ArrayList<>(songs.size());
        String result,artist = songs.get(indexes.get(position)).getArtist();
        for(Song song : songs)
        {
            if(song.getArtist().equals(artist))
            {
                if(!albums.contains(song.getAlbum()))
                    albums.add(song.getAlbum());
                songsCount++;
            }
        }
        result = context.getString(R.string.description_artist)
                .replace("%s1",Integer.toString(albums.size()))
                .replace("%s2",Integer.toString(songsCount));
        albums.clear();
        albums.trimToSize();
        return result;
    }

    public static String getYearDescription(Context context,List<Song> songs,List<Integer> indexes,int position)
    {
        int songsCount = 0;
        ArrayList<String> albums = new ArrayList<>(songs.size());
        ArrayList<String> artists = new ArrayList<>(songs.size());
        String result;
        int year = songs.get(indexes.get(position)).getYear();
        for(Song song : songs)
        {
            if(song.getYear() == year)
            {
                if(!albums.contains(song.getAlbum()))
                    albums.add(song.getAlbum());
                if(!artists.contains(song.getArtist()))
                    artists.add(song.getArtist());
                songsCount++;
            }
        }
        result = context.getString(R.string.description_year)
                .replace("%s1",Integer.toString(artists.size()))
                .replace("%s2",Integer.toString(albums.size()))
                .replace("%s3",Integer.toString(songsCount));
        albums.clear();
        albums.trimToSize();
        artists.clear();
        artists.trimToSize();
        return result;
    }

    public SongsAdapter(Context context)
    {
        this.context = context;
        songs = new ArrayList<>();
        indexes = new ArrayList<>();
    }

    public SongsAdapter(Context context,int size)
    {
        this.context = context;
        songs = new ArrayList<>(size);
        indexes = new ArrayList<>(size);
    }

    @CallSuper
    public void ensureCapacity(int size)
    {
        songs.ensureCapacity(size);
        indexes.ensureCapacity(size);
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
        if(order == PlayService.OrderType.NONE && view == ViewType.SONGS)
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

    public void setOrderType(PlayService.OrderType newOrder)
    {
        order = newOrder;
        if(order == OrderType.NONE && view == ViewType.SONGS)
        {
            indexes.clear();
            indexes.trimToSize();
        }
    }

    public void setViewType(ViewType newView)
    {
        view = newView;
    }

    public OrderType getOrderType()
    {
        return order;
    }

    public ViewType getViewType()
    {
        return view;
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
        switch(view)
        {
            case SONGS:
            {

                if(order != OrderType.NONE)
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
                                    return songs.get(o1).getTitle().toString().compareTo(songs.get(o2).getTitle().toString());
                                }
                            });
                            break;
                        }
                        case INDEX:
                        {
                            Collections.sort(indexes,new Comparator<Integer>(){
                                @Override
                                public int compare(Integer o1,Integer o2){
                                    return songs.get(o1).getTrackN() - songs.get(o2).getTrackN();
                                }
                            });
                            break;
                        }
                    }
                }
                break;
            }
            case ALBUMS:
            {
                trimToAlbums(songs,indexes);
                if(order == PlayService.OrderType.ALPHABETICAL)
                {
                    Collections.sort(indexes,new Comparator<Integer>(){
                        @Override
                        public int compare(Integer o1,Integer o2){
                            return songs.get(o1).getAlbum().compareTo(songs.get(o2).getAlbum());
                        }
                    });
                }
                break;
            }
            case ARTISTS:
            {
                trimToArtists(songs,indexes);
                if(order == PlayService.OrderType.ALPHABETICAL)
                {
                    Collections.sort(indexes,new Comparator<Integer>(){
                        @Override
                        public int compare(Integer o1,Integer o2){
                            return songs.get(o1).getArtist().compareTo(songs.get(o2).getArtist());
                        }
                    });
                }
                break;
            }
            case YEARS:
            {
                trimToYears(songs,indexes);
                if(order == PlayService.OrderType.ALPHABETICAL)
                {
                    Collections.sort(indexes,new Comparator<Integer>(){
                        @Override
                        public int compare(Integer o1,Integer o2){
                            return songs.get(o1).getYear() - songs.get(o2).getYear();
                        }
                    });
                }
                break;
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
        Song song = get(position);
        if(showIndexes)
        {
            holder.index.setVisibility(View.VISIBLE);
            holder.index.setText(Integer.toString(song.getTrackN()));
        }
        else
            holder.index.setVisibility(View.GONE);
        switch(view)
        {
            case ALBUMS:
            {
                holder.title.setText(song.getAlbum());
                holder.description.setText(getAlbumDescription(context,songs,indexes,position));
                holder.time.setVisibility(View.GONE);
                break;
            }
            case ARTISTS:
            {
                holder.title.setText(song.getArtist());
                holder.description.setText(getArtistDescription(context,songs,indexes,position));
                holder.time.setVisibility(View.GONE);
                break;
            }
            case YEARS:
            {
                holder.title.setText(Integer.toString(song.getYear()));
                holder.description.setText(getYearDescription(context,songs,indexes,position));
                holder.time.setVisibility(View.GONE);
                break;
            }
            default:
            {
                holder.title.setText(song.getTitle());
                holder.description.setText(song.getSongDescription());
                holder.time.setText(song.getSongDurationDescription());
                holder.time.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public int getItemCount()
    {
        int size;
        if(view == ViewType.SONGS)
            size = songs.size();
        else
            size = indexes.size();
        return size;
    }

    public class SongHandle extends ViewHandle{
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
        private static final String KEY_ARTIST = "artist";
        private static final String KEY_ALBUM = "album";
        private static final String KEY_TRACKN = "trackN";
        private static final String KEY_DURATION = "duration";
        private static final String KEY_YEAR = "year";
        private static final String KEY_ID = "id";

        private MediaBrowserCompat.MediaItem item;

        public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>()
        {
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

        }

        public Song(Parcel parcel,ClassLoader classLoader)
        {
            item = MediaBrowserCompat.MediaItem.CREATOR.createFromParcel(parcel);
            item.getDescription().getExtras().setClassLoader(classLoader);
        }

        public Song(Song song)
        {
            item = new MediaBrowserCompat.MediaItem(song.getDescription(),song.getFlags());
        }

        public Song(MediaDescriptionCompat description,int flags)
        {
            item = createFromDescription(description,flags);
        }


        public Song(MediaDescriptionCompat description)
        {
            item = createFromDescription(description,FLAG_PLAYABLE);
        }

        private static MediaBrowserCompat.MediaItem createFromDescription(MediaDescriptionCompat description,int flags)
        {
            Log.d(TAG_LOGS,"Media id: " + description.getMediaId());
            return new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                            .setDescription(description.getDescription())
                            .setExtras(description.getExtras())
                            .setIconBitmap(description.getIconBitmap())
                            .setIconUri(description.getIconUri())
                            .setMediaId(description.getMediaId())
                            .setMediaUri(description.getMediaUri())
                            .setSubtitle(description.getSubtitle())
                            .setTitle(description.getTitle()).build(),flags);
        }

        public Song(MediaBrowserCompat.MediaItem newItem)
        {
            if(newItem.getDescription().getExtras() == null)
                this.item = createFromDescription(newItem.getDescription(),newItem.getFlags());
            else
                this.item = newItem;
        }

        public Song(String path,CharSequence title,int flags)
        {
            item = new MediaBrowserCompat.MediaItem(
                    new MediaDescriptionCompat.Builder()
                    .setMediaId(path)
                    .setTitle(title)
                    .setExtras(new Bundle())
                    .build(),flags);
        }

        public Song(String path,CharSequence title)
        {
            this(path,title,FLAG_PLAYABLE);
        }

        public String getMediaId()
        {
            return item.getDescription().getMediaId();
        }

        public CharSequence getTitle(){
            return item.getDescription().getTitle();
        }

        public String getPath(){
            return item.getMediaId();
        }

        public Song setId(int id){
            item.getDescription().getExtras().putInt(KEY_ID,id);
            return this;
        }

        public int getId(){
            return item.getDescription().getExtras().getInt(KEY_ID,-1);
        }

        public Song setArtist(String artist){
            item.getDescription().getExtras().putString(KEY_ARTIST,artist);
            return this;
        }

        public String getArtist(){
            return item.getDescription().getExtras().getString(KEY_ARTIST,"");
        }

        public Song setAlbum(String album){
            item.getDescription().getExtras().putString(KEY_ALBUM,album);
            return this;
        }

        public String getAlbum(){
            return item.getDescription().getExtras().getString(KEY_ALBUM,"");
        }

        public Song setTrackN(int trackN){
            item.getDescription().getExtras().putInt(KEY_TRACKN,trackN);
            return this;
        }

        public int getTrackN(){
            return item.getDescription().getExtras().getInt(KEY_TRACKN);
        }

        public Song setDuration(long time){
            item.getDescription().getExtras().putLong(KEY_DURATION,time);
            return this;
        }

        public long getDuration(){
            return item.getDescription().getExtras().getLong(KEY_DURATION);
        }

        public Song setYear(int year){
            item.getDescription().getExtras().putInt(KEY_YEAR,year);
            return this;
        }

        public int getYear(){
            return item.getDescription().getExtras().getInt(KEY_YEAR);
        }

        public String getSongDescription()
        {
            return SongsAdapter.getSongDescription(getAlbum(),getArtist());
        }

        public String getSongDurationDescription()
        {
            return SongsAdapter.getSongTimeDescription(getDuration());
        }

        public MediaDescriptionCompat getDescription()
        {
            return item.getDescription();
        }

        public int getFlags()
        {
            return item.getFlags();
        }

        public MediaBrowserCompat.MediaItem toMediaItem()
        {
            return item;
        }

        @Override
        public boolean equals(Object song)
        {
            boolean result;
            if(song instanceof Song)
                result = getMediaId().equals(((Song)song).getMediaId());
            else
                result = super.equals(song);
            return result;
        }
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