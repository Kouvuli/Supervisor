package com.example.SupervisorP.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.SupervisorP.R;

public class HistoryVH extends RecyclerView.ViewHolder  {
    public TextView txt_date,txt_start_time,txt_end_time,txt_option;
    public HistoryVH(@NonNull View itemView) {
        super(itemView);
        txt_date=itemView.findViewById(R.id.txt_ngay);
        txt_start_time=itemView.findViewById(R.id.txt_gio_bd);
        txt_end_time=itemView.findViewById(R.id.txt_gio_kt);
        txt_option=itemView.findViewById(R.id.txt_option);
    }
}
