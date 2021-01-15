package it.developing.ico2k2.luckyplayer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.StyleRes;
import androidx.appcompat.widget.Toolbar;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.activities.base.BaseActivity;

public class InitializeActivity extends BaseActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        findViewById(R.id.initialize_fab).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    public boolean onThemeChanged(@StyleRes int oldTheme,@StyleRes int newTheme)
    {
        return getMainSharedPreferences().getBoolean(getString(R.string.key_initialized),false);
    }

    public boolean onNoDataFound()
    {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_initialize,menu);
        menu.findItem(R.id.menuShowEveryTime).setChecked(getMainSharedPreferences().getBoolean(getString(R.string.key_show_init_every_time),false));
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
            case R.id.menuShowEveryTime:
            {
                item.setChecked(!item.isChecked());
                getMainSharedPreferences().edit()
                        .putBoolean(getString(R.string.key_show_init_every_time),item.isChecked())
                        .apply();
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
