package com.example.SupervisorP.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.SupervisorP.databinding.ActivityScheduleInfoBinding;
import com.example.SupervisorP.models.Schedule;

public class ScheduleInfoActivity extends AppCompatActivity {
    private ActivityScheduleInfoBinding binding;
    Schedule schedule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityScheduleInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        schedule = (Schedule) getIntent().getSerializableExtra("INFO");
        setData();
    }

    private void setData() {
        binding.txtDate.setText(schedule.getDate());
        binding.txtStartTime.setText(schedule.getTimeStart());
        binding.txtEndTime.setText(schedule.getTimeEnd());
        binding.txtDuration.setText(schedule.getDuration());
        binding.txtInterruptTime.setText(schedule.getInterruptTime());
        binding.txtSumTime.setText(schedule.getSum());
    }

}