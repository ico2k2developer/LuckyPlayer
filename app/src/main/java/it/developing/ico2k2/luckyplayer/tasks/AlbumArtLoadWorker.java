package it.developing.ico2k2.luckyplayer.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.HashMap;
import java.util.Map;

public class AlbumArtLoadWorker extends Worker
{
    public static final String PARAM_URI_S  = "uri";
    public static final String PARAM_WIDTH_I  = "width";
    public static final String PARAM_HEIGHT_I  = "height";
    public static final String PARAM_BITMAP_O  = "bitmap";

    private final Data input;

    public AlbumArtLoadWorker(@NonNull Context context,@NonNull WorkerParameters params)
    {
        super(context,params);
        input = params.getInputData();
    }

    @Override
    public Result doWork()
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try
        {
            String uri = input.getString(PARAM_URI_S);
            if(!TextUtils.isEmpty(uri))
            {
                retriever.setDataSource(uri);
                byte[] bytes = retriever.getEmbeddedPicture();
                if(bytes != null)
                {
                    int width = input.getInt(PARAM_WIDTH_I,0);
                    int height = input.getInt(PARAM_HEIGHT_I,0);
                    bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    if(width > 0 && height > 0)
                    {
                        bitmap = Bitmap.createScaledBitmap(bitmap,width,height,false);
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        retriever.release();
        if(bitmap == null)
            return Result.failure();
        Map<String,Object> map = new HashMap<>();
        map.put(PARAM_BITMAP_O,bitmap);
        return Result.success(new Data.Builder().putAll(map).build());
    }
}
