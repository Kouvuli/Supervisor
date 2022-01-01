package com.example.SupervisorP.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.SupervisorP.R;
import com.example.SupervisorP.activities.EditScheduleActivity;
import com.example.SupervisorP.activities.KeyLogActivity;
import com.example.SupervisorP.activities.ScheduleInfoActivity;
import com.example.SupervisorP.firebase.ScheduleDBM;
import com.example.SupervisorP.models.History;

import java.util.ArrayList;
import java.util.Collection;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context context;
    ArrayList<History> list = new ArrayList<>();
    ArrayList<History> listAll=new ArrayList<>();
    public HistoryAdapter(Context ctx)
    {
        this.context = ctx;
    }
    public void setItems(ArrayList<History> histories)
    {
        list.addAll(histories);
        listAll.addAll(histories);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_history_item,parent,false);
        return new HistoryVH(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        History e = null;
        this.onBindViewHolder(holder,position,e);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, History e)
    {
        HistoryVH vh = (HistoryVH) holder;
        History history = e==null? list.get(position):e;
        vh.txt_date.setText(history.getDate());
        vh.txt_start_time.setText(history.getTimeStart());
        vh.txt_end_time.setText(history.getTimeEnd());
        vh.txt_option.setOnClickListener(v->
        {
            PopupMenu popupMenu =new PopupMenu(context,vh.txt_option);
            popupMenu.inflate(R.menu.main_menu);
            popupMenu.setOnMenuItemClickListener(item->
            {
                switch (item.getItemId())
                {
                    case R.id.menu_key_log:
                        Intent intent1=new Intent(context, KeyLogActivity.class);
                        intent1.putExtra("KEYLOG",history);
                        context.startActivity(intent1);
                        break;

                }
                return false;
            });
            popupMenu.show();
        });

    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence date) {
                ArrayList<History> filteredList = new ArrayList<>();
                for (History history:listAll){
                    if (date.toString().equals(history.getDate())){
                        filteredList.add(history);
                    }
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list.clear();
                list.addAll((Collection<? extends History>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
