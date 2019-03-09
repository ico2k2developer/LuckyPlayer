package it.developing.ico2k2.luckyplayer.dialogs;

import android.annotation.TargetApi;
import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import it.developing.ico2k2.luckyplayer.R;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressDialog extends DefaultDialog
{
    protected TextView message;

    public ProgressDialog(Context context)
    {
        super(context);
        prepare(context);
    }

    @TargetApi(11)
    public ProgressDialog(Context context,@StyleRes int style)
    {
        super(context,style);
        prepare(context);
    }

    protected void prepare(Context context)
    {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setGravity(Gravity.CENTER);
        int margin = context.getResources().getDimensionPixelSize(R.dimen.default_medium_margin);
        linearLayout.setPadding(margin,margin,margin,margin);
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        message = new TextView(context);
        message.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f));
        message.setPadding(margin,0,0,0);
        linearLayout.addView(progressBar);
        linearLayout.addView(message);
        super.setView(linearLayout);
    }

    @Override
    public ProgressDialog setView(View view)
    {
        throw new UnsupportedOperationException("You can't call setView()");
    }

    @Override
    public ProgressDialog setView(@LayoutRes int layout)
    {
        throw new UnsupportedOperationException("You can't call setView()");
    }

    @Override
    public ProgressDialog setMessage(CharSequence sequence)
    {
        message.setText(sequence);
        return this;
    }

    @Override
    public ProgressDialog setMessage(@StringRes int string)
    {
        message.setText(string);
        return this;
    }
}
