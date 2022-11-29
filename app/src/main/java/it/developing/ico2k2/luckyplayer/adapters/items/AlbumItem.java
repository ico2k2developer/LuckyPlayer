package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Build;
import android.os.Parcel;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Collection;

public class AlbumItem extends BrowsableItem<SongItem> implements CreatedByArtistItem
{
    private final String artist;

    public AlbumItem(String title,String artist)
    {
        super(title);
        this.artist = artist;
    }

    public AlbumItem(String title,String artist,int size)
    {
        super(title,size);
        this.artist = artist;
    }

    public AlbumItem(String title, String artist,Collection<SongItem> items)
    {
        super(title,items);
        this.artist = artist;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getSubtitle()
    {
        return getArtist();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(getTitle());
        dest.writeString(getArtist());
        dest.writeParcelableArray(getItems(),0);
    }

    public static final Creator<AlbumItem> CREATOR = new Creator<AlbumItem>()
    {
        @Override
        public AlbumItem createFromParcel(Parcel in)
        {
            String title = in.readString();
            String artist = in.readString();
            MediaItem[] array;
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                array = (MediaItem[])in.readParcelableArray(loader);
            else
                array = in.readParcelableArray(loader,MediaItem.class);
            return new AlbumItem(title,artist,Arrays.asList((SongItem[])array));
        }

        @Override
        public AlbumItem[] newArray(int size)
        {
            return new AlbumItem[size];
        }
    };
}
