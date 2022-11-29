package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Build;
import android.os.Parcel;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Collection;

public class GenreItem extends BrowsableItem<SongItem>
{
    public GenreItem(String title)
    {
        super(title);
    }

    public GenreItem(String title,int size)
    {
        super(title,size);
    }

    public GenreItem(String title,Collection<SongItem> items)
    {
        super(title,items);
    }

    @Override
    public String getSubtitle()
    {
        return getTitle();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeString(getTitle());
        dest.writeParcelableArray(getItems(),0);
    }

    public static final Creator<GenreItem> CREATOR = new Creator<GenreItem>()
    {
        @Override
        public GenreItem createFromParcel(Parcel in)
        {
            String title = in.readString();
            MediaItem[] array;
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                array = (MediaItem[])in.readParcelableArray(loader);
            else
                array = in.readParcelableArray(loader,MediaItem.class);
            return new GenreItem(title,Arrays.asList((SongItem[])array));
        }

        @Override
        public GenreItem[] newArray(int size)
        {
            return new GenreItem[size];
        }
    };
}
