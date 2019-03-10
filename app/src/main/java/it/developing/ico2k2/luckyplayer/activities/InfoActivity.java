package it.developing.ico2k2.luckyplayer.activities;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.File;
import java.util.BitSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.SquareImageView;
import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;
import it.developing.ico2k2.luckyplayer.tasks.AlbumArtLoadTask;
import it.developing.ico2k2.luckyplayer.tasks.AsyncThread;

public class InfoActivity extends BaseActivity
{
    private CollapsingToolbarLayout toolbarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setSupportActionBar((Toolbar)findViewById(R.id.info_toolbar));
        toolbarLayout = findViewById(R.id.info_appbar_collapsingLayout);

        handlePath(getIntent().getDataString());
    }

    void handlePath(String path)
    {
        setTitle(path);
        final SquareImageView imageView = findViewById(R.id.info_album_art);
        AlbumArtLoadTask task = new AlbumArtLoadTask(new AsyncThread.AsyncThreadBaseCallbacks(){
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
                path,
                imageView.getWidth(),
                imageView.getWidth()
        );
        task.execute(config);
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
        super.setTitle(title);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH)
        {
            ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription(
                    title.toString());
            setTaskDescription(taskDescription);

        }
    }
}