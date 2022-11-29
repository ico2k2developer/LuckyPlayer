package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Build;
import android.os.Parcel;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Collection;

public class ArtistItem extends BrowsableItem<SongItem> implements CreatedByArtistItem
{
    public ArtistItem(String artist)
    {
        super(artist);
    }

    public ArtistItem(String artist,int size)
    {
        super(artist,size);
    }

    public ArtistItem(String artist, Collection<SongItem> items)
    {
        super(artist,items);
    }

    @Override
    public String getArtist()
    {
        return getTitle();
    }

    @Override
    public String getSubtitle() {
        return getArtist();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(getArtist());
        dest.writeParcelableArray(getItems(),0);
    }

    public static final Creator<ArtistItem> CREATOR = new Creator<ArtistItem>()
    {
        @Override
        public ArtistItem createFromParcel(Parcel in)
        {
            String artist = in.readString();
            MediaItem[] array;
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                array = (MediaItem[])in.readParcelableArray(loader);
            else
                array = in.readParcelableArray(loader,MediaItem.class);
            return new ArtistItem(artist, Arrays.asList((SongItem[])array));
        }

        @Override
        public ArtistItem[] newArray(int size)
        {
            return new ArtistItem[size];
        }
    };
}
