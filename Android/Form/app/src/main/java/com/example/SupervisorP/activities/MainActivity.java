package com.example.SupervisorP.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.SupervisorP.R;
import com.example.SupervisorP.databinding.ActivityMainBinding;
import com.example.SupervisorP.fragments.HistoryFragment;
import com.example.SupervisorP.fragments.ScheduleFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    public String dateHis;
    public String dateSche;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(MaterialDatePicker.todayInUtcMilliseconds());
        SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
        String today=format1.format(calendar.getTime());
        dateHis=today;
        dateSche=today;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ScheduleFragment()).commit();
        setListener();
    }

    private void setListener() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment=null;
                if(tab.getPosition()==0){
                    selectedFragment=new ScheduleFragment();
                }
                else{
                    selectedFragment=new HistoryFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}