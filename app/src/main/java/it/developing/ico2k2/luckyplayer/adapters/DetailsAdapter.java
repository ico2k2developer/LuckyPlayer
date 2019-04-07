package it.developing.ico2k2.luckyplayer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.developing.ico2k2.luckyplayer.R;
import it.developing.ico2k2.luckyplayer.adapters.base.BaseAdapter;
import it.developing.ico2k2.luckyplayer.adapters.lib.ViewHandle;

public class DetailsAdapter extends BaseAdapter<DetailsAdapter.DetailHandle>
{
    private ArrayList<Detail> details;

    public DetailsAdapter()
    {
        details = new ArrayList<>();
    }

    public DetailsAdapter(int size)
    {
        details = new ArrayList<>(size);
    }

    public void addAll(Collection<? extends Detail> collection)
    {
        details.addAll(collection);
    }

    public void add(Detail detail)
    {
        details.add(detail);
    }

    public Detail get(int index)
    {
        return details.get(index);
    }

    public void clear()
    {
        details.clear();
    }

    public void remove(Detail detail)
    {
        details.remove(detail);
    }

    public void remove(int index)
    {
        details.remove(index);
    }


    @Override
    @NonNull
    public DetailHandle onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout)LayoutInflater.from(
                parent.getContext()).inflate(R.layout.list_item_detail,
                parent,
                false);
        return new DetailHandle(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailHandle holder,int position)
    {
        super.onBindViewHolder(holder,position);
        Detail detail = details.get(position);
        if(detail instanceof CheckedDetail)
        {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(View.GONE);
            holder.checkBox.setChecked(((CheckedDetail)detail).isChecked());
        }
        else if(detail instanceof TextDetail)
        {
            holder.checkBox.setVisibility(View.GONE);
            holder.icon.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.checkBox.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);
        }
        holder.title.setText(detail.getTitle());
        holder.description.setText(detail.getDescription());
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public static class DetailHandle extends ViewHandle{
        // each data item is just a string in this case
        AppCompatTextView title,description;
        AppCompatCheckBox checkBox;
        AppCompatImageView icon;

        DetailHandle(ConstraintLayout layout)
        {
            super(layout);
            title = layout.findViewById(R.id.itemTitle);
            description = layout.findViewById(R.id.itemDescription);
            checkBox = layout.findViewById(R.id.itemCheckBox);
            icon = layout.findViewById(R.id.itemIcon);
        }
    }

    public static class Detail
    {
        private String title;
        private String description;

        public Detail(String title,String description)
        {
            this.title = title;
            this.description = description;
        }

        public String getTitle(){
            return title;
        }

        public String getDescription(){
            return description;
        }
    }

    public static class CheckedDetail extends Detail
    {
        private boolean checked;

        public CheckedDetail(String title,String description)
        {
            this(title,description,false);
        }

        public CheckedDetail(String title,String description,boolean checked)
        {
            super(title,description);
            this.checked = checked;
        }

        public boolean isChecked(){
            return checked;
        }

        public void setChecked(boolean checked){
            this.checked = checked;
        }
    }

    public static class TextDetail extends Detail
    {
        private String text;

        public TextDetail(String title,String description)
        {
            this(title,description,null);
        }

        public TextDetail(String title,String description,String text)
        {
            super(title,description);
            this.text = text;
        }

        public String getText(){
            return text;
        }

        public void setText(String text){
            this.text = text;
        }
    }
}
