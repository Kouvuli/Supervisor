package com.example.SupervisorP.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.SupervisorP.R;
import com.example.SupervisorP.activities.EditScheduleActivity;
import com.example.SupervisorP.activities.MainActivity;
import com.example.SupervisorP.adapters.ScheduleAdapter;
import com.example.SupervisorP.firebase.ScheduleDBM;
import com.example.SupervisorP.models.Schedule;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


public class ScheduleFragment extends Fragment {
    MainActivity main;
    RecyclerView recyclerView;
    ScheduleAdapter scheduleAdapter;
    TextInputEditText edtDate;
    String date;
    TextInputLayout edtDateLayout;
    ImageButton addButton;
    ScheduleDBM scheduleDBM;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean isLoading=false;
    String key = null;
    Context context;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            main = (MainActivity) getActivity();
            context = getActivity();

        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipe);
        recyclerView=(RecyclerView) view.findViewById(R.id.rv);
        edtDateLayout=(TextInputLayout) view.findViewById(R.id.layoutDate);
        edtDate=(TextInputEditText) view.findViewById(R.id.edtDate);
        addButton =(ImageButton)view.findViewById(R.id.add_btn);
        date=getActivity().getIntent().getStringExtra("DATE");
        recyclerView.setHasFixedSize(true);
        scheduleDBM=new ScheduleDBM();
        scheduleAdapter=new ScheduleAdapter(context);
        recyclerView.setAdapter(scheduleAdapter);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        loadData();
        setListener();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
          @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItem = linearLayoutManager.getItemCount();
                int lastVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (totalItem < lastVisible + 3) {
                    if (!isLoading) {
                        isLoading = true;
                        loadData();

                    }
                }
            }
        });
        return view;
    }

    private void setListener() {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.DatePickerStyle)
                .build();

        if(date==null){
            edtDate.setText(main.dateSche);
        }
        else {
            edtDate.setText(date);
            main.dateSche=date;
        }
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
        edtDateLayout.setEndIconOnClickListener(view -> {
            datePicker.show(((AppCompatActivity)context).getSupportFragmentManager(),"Material_Date_Picker");
            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    calendar.setTimeInMillis(selection);
                    String date=format1.format(calendar.getTime());
                    edtDate.setText(date);
                    main.dateSche=date;
                    //scheduleAdapter.getFilter().filter(edtDate.getText().toString());
                    loadData();
                }
            });
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, EditScheduleActivity.class);
                Schedule schedule=new Schedule();
                schedule.setDate(edtDate.getText().toString());
                intent.putExtra("EDIT",schedule);
                context.startActivity(intent);
            }
        });
    }

    public void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        scheduleAdapter.getFilter().filter(edtDate.getText().toString());
        scheduleDBM.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Schedule> schedules = new ArrayList<>();
                for (DataSnapshot data:snapshot.getChildren()){
                    Schedule schedule=data.getValue(Schedule.class);
                    schedule.setKey(data.getKey());
                    schedules.add(schedule);
                    key=data.getKey();
                }
                scheduleAdapter.setItems(schedules);
                scheduleAdapter.notifyDataSetChanged();

                isLoading=false;
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}
