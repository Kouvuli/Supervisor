package com.example.SupervisorP.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.SupervisorP.databinding.ActivityKeyLogBinding;
import com.example.SupervisorP.models.History;
import com.example.SupervisorP.models.Schedule;

public class KeyLogActivity extends AppCompatActivity {
    private ActivityKeyLogBinding binding;
    History history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityKeyLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        history = (History) getIntent().getSerializableExtra("KEYLOG");
        setData();
    }

    private void setData() {
        binding.txtKeyLog.setText(history.getKeyLog());
    }
}