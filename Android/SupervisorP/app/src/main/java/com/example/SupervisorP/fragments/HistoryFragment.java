package com.example.SupervisorP.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.SupervisorP.R;
import com.example.SupervisorP.activities.MainActivity;
import com.example.SupervisorP.adapters.HistoryAdapter;
import com.example.SupervisorP.firebase.HistoryDBM;
import com.example.SupervisorP.models.History;
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


public class HistoryFragment extends Fragment {
    MainActivity main;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;
    TextInputEditText edtDate;
    TextInputLayout edtDateLayout;
    HistoryDBM historyDBM;
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
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipe);
        recyclerView=(RecyclerView) view.findViewById(R.id.rv);
        edtDateLayout=(TextInputLayout) view.findViewById(R.id.layoutDate);
        edtDate=(TextInputEditText) view.findViewById(R.id.edtDate);
        recyclerView.setHasFixedSize(true);
        historyDBM=new HistoryDBM();
        historyAdapter=new HistoryAdapter(context);
        recyclerView.setAdapter(historyAdapter);
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
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(MaterialDatePicker.todayInUtcMilliseconds());
        SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
        String today=format1.format(calendar.getTime());
        edtDate.setText(main.dateHis);
        edtDateLayout.setEndIconOnClickListener(view -> {
            datePicker.show(((AppCompatActivity)context).getSupportFragmentManager(),"Material_Date_Picker");
            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    calendar.setTimeInMillis(selection);
                    String date=format1.format(calendar.getTime());
                    edtDate.setText(date);
                    main.dateHis=date;
                    historyAdapter.getFilter().filter(edtDate.getText().toString());
                }
            });
        });
    }

    public void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        historyDBM.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<History> histories = new ArrayList<>();
                for (DataSnapshot data:snapshot.getChildren()){
                    History history=data.getValue(History.class);
                    history.setKey(data.getKey());
                    histories.add(history);
                    key=data.getKey();
                }
                historyAdapter.setItems(histories);
                historyAdapter.notifyDataSetChanged();
                isLoading=false;
                swipeRefreshLayout.setRefreshing(false);
                historyAdapter.getFilter().filter(edtDate.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}