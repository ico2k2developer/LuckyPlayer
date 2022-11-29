package it.developing.ico2k2.luckyplayer.adapters.items;

import android.os.Parcel;

import androidx.annotation.NonNull;

import it.developing.ico2k2.luckyplayer.database.file.media.BaseMedia;

public class SongItem extends PlayableItem
{
    public SongItem(BaseMedia.UniqueId id,String title,String artist,
                    PlayableItem.Timestamp timestamp)
    {
        super(id,title,artist,timestamp);
    }

    public SongItem(BaseMedia.UniqueId id,String title,String artist,long length)
    {
        super(id,title,artist,length);
    }

    public SongItem(short volume,long id,String title,String artist,long length)
    {
        super(volume,id,title,artist,length);
    }

    public SongItem(BaseMedia media)
    {
        super(media);
    }

    @Override
    public String getSubtitle()
    {
        return getArtist();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeParcelable(getUniqueId(),0);
        dest.writeString(getTitle());
        dest.writeString(getArtist());
        dest.writeParcelable(getTimestamp(),0);
    }

    public static final Creator<SongItem> CREATOR = new Creator<SongItem>()
    {
        @Override
        public SongItem createFromParcel(Parcel in)
        {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            return new SongItem(
                    BaseMedia.UniqueId.CREATOR.createFromParcel(in),
                    in.readString(),in.readString(),
                    Timestamp.CREATOR.createFromParcel(in));
        }

        @Override
        public SongItem[] newArray(int size)
        {
            return new SongItem[size];
        }
    };
}
