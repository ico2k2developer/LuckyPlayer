package it.developing.ico2k2.luckyplayer.activities.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;

import it.developing.ico2k2.luckyplayer.fragments.SmallPlayerFragment;
import it.developing.ico2k2.luckyplayer.services.OnServiceBoundListener;
import it.developing.ico2k2.luckyplayer.services.OnServiceMessageListener;

import static it.developing.ico2k2.luckyplayer.Keys.KEY_REQUEST_CODE;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_OFFLINE;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_ONLINE;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_SCAN_REQUESTED;
import static it.developing.ico2k2.luckyplayer.Keys.MESSAGE_SONG_REQUEST;

public abstract class BasePlayingActivity extends BaseActivity implements OnServiceBoundListener,OnServiceMessageListener
{
    private static final int ID_FRAME_LAYOUT = 0xFADE;

    private Bundle bundle;
    private SmallPlayerFragment fragment;

    protected void requestPlayer()
    {
        if(fragment == null)
        {
            FrameLayout frameLayout = new FrameLayout(this);
            frameLayout.setId(ID_FRAME_LAYOUT);
            if(frameLayout.getParent() != null)
                ((ViewGroup)frameLayout.getParent()).removeView(frameLayout);
            ViewGroup parent = (ViewGroup)getContentView();

            TypedValue value = new TypedValue();
            /*getTheme().resolveAttribute(R.attr.toolbarShadow,value,true);
            View shadow = new View(this);
            shadow.setBackgroundResource(value.resourceId);*/
            //parent = (ViewGroup)parent.getChildAt(parent.getChildCount() - 1);
            if(parent instanceof ConstraintLayout)
            {
                parent.addView(frameLayout);
                ConstraintSet set = new ConstraintSet();
                set.clone((ConstraintLayout)parent);
                set.connect(ID_FRAME_LAYOUT,ConstraintSet.START,ConstraintSet.PARENT_ID,ConstraintSet.START);
                set.connect(ID_FRAME_LAYOUT,ConstraintSet.END,ConstraintSet.PARENT_ID,ConstraintSet.END);
                set.connect(ID_FRAME_LAYOUT,ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM);
                set.applyTo((ConstraintLayout)parent);
            }
            else if(parent instanceof CoordinatorLayout)
            {
                LinearLayout linearLayout = new LinearLayout(this);
                CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setLayoutParams(params);
                int index = parent.getChildCount() - 1;
                View view = parent.getChildAt(index);
                parent.removeViewAt(index);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
                params2.weight = 1;
                view.setLayoutParams(params2);
                linearLayout.addView(view);
                linearLayout.addView(frameLayout);
                parent.addView(linearLayout);

            }
            fragment = new SmallPlayerFragment();
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(ID_FRAME_LAYOUT,fragment);
        transaction.commit();
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH)
        {
            if(getNavigationBarColored())
                getWindow().setNavigationBarColor(getNavigationBarPlayingColor());
        }
    }

    protected void removePlayer()
    {
        if(fragment != null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bundle = new Bundle();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        getLuckyPlayer().addOnServiceBoundListener(this);
        getLuckyPlayer().addOnServiceMessageListener(this);
        getLuckyPlayer().prepareService();
    }

    protected static final int REQUEST_SCAN = 0x10;
    protected static final int REQUEST_SONGS = 0x11;

    public void requestScan()
    {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) !=  PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_SCAN);
        else
            sendMessageToService(MESSAGE_SCAN_REQUESTED);
    }

    public void requestSongs(String requestCode)
    {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) !=  PackageManager.PERMISSION_GRANTED)
        {
            bundle.putString(Integer.toString(REQUEST_SONGS),requestCode);
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_SONGS);
        }
        else
        {
            Message message = Message.obtain();
            message.what = MESSAGE_SONG_REQUEST;
            Bundle extra = new Bundle();
            extra.putString(KEY_REQUEST_CODE,requestCode);
            message.setData(extra);
            sendMessageToService(message,true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[] permissions,@NonNull int[] grantResults)
    {
        if(grantResults.length > 0)
        {
            switch(requestCode)
            {
                case REQUEST_SCAN:
                {
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        requestScan();
                    break;
                }
                case REQUEST_SONGS:
                {
                    String key = Integer.toString(REQUEST_SONGS);
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        requestSongs(bundle.getString(key));
                    bundle.remove(key);
                    break;
                }

                // other 'case' lines to check for other
                // permissions this app might request.
            }
        }
    }

    @Override
    public void onMessageReceived(int key,@Nullable Bundle packet)
    {

    }

    @Override
    @CallSuper
    public void onServiceBound(){
        getLuckyPlayer().sendMessageToService(MESSAGE_ONLINE);
    }

    @Override
    @CallSuper
    public void onServiceNotBound(){
        finish();
    }

    public void sendMessageToService(int what)
    {
        getLuckyPlayer().sendMessageToService(what);
    }

    public void sendMessageToService(Message message,boolean recycle)
    {
        getLuckyPlayer().sendMessageToService(message,recycle);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getLuckyPlayer().sendMessageToService(MESSAGE_ONLINE);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getLuckyPlayer().sendMessageToService(MESSAGE_OFFLINE);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        getLuckyPlayer().removeOnServiceBoundListener(this);
        getLuckyPlayer().removeOnServiceMessageListener(this);
    }
}
