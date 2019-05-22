package it.developing.ico2k2.luckyplayer.activities;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.palette.graphics.Palette;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;
import it.developing.ico2k2.luckyplayer.adapters.DetailsAdapter;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;
import it.developing.ico2k2.luckyplayer.dialogs.DefaultDialog;
import it.developing.ico2k2.luckyplayer.fragments.DetailsFragment;
import it.developing.ico2k2.luckyplayer.fragments.base.BaseFragment;
import it.developing.ico2k2.luckyplayer.tasks.AlbumArtLoadTask;
import it.developing.ico2k2.luckyplayer.tasks.AsyncThread;

import static it.developing.ico2k2.luckyplayer.Keys.EXTRA_URI;

public class InfoActivity extends BaseActivity
{
    private CollapsingToolbarLayout toolbarLayout;
    private DetailsFragment tagDetails,fileDetails;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        final Toolbar toolbar = findViewById(R.id.info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarLayout = findViewById(R.id.info_appbar_collapsingLayout);
        tagDetails = (DetailsFragment)getSupportFragmentManager().findFragmentById(R.id.info_details_tag);
        tagDetails.setOnFragmentInitialized(new BaseFragment.OnFragmentInitialized(){
            @Override
            public void onInitialized(@NonNull View view){
                tagDetails.setTitle(R.string.info_details_tag);
            }
        });
        fileDetails = (DetailsFragment)getSupportFragmentManager().findFragmentById(R.id.info_details_file);
        fileDetails.setOnFragmentInitialized(new BaseFragment.OnFragmentInitialized(){
            @Override
            public void onInitialized(@NonNull View view){
                fileDetails.setTitle(R.string.info_details_file);
            }
        });
        toolbarLayout.setTitleEnabled(true);

        toolbar.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener(){
            @Override
            public void onChildViewAdded(View parent,View child){
                if(!(child instanceof AppCompatTextView) &&
                    !(child instanceof AppCompatImageButton) &&
                    !(child instanceof ActionMenuView))
                {
                    toolbar.removeView(child);
                }
            }

            @Override
            public void onChildViewRemoved(View parent,View child){

            }
        });

        handleIntent(getIntent());
    }

    public String getRealPath(Uri contentPath){
        String result = null;
        if(contentPath != null)
        {
            result = contentPath.toString();
            Log.d("UWUWU","Original path: " + result);
            result = contentPath.getPath();
            if(!new File(result).exists())
            {
                try
                {
                    Cursor cursor = getContentResolver().query(contentPath,null,null,null,null);

                    int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    result = cursor.getString(index);
                    cursor.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
            Log.d("UWUWU","Real path: " + result);
        }
        return result;
    }

    void handleIntent(Intent intent)
    {
        String path = intent.getStringExtra(EXTRA_URI);
        if(path == null)
            path = getRealPath(intent.getData());
        try
        {
            AudioFile audio = AudioFileIO.read(new File(path));
            Tag tag = audio.getTag();
            setTitle(tag.getFirst(FieldKey.TITLE));
            String[] titles = getResources().getStringArray(R.array.info_details_tag_titles);
            final DetailsAdapter tagAdapter = new DetailsAdapter(titles.length);
            String data;
            for(FieldKey field : FieldKey.values())
            {
                data = tag.getFirst(field);
                if(!TextUtils.isEmpty(data))
                {
                    DetailsAdapter.Detail detail;
                    if(data.contains("\n"))
                    {
                        detail = new DetailsAdapter.TextDetail(titles[field.ordinal()],null);
                        ((DetailsAdapter.TextDetail)detail).setText(data);
                    }
                    else
                        detail = new DetailsAdapter.Detail(titles[field.ordinal()],data);
                    tagAdapter.add(detail);
                }
            }
            tagAdapter.setOnItemClickListener(new ViewHandle.OnItemClickListener(){
                @Override
                public void onItemClick(ViewHandle handle,int position){
                    showDetailDialog(tagAdapter.get(position));
                }
            });
            tagDetails.setOnFragmentInitialized(new BaseFragment.OnFragmentInitialized(){
                @Override
                public void onInitialized(@NonNull View view){
                    tagDetails.setAdapter(tagAdapter);
                }
            });
            AudioHeader header = audio.getAudioHeader();
            titles = getResources().getStringArray(R.array.info_details_file_titles);
            final DetailsAdapter fileAdapter = new DetailsAdapter(titles.length);
            int a = 0;
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],header.getBitRate()));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],Integer.toString(header.getBitsPerSample())));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],header.getChannels()));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],header.getEncodingType()));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],header.getFormat()));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],header.getSampleRate()));
            fileAdapter.add(new DetailsAdapter.CheckedDetail(titles[a++],null,header.isLossless()));
            fileAdapter.add(new DetailsAdapter.CheckedDetail(titles[a],null,header.isVariableBitRate()));
            fileAdapter.setOnItemClickListener(new ViewHandle.OnItemClickListener(){
                @Override
                public void onItemClick(ViewHandle handle,int position){
                    showDetailDialog(fileAdapter.get(position));
            }
            });
            fileDetails.setOnFragmentInitialized(new BaseFragment.OnFragmentInitialized(){
                @Override
                public void onInitialized(@NonNull View view){
                    fileDetails.setAdapter(fileAdapter);
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            setTitle(path);
        }
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

    public void showDetailDialog(DetailsAdapter.Detail detail)
    {
        if(!(detail instanceof DetailsAdapter.CheckedDetail))
            showDetailDialog(detail.getTitle(),detail.getDescription());
    }

    public void showDetailDialog(final String label,final String text)
    {
        DefaultDialog dialog = new DefaultDialog(InfoActivity.this);
        dialog.setTitle(label);
        dialog.setMessage(text);
        dialog.setCancelable(true);
        dialog.setNeutralButton(R.string.copy,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog,int which){
                setClipboard(label,text,true);
            }
        });
        dialog.setPositiveButton(android.R.string.ok);
        dialog.show();
    }


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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean result = true;

        switch(id)
        {
            default:
            {
                result = super.onOptionsItemSelected(item);
            }
        }
        return result;
    }*/
}