package it.developing.ico2k2.luckyplayer.views;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import it.developing.ico2k2.luckyplayer.R;

public class DefaultLayout extends CoordinatorLayout
{
    private static final int ID_SHADOW = 0xFADE;
    private static final int ID_MAIN_LAYOUT = 0xFACE1;
    private static final int ID_INNER_LAYOUT = 0xFACE2;

    private View shadow;
    private ConstraintLayout layout;
    private ConstraintLayout.LayoutParams params2;
    private ConstraintSet set;
    private boolean setup;

    public DefaultLayout(Context context)
    {
        super(context);
        setup();
    }

    public DefaultLayout(Context context,@Nullable AttributeSet attrs)
    {
        super(context,attrs);
        setup();
    }

    public DefaultLayout(Context context,@Nullable AttributeSet attrs,@AttrRes int defAttrStyle)
    {
        super(context,attrs,defAttrStyle);
        setup();
    }

    private void setup()
    {
        setFitsSystemWindows(true);
        setWillNotDraw(false);
        layout = new ConstraintLayout(getContext());
        layout.setId(ID_MAIN_LAYOUT);
        shadow = new View(getContext());
        shadow.setId(ID_SHADOW);
        shadow.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height)));
        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.toolbarShadow,value,true);
        shadow.setBackgroundResource(value.resourceId);
        layout.addView(shadow);
        params2 = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        set = new ConstraintSet();
        setup = false;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if(!setup && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            int a;
            View view = null;
            for(a = 0; a < getChildCount(); a++)
            {
                View v = getChildAt(a);
                LayoutParams params = (LayoutParams)v.getLayoutParams();
                if(params.getBehavior() instanceof AppBarLayout.ScrollingViewBehavior)
                {
                    view = v;
                    break;
                }

            }
            if(view != null)
            {
                if(view.getId() == NO_ID)
                    view.setId(ID_INNER_LAYOUT);
                layout.setLayoutParams(view.getLayoutParams());
                ViewGroup parent = (ViewGroup)view.getParent();
                parent.removeView(view);
                view.setLayoutParams(params2);
                //layout.addView(shadow);
                layout.addView(view);
                set.clone(layout);
                set.connect(ID_SHADOW,ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP);
                set.applyTo(layout);

                parent.addView(layout,0);
            }
            setup = true;
        }
    }
}
