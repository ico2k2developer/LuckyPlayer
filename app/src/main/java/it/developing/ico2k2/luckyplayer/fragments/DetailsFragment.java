package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.adapters.DetailsAdapter;
import it.developing.ico2k2.luckyplayer.adapters.layoutmanagers.BaseLayoutManager;
import it.developing.ico2k2.luckyplayer.fragments.base.BaseFragment;

public class DetailsFragment extends BaseFragment
{
    private RecyclerView list;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void setAdapter(DetailsAdapter detailsAdapter)
    {
        if(list != null)
            list.setAdapter(detailsAdapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        boolean result = true;
        switch(item.getItemId())
        {
            default:
            {
                result = false;
            }
        }
        return result;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,Bundle savedInstance)
    {
        return inflater.inflate(R.layout.fragment_details,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view,savedInstanceState);
        list = view.findViewById(android.R.id.list);
        RecyclerView.LayoutManager layoutManager = new BaseLayoutManager(getActivity());
        ((BaseLayoutManager)layoutManager).setScrollEnabled(false);
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
    }

    public void setTitle(CharSequence title)
    {
        AppCompatTextView label = getView().findViewById(android.R.id.title);
        if(label != null)
        {
            label.setText(title);
            label.setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(@StringRes int title)
    {
        AppCompatTextView label = getView().findViewById(android.R.id.title);
        if(label != null)
        {
            label.setText(title);
            label.setVisibility(View.VISIBLE);
        }
    }

    public void setTitleVisibility(int visibility)
    {
        AppCompatTextView label = getView().findViewById(android.R.id.title);
        if(label != null)
            label.setVisibility(visibility);
    }
}
