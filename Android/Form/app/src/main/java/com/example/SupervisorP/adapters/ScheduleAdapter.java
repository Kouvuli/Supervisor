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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.SupervisorP.R;
import com.example.SupervisorP.activities.EditScheduleActivity;
import com.example.SupervisorP.activities.ScheduleInfoActivity;
import com.example.SupervisorP.dialogs.CustomDialog;
import com.example.SupervisorP.firebase.ScheduleDBM;
import com.example.SupervisorP.fragments.ScheduleFragment;
import com.example.SupervisorP.models.Schedule;

import java.util.ArrayList;
import java.util.Collection;

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable
{
    private Context context;
    ArrayList<Schedule> list = new ArrayList<>();
    ArrayList<Schedule> listAll = new ArrayList<>();
    public ScheduleAdapter(Context ctx)
    {
        this.context = ctx;
    }
    public void setItems(ArrayList<Schedule> schedules)
    {
        list.addAll(schedules);
        listAll.addAll(schedules);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_schedule_item,parent,false);
        return new ScheduleVH(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        Schedule e = null;
        this.onBindViewHolder(holder,position,e);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, Schedule e)
    {
        ScheduleVH vh = (ScheduleVH) holder;
        Schedule schedule = e==null? list.get(position):e;
        vh.txt_date.setText(schedule.getDate());
        vh.txt_start_time.setText(schedule.getTimeStart());
        vh.txt_end_time.setText(schedule.getTimeEnd());
        vh.txt_option.setOnClickListener(v->
        {
            PopupMenu popupMenu =new PopupMenu(context,vh.txt_option);
            popupMenu.inflate(R.menu.option_menu);
            popupMenu.setOnMenuItemClickListener(item->
            {
                switch (item.getItemId())
                {
                    case R.id.menu_info:
                        Intent intent1=new Intent(context, ScheduleInfoActivity.class);
                        intent1.putExtra("INFO",schedule);
                        context.startActivity(intent1);
                        break;
                    case R.id.menu_edit:
                        Intent intent=new Intent(context, EditScheduleActivity.class);
                        intent.putExtra("EDIT",schedule);
                        context.startActivity(intent);
                        break;
                    case R.id.menu_remove:
                        ScheduleDBM schDBM=new ScheduleDBM();
                        schDBM.remove(schedule.getKey()).addOnSuccessListener(suc->
                        {
                            Toast.makeText(context, "Record is removed", Toast.LENGTH_SHORT).show();
                            notifyItemRemoved(position);
                            list.remove(schedule);
                        }).addOnFailureListener(er-> {
                            Toast.makeText(context, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                        });

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
                ArrayList<Schedule> filteredList = new ArrayList<>();
                for (Schedule schedule:listAll){
                    if (date.toString().equals(schedule.getDate())){
                        filteredList.add(schedule);
                    }
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list.clear();
                list.addAll((Collection<? extends Schedule>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
