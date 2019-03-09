package it.developing.ico2k2.luckyplayer.dialogs;

import android.content.Context;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.dialogs.DefaultDialog;

/**
 * Created by Ico on 02/03/2018.
 */

public class ConfirmDialog extends DefaultDialog
{
    public static final int DEFAULT_MESSAGE_RESID = R.string.dialog_confirm_message_default;

    public ConfirmDialog(Context context,int theme)
    {
        super(context,theme);
        initialize(context);
    }

    public ConfirmDialog(Context context)
    {
        super(context);
        initialize(context);
    }

    @Override
    protected void initialize(Context context)
    {
        super.initialize(context);
        setMessage(DEFAULT_MESSAGE_RESID);
    }
}
