package it.developing.ico2k2.luckyplayer;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

public class SquareImageView  extends AppCompatImageView{

    public static final int NO_SIZE = 0x0;
    public static final int WIDTH_SIZED = 0x1;
    public static final int HEIGHT_SIZED = 0x2;

    private int sizingMode;

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray b = context.obtainStyledAttributes(attrs,R.styleable.SquareImageView,0,0);
        sizingMode = b.getInt(R.styleable.SquareImageView_sizingMode,NO_SIZE);
        b.recycle();
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray b = context.obtainStyledAttributes(attrs,R.styleable.SquareImageView,defStyleAttr,0);
        sizingMode = b.getInt(R.styleable.SquareImageView_sizingMode,NO_SIZE);
        b.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        switch(sizingMode)
        {
            case WIDTH_SIZED:
            {
                int size = getMeasuredWidth();
                if(size < getSuggestedMinimumWidth())
                    size = getSuggestedMinimumWidth();
                if(size < getSuggestedMinimumHeight())
                    size = getSuggestedMinimumHeight();
                setMeasuredDimension(size,size);
                break;
            }
            case HEIGHT_SIZED:
            {
                int size = getMeasuredHeight();
                if(size < getSuggestedMinimumWidth())
                    size = getSuggestedMinimumWidth();
                if(size < getSuggestedMinimumHeight())
                    size = getSuggestedMinimumHeight();
                setMeasuredDimension(size,size);
                break;
            }
        }
    }

    public int getSizingMode()
    {
        return sizingMode;
    }

    public void setSizingMode(int mode)
    {
        sizingMode = mode;
        invalidate();
    }
}