package it.developing.ico2k2.luckyplayer.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AlbumArtLoadThread
{
    private String uri;
    private int width,height;
    private ThreadCallback callbacks;

    public AlbumArtLoadThread(String uri,int width,int height,@NonNull ThreadCallback callbacks)
    {
        this.uri = uri;
        this.width = width;
        this.height = height;
        this.callbacks = callbacks;
        run();
    }

    public AlbumArtLoadThread(String uri,@NonNull ThreadCallback callbacks)
    {
        this(uri,0,0,callbacks);
    }

    private void run()
    {
        Handler handler = new Handler();
        handler.post(new Runnable(){
            @Override public void run()
            {
                callbacks.onThreadCreated();
                Bitmap bitmap = null;
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try
                {
                    if(!TextUtils.isEmpty(uri))
                    {
                        retriever.setDataSource(uri);
                        byte[] bytes = retriever.getEmbeddedPicture();
                        if(bytes != null)
                        {
                            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                            if(width > 0 && height > 0)
                            {
                                bitmap = Bitmap.createScaledBitmap(bitmap,width,height,false);
                            }
                            callbacks.onProgressResult(bitmap);
                        }
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                retriever.release();
                callbacks.onProgressResult(bitmap);
            }
        });
    }
}

/*public class AlbumArtLoadTask extends AsyncTask<AlbumArtLoadTask.AlbumArtLoadConfig,Void,Bitmap>
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
            if(!TextUtils.isEmpty(config.uri))
            {
                retriever.setDataSource(config.uri);
                byte[] bytes = retriever.getEmbeddedPicture();
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
}*/
