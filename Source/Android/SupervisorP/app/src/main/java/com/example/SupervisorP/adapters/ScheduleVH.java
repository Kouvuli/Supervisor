package com.example.SupervisorP.adapters;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.SupervisorP.R;


public class ScheduleVH extends RecyclerView.ViewHolder {
    public TextView txt_date,txt_option,txt_start_time,txt_end_time;

    public ScheduleVH(@NonNull View itemView) {
        super(itemView);
        txt_date=itemView.findViewById(R.id.txt_date);
        txt_start_time=itemView.findViewById(R.id.txt_start_time);
        txt_end_time=itemView.findViewById(R.id.txt_end_time);
        txt_option=itemView.findViewById(R.id.txt_option);
    }
}
