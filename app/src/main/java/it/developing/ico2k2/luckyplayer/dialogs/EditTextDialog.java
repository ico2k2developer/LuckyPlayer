package it.developing.ico2k2.luckyplayer.dialogs;

import android.content.Context;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import static android.text.InputType.*;

/**
 * Created by Ico on 10/03/2018.
 */

public class EditTextDialog extends DefaultDialog
{
    public static final int DEFAULT_INPUTTYPE = TYPE_CLASS_TEXT | TYPE_TEXT_FLAG_NO_SUGGESTIONS | TYPE_TEXT_VARIATION_SHORT_MESSAGE;
    public static final int DEFAULT_PADDINGLEFT = 10;
    public static final int DEFAULT_PADDINGTOP = 0;
    public static final int DEFAULT_PADDINGRIGHT = 10;
    public static final int DEFAULT_PADDINGBOTTOM = 0;

    private LinearLayout layout;
    private AppCompatEditText editText;


    public EditTextDialog(Context context,int theme)
    {
        super(context,theme);
        initialize(context);
    }

    public EditTextDialog(Context context) {
        super(context);
        initialize(context);
    }

    @Override
    protected void initialize(Context context)
    {
        super.initialize(context);
        layout = new LinearLayout(context);
        layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
        editText = new AppCompatEditText(context);
        editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setInputType(DEFAULT_INPUTTYPE);
        setPadding(DEFAULT_PADDINGLEFT,DEFAULT_PADDINGTOP,DEFAULT_PADDINGRIGHT,DEFAULT_PADDINGBOTTOM);
        layout.addView(editText);
        setView(layout);
    }

    public EditTextDialog setPadding(int left, int top, int right, int bottom)
    {
        layout.setPadding(left,top,right,bottom);
        return this;
    }

    public EditTextDialog setInputType(int type)
    {
        editText.setInputType(type);
        return this;
    }

    public EditTextDialog setHint(int resId)
    {
        editText.setHint(resId);
        return this;
    }

    public EditTextDialog setHint(CharSequence hint)
    {
        editText.setHint(hint);
        return this;
    }

    public EditTextDialog setText(int resId)
    {
        editText.setText(resId);
        return this;
    }

    public EditTextDialog setText(CharSequence hint)
    {
        editText.setText(hint);
        return this;
    }

    public Editable getText()
    {
        return editText.getText();
    }
}
