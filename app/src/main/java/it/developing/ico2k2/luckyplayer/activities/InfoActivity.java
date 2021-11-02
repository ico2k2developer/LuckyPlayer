package it.developing.ico2k2.luckyplayer.activities;

import static it.developing.ico2k2.luckyplayer.Resources.FILE_PROVIDER_AUTHORITY;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.palette.graphics.Palette;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;
import it.developing.ico2k2.luckyplayer.adapters.DetailsAdapter;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;
import it.developing.ico2k2.luckyplayer.database.Database;
import it.developing.ico2k2.luckyplayer.dialogs.DefaultDialog;
import it.developing.ico2k2.luckyplayer.fragments.DetailsFragment;
import it.developing.ico2k2.luckyplayer.tasks.AlbumArtLoadWorker;
import it.developing.ico2k2.luckyplayer.tasks.AsyncTask;
import it.developing.ico2k2.luckyplayer.tasks.MediaManager;

public class InfoActivity extends BaseActivity
{
    private static final String TAG = InfoActivity.class.getSimpleName();

    private CollapsingToolbarLayout toolbarLayout;
    private DetailsFragment tagDetails,fileDetails;
    private it.developing.ico2k2.luckyplayer.database.data.File file;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarLayout = findViewById(R.id.info_appbar_collapsingLayout);
        tagDetails = (DetailsFragment)getSupportFragmentManager().findFragmentById(R.id.info_details_tag);
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

