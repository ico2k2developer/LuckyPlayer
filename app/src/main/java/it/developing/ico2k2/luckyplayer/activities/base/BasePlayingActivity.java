package it.developing.ico2k2.luckyplayer.activities.base;

import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;

import it.developing.ico2k2.luckyplayer.MediaBrowserDependent;
import it.developing.ico2k2.luckyplayer.fragments.SmallPlayerFragment;
import it.developing.ico2k2.luckyplayer.services.PlayService;

public abstract class BasePlayingActivity extends BaseActivity implements MediaBrowserDependent
{
    private static final String TAG = BasePlayingActivity.class.getSimpleName();

    private static final int ID_FRAME_LAYOUT = 0xFADE;

    private SmallPlayerFragment playerFragment;
    private MediaBrowserCompat browser;
    private MediaControllerCompat.Callback controllerCallback;
    private boolean playerShowing = false,connected = false;

    public MediaBrowserCompat getMediaBrowser()
    {
        return browser;
    }

    protected void requestPlayer()
    {
        Log.d(TAG,"Player requested, showing? " + isPlayerShowing());
        if(!isPlayerShowing())
        {
            if(playerFragment == null)
            {
                FrameLayout frameLayout = new FrameLayout(this);
                frameLayout.setId(ID_FRAME_LAYOUT);
                if(frameLayout.getParent() != null)
                    ((ViewGroup)frameLayout.getParent()).removeView(frameLayout);
                ViewGroup parent = (ViewGroup)getContentView();

                /*TypedValue value = new TypedValue();
            getTheme().resolveAttribute(R.attr.toolbarShadow,value,true);
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
                playerFragment = new SmallPlayerFragment();
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(ID_FRAME_LAYOUT,playerFragment);
            transaction.commit();
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH)
            {
                if(getNavigationBarColored())
                    getWindow().setNavigationBarColor(getNavigationBarPlayingColor());
            }
            playerShowing = true;
        }
    }

    protected void removePlayer()
    {
        if(isPlayerShowing() && playerFragment != null)
        {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(playerFragment);
            transaction.commit();
            playerShowing = false;
        }
    }

    protected boolean isPlayerShowing()
    {
        return playerShowing;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        browser = new MediaBrowserCompat(this,new ComponentName(this,PlayService.class),new MediaBrowserCompat.ConnectionCallback()
        {
            @Override
            public void onConnected()
            {
                Log.d(TAG,"MediaBrowser connected");
                connected = true;
                try
                {
                    MediaSessionCompat.Token token = browser.getSessionToken();
                    MediaControllerCompat mediaController = new MediaControllerCompat(BasePlayingActivity.this,token);
                    MediaControllerCompat.setMediaController(BasePlayingActivity.this, mediaController);
                    Log.d(TAG,"MediaController created");

                    mediaController.registerCallback(controllerCallback = new MediaControllerCompat.Callback()
                    {
                        @Override
                        public void onPlaybackStateChanged(PlaybackStateCompat playbackState)
                        {
                            int state = playbackState.getState();
                            switch(state)
                            {
                                case PlaybackStateCompat.STATE_BUFFERING:
                                case PlaybackStateCompat.STATE_FAST_FORWARDING:
                                case PlaybackStateCompat.STATE_PAUSED:
                                case PlaybackStateCompat.STATE_PLAYING:
                                case PlaybackStateCompat.STATE_REWINDING:
                                case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                                case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                                case PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM:
                                case PlaybackStateCompat.STATE_STOPPED:
                                {
                                    if(!isPlayerShowing())
                                        requestPlayer();
                                    playerFragment.setPlaying(state == PlaybackStateCompat.STATE_PLAYING);
                                    playerFragment.setTimeProgress((int)playbackState.getPosition());
                                    break;
                                }
                                default:
                                {
                                    removePlayer();
                                }
                            }
                        }

                        @Override
                        public void onMetadataChanged(MediaMetadataCompat metadata)
                        {
                            Log.d(TAG,"Metadata changed");
                            requestPlayer();
                            playerFragment.setTimeTotal(metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
                            playerFragment.setTitleSubtitle(metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE),
                                    metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE));
                        }
                    });
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                //requestSongs(TabsActivity.class.getName());



                // Save the controller

                // Finish building the UI
            }

            @Override
            public void onConnectionSuspended()
            {
                Log.d(TAG,"MediaBrowser connection suspended");
                connected = false;
            }

            @Override
            public void onConnectionFailed()
            {
                Log.d(TAG,"MediaBrowser connection failed");
                connected = false;
            }
        },null);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        try
        {
            browser.connect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(BasePlayingActivity.this);
        if(controller != null)
        {
            controller.unregisterCallback(controllerCallback);
        }
        if(browser.isConnected())
            browser.disconnect();
    }

    protected void sendMessageToService(String message,@Nullable Bundle extras)
    {
        MediaControllerCompat.getMediaController(this).getTransportControls().sendCustomAction(message,extras);
    }

    protected void sendMessageToService(String message)
    {
        sendMessageToService(message,null);
    }
}
