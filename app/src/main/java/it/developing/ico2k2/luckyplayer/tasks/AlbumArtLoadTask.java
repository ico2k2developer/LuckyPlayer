package it.developing.ico2k2.luckyplayer.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

public class AlbumArtLoadTask extends AsyncTask<AlbumArtLoadTask.AlbumArtLoadConfig,Void,Bitmap>
{
    private AsyncThread.AsyncThreadBaseCallbacks callbacks;

    public static class AlbumArtLoadConfig
    {
        private int width,height;
        private String uri;

        public AlbumArtLoadConfig(String uri,int width,int height)
        {
            this.uri = uri;
            this.width = width;
            this.height = height;
        }

        public AlbumArtLoadConfig(String uri)
        {
            this(uri,0,0);
        }
    }

    public AlbumArtLoadTask(AsyncThread.AsyncThreadBaseCallbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    @Override
    @Nullable
    protected Bitmap doInBackground(AlbumArtLoadConfig... strings)
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try
        {
            AlbumArtLoadConfig config = strings[0];
            if(config.uri != null)
            {
                retriever.setDataSource(config.uri);
                byte[] bytes = retriever.getEmbeddedPicture();
                retriever.release();
                if(bytes != null)
                {
                    bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    if(config.width > 0 && config.height > 0)
                    {
                        bitmap = Bitmap.createScaledBitmap(bitmap,config.width,config.height,false);
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            retriever.release();
        }
        retriever.release();
        return bitmap;
    }

    @Override
    protected void onPreExecute()
    {
        callbacks.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... progress)
    {
        callbacks.onProgressUpdate((Object[])progress);
    }

    @Override
    protected void onPostExecute(Bitmap result)
    {
        callbacks.onPostExecute(result);
    }
}