        loadIntent(getIntent());
    }

    private static class Value
    {
        private final FieldKey key;
        private final String value;

        private Value(FieldKey key,String value)
        {
            this.key = key;
            this.value = value;
        }
    }

    private void loadIntent(@NonNull final Intent intent)
    {
        final String[] titles = getResources().getStringArray(R.array.info_details_tag_titles);
        final DetailsAdapter tagAdapter = new DetailsAdapter(titles.length);

        tagAdapter.setOnItemClickListener(new ViewHandle.OnItemClickListener(){
            @Override
            public void onItemClick(ViewHandle handle,int position){
                showDetailDialog(tagAdapter.get(position));
            }
        });
        new AsyncTask<Value,it.developing.ico2k2.luckyplayer.database.data.File>()
                .executeProgressAsync(new AsyncTask.OnStart()
        {
            @Override public void onStart()
            {

            }
        },new AsyncTask.OnCall<Value,it.developing.ico2k2.luckyplayer.database.data.File>()
        {
            @Override public it.developing.ico2k2.luckyplayer.database.data.File
                call(@NonNull AsyncTask.PublishProgress<Value> progress) throws Exception
            {
                it.developing.ico2k2.luckyplayer.database.data.File file = loadFile(InfoActivity.this,intent);
                AudioFile audio = AudioFileIO.read(new File(file.getUri()));
                Tag tag = audio.getTag();
                AudioHeader header = audio.getAudioHeader();
                String value;
                for(FieldKey key : FieldKey.values())
                {
                    value = null;
                    try
                    {
                        value = tag.getFirst(key);
                    }
                    catch(KeyNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    if(value != null)
                        progress.publishProgress(new Value(key,value));
                }
                return file;
            }
        },new AsyncTask.OnProgress<Value>()
        {
            @Override public void onProgress(@NonNull Value progress)
            {
                switch(progress.key)
                {
                    case TITLE:
                    {
                        setTitle(progress.value);
                    }
                    default:
                    {
                        if(!TextUtils.isEmpty(progress.value))
                        {
                            DetailsAdapter.Detail detail;
                            if(progress.value.contains("\n"))
                            {
                                detail = new DetailsAdapter.TextDetail(titles[progress.key.ordinal()],
                                                                       null,progress.value);
                            }
                            else
                                detail = new DetailsAdapter.Detail(titles[progress.key.ordinal()],
                                                                   progress.value);
                            tagAdapter.add(detail);
                        }
                    }
                }
            }
        },new AsyncTask.OnFinish<it.developing.ico2k2.luckyplayer.database.data.File>(){
            @Override public void onComplete(@Nullable it.developing.ico2k2.luckyplayer.database.data.File result)
            {
                InfoActivity.this.file = result;
            }
        });
        try
        {
            setTitle(tag.getFirst(FieldKey.TITLE));
            String data;
            for(FieldKey field : FieldKey.values())
            {
                data = tag.getFirst(field);
                if(!TextUtils.isEmpty(data))
                {
                    DetailsAdapter.Detail detail;
                    if(data.contains("\n"))
                    {
                        detail = new DetailsAdapter.TextDetail(titles[field.ordinal()],null,data);
                    }
                    else
                        detail = new DetailsAdapter.Detail(titles[field.ordinal()],data);
                    tagAdapter.add(detail);
                }
            }
            /*tagDetails.setOnFragmentInitialized(new BaseFragment.OnFragmentInitialized(){
                @Override
                public void onInitialized(@NonNull View view){
                    tagDetails.setAdapter(tagAdapter);
                }
            });*/
            titles = getResources().getStringArray(R.array.info_details_file_titles);
            final DetailsAdapter fileAdapter = new DetailsAdapter(titles.length);
            int a = 0;
            fileAdapter.add(new DetailsAdapter.TextDetail(titles[a++],null,path));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],header.getBitRate()));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],Integer.toString(header.getBitsPerSample())));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],header.getChannels()));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],header.getEncodingType()));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],header.getFormat()));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],header.getSampleRate()));
            fileAdapter.add(new DetailsAdapter.Detail(titles[a++],Integer.toString(header.getTrackLength())));
            fileAdapter.add(new DetailsAdapter.CheckedDetail(titles[a++],null,header.isLossless()));
            fileAdapter.add(new DetailsAdapter.CheckedDetail(titles[a],null,header.isVariableBitRate()));
            fileAdapter.setOnItemClickListener(new ViewHandle.OnItemClickListener(){
                @Override
                public void onItemClick(ViewHandle handle,int position){
                    showDetailDialog(fileAdapter.get(position));
            }
            });
            /*fileDetails.setOnFragmentInitialized(new BaseFragment.OnFragmentInitialized(){
                @Override
                public void onInitialized(@NonNull View view){
                    fileDetails.setAdapter(fileAdapter);
                }
            });*/
            final AppCompatImageView imageView = findViewById(R.id.info_album_art);
            WorkManager manager = WorkManager.getInstance(this);
            WorkRequest request = new OneTimeWorkRequest.Builder(AlbumArtLoadWorker.class).setInputData(
                    new Data.Builder()
                    .putString(AlbumArtLoadWorker.PARAM_URI_S,path)
                    .build()).build();
            manager.enqueue(request);
            manager.getWorkInfoByIdLiveData(request.getId()).observe(this,new Observer<WorkInfo>(){
                @Override
                public void onChanged(WorkInfo workInfo){
                    Bitmap image = (Bitmap)workInfo.getOutputData().getKeyValueMap().get(AlbumArtLoadWorker.PARAM_BITMAP_O);
                    Palette.from(image).generate(new Palette.PaletteAsyncListener(){
                        @Override
                        public void onGenerated(@Nullable Palette palette){
                            if(palette != null)
                            {
                                int mutedColor = palette.getMutedColor(R.attr.colorPrimary);
                                toolbarLayout.setContentScrimColor(mutedColor);
                            }
                        }
                    });
                    imageView.setImageBitmap(image);
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this,R.string.error,Toast.LENGTH_LONG).show();
            finish();
        }
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
        if(detail instanceof DetailsAdapter.TextDetail)
            showDetailDialog(detail.getTitle(),((DetailsAdapter.TextDetail)detail).getText());
        else if(!(detail instanceof DetailsAdapter.CheckedDetail))
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

    public static final String EXTRA_ID = "item id";

    @Nullable
    public static it.developing.ico2k2.luckyplayer.database.data.File loadFile(Context context,Intent intent)
    {
        it.developing.ico2k2.luckyplayer.database.data.File file = null;
        MediaManager.CursorResult result = null;
        Bundle extras = intent.getExtras();
        Uri uri = intent.getData();
        if(uri != null)
            result = MediaManager.getRealPath(context,uri);
        if(extras != null)
        {
            String tmp;
            if(result == null)
            {
                tmp = extras.getString(Intent.EXTRA_STREAM);
                if(tmp != null)
                    result = MediaManager.getRealPath(context,Uri.parse(tmp));
            }
            if(result != null)
            {
                tmp = extras.getString(EXTRA_ID);
                if(tmp != null)
                {
                    try
                    {
                        file = result.loadBest(Database.getTableId(tmp),Database.getItemId(tmp));
                    }
                    catch(StringIndexOutOfBoundsException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(file == null && result != null)
            file = result.loadBest(0,0);
        return file;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_info,menu);
        /*ShareActionProvider sap = (ShareActionProvider)MenuItemCompat.getActionProvider(menu.findItem(R.id.info_share));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");
        if(path == null)
            resolvePath(getIntent());
        Uri uri = FileProvider.getUriForFile(this,FILE_PROVIDER_AUTHORITY,new File(path));
        Log.d(TAG,"Path: " + uri);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sap.setShareIntent(intent);*/
        return true;
    }

    private static final String FILENAME_COVER = "album cover.png";

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();
        boolean result = true;

        switch(id)
        {
            case R.id.info_save_cover:
            case R.id.info_send_cover:
            {
                WorkManager manager = WorkManager.getInstance(this);
                WorkRequest request = new OneTimeWorkRequest.Builder(AlbumArtLoadWorker.class).setInputData(
                        new Data.Builder()
                                .putString(AlbumArtLoadWorker.PARAM_URI_S,path)
                                .build()).build();
                manager.enqueue(request);
                manager.getWorkInfoByIdLiveData(request.getId()).observe(this,new Observer<WorkInfo>(){
                    @Override
                    public void onChanged(WorkInfo workInfo){
                        Bitmap image = (Bitmap)workInfo.getOutputData().getKeyValueMap().get(AlbumArtLoadWorker.PARAM_BITMAP_O);
                        File dir;
                        String name;
                        if(id == R.id.info_send_cover)
                        {
                            dir = new File(getExternalCacheDir(),"covers");
                            dir.mkdir();
                            name = FILENAME_COVER;
                        }
                        else
                        {
                            dir = getExternalFilesDir(null);
                            dir = new File(dir,"Download");
                            name = getSupportActionBar().getTitle() + FILENAME_COVER.substring(FILENAME_COVER.lastIndexOf("."));
                        }
                        Log.d(TAG,"Cache dir: " + dir.getAbsolutePath());
                        if(dir.isDirectory()){
                            try
                            {
                                OutputStream os;
                                File file = new File(dir,name);
                                os = new FileOutputStream(file);
                                image.compress(Bitmap.CompressFormat.PNG,100,os);
                                os.flush();
                                os.close();
                                if(id == R.id.info_send_cover)
                                {
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("image/png");
                                    intent.putExtra(Intent.EXTRA_STREAM,FileProvider.getUriForFile(InfoActivity.this,FILE_PROVIDER_AUTHORITY,file));
                                    startActivity(intent);
                                }
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                                Toast.makeText(InfoActivity.this,R.string.error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                break;
            }
            default:
            {
                result = super.onOptionsItemSelected(item);
            }
        }
        return result;
    }
}