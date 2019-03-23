package it.developing.ico2k2.luckyplayer.activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;


import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;
import java.util.List;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;
import it.developing.ico2k2.luckyplayer.tasks.AlbumArtLoadTask;
import it.developing.ico2k2.luckyplayer.tasks.AsyncThread;

import static it.developing.ico2k2.luckyplayer.Keys.EXTRA_URI;

public class InfoActivity extends BaseActivity
{
    private CollapsingToolbarLayout toolbarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setSupportActionBar((Toolbar)findViewById(R.id.info_toolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarLayout = findViewById(R.id.info_appbar_collapsingLayout);


        handleIntent(getIntent());
    }



    public String getRealPath(Uri contentPath){
        String result = Uri.decode(contentPath.toString()).replace("file://","");
        Log.d("UWUWU","Original path: " + result);
        if(!new File(result).exists())
        {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(contentPath,projection,null,null,null);

            if(cursor != null)
            {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                result = cursor.getString(columnIndex);
                cursor.close();

            }
        }
        Log.d("UWUWU","Real path: " + result);
        Toast.makeText(this,result,Toast.LENGTH_SHORT).show();
        return result;
    }

    void handleIntent(Intent intent)
    {
        String path = intent.getStringExtra(EXTRA_URI);
        if(path == null)
            path = getRealPath(intent.getData());
        final AppCompatImageView imageView = findViewById(R.id.info_album_art);
        AlbumArtLoadTask task = new AlbumArtLoadTask(new AsyncThread.AsyncThreadBaseCallbacks()
        {
            @Override
            public void onPreExecute(){
            }

            @Override
            public void onProgressUpdate(Object... progress){

            }

            @Override
            public void onPostExecute(@Nullable Object result){
                if(result instanceof Bitmap)
                {
                    Bitmap albumArt = (Bitmap)result;
                    Palette.from(albumArt).generate(new Palette.PaletteAsyncListener(){
                        @Override
                        public void onGenerated(@Nullable Palette palette){
                            if(palette != null)
                            {
                                int mutedColor = palette.getMutedColor(R.attr.colorPrimary);
                                toolbarLayout.setContentScrimColor(mutedColor);
                            }
                        }
                    });
                    imageView.setImageBitmap(albumArt);
                }
            }
        });
        AlbumArtLoadTask.AlbumArtLoadConfig config = new AlbumArtLoadTask.AlbumArtLoadConfig(
                path
        );
        task.execute(config);
        setTitle(path);
    }

    /*

        Palette.from(albumArt).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int mutedColor = palette.getLightMutedColor(R.attr.colorPrimary);
                collapsingLayout.setContentScrimColor(mutedColor);
            }
        });
     */


    @Override
    public void setTitle(CharSequence title)
    {
        toolbarLayout.setTitleEnabled(true);
        super.setTitle(title);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH)
        {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                    title.toString());
            setTaskDescription(taskDescription);
        }
    }
}