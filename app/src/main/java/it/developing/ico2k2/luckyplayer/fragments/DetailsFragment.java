package it.developing.ico2k2.luckyplayer.fragments;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.adapters.DetailsAdapter;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;

public class DetailsFragment extends Fragment
{

    private RecyclerView list;
    private DetailsAdapter adapter;
    private int contextClickPosition;

    /*public static DetailsFragment create(int index)
    {
        DetailsFragment fragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_INDEX,index);
        fragment.setArguments(bundle);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void setAdapter(DetailsAdapter detailsAdapter)
    {
        if(list == null)
            adapter = detailsAdapter;
        else
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
        list = view.findViewById(android.R.id.list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
        if(adapter != null)
            list.setAdapter(adapter);
    }
}
