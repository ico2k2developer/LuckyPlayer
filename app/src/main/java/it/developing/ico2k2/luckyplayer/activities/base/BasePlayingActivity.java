package it.developing.ico2k2.luckyplayer.activities.base;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.appbar.AppBarLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.fragments.SmallPlayerFragment;

import static it.developing.ico2k2.luckyplayer.Keys.KEY_LOGS;

public abstract class BasePlayingActivity extends BaseActivity
{
    private static final int ID_FRAME_LAYOUT = 0xFADE;

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
            Log.d(KEY_LOGS,parent.getClass().getName());
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
    }

    protected void removePlayer()
    {
        if(fragment != null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        }
    }
}
