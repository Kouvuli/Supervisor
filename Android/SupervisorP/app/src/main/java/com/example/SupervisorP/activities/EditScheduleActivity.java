package com.example.SupervisorP.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.SupervisorP.R;
import com.example.SupervisorP.databinding.ActivityEditScheduleBinding;
import com.example.SupervisorP.firebase.ScheduleDBM;
import com.example.SupervisorP.models.Schedule;
import com.example.SupervisorP.utilities.Constants;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class EditScheduleActivity extends AppCompatActivity {
    private ActivityEditScheduleBinding binding;
    Schedule schedule;
    Date timeBg;
    Date timeEd;
    ScheduleDBM scheduleDBM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEditScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        scheduleDBM=new ScheduleDBM();
        schedule = (Schedule) getIntent().getSerializableExtra("EDIT");
        setData();
        setListener();
    }

    private void setData() {
        if(schedule.getTimeStart()==null){
            binding.confirmButton.setText("Thêm");
            return;
        }
        binding.edtBgTime.setText(schedule.getTimeStart());
        binding.edtEdTime.setText(schedule.getTimeEnd());
        binding.edtDuration.setText(schedule.getDuration());
        binding.edtInterruptTime.setText(schedule.getInterruptTime());
        binding.edtSum.setText(schedule.getSum());
    }

    private void setListener() {

        binding.layoutBgTime.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBgTime();
            }
        });
        binding.layoutEdTime.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEdTime();
            }
        });
        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    if(schedule.getTimeStart()==null){
                        Schedule schedule_final = new Schedule(schedule.getDate(),binding.edtDuration.getText().toString(),
                                binding.edtBgTime.getText().toString(),
                                binding.edtEdTime.getText().toString(),
                                binding.edtInterruptTime.getText().toString(),
                                binding.edtSum.getText().toString());
                        scheduleDBM.add(schedule_final).addOnSuccessListener(suc ->
                        {
                            Toast.makeText(getApplicationContext(), "Inserted", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(er ->
                        {
                            Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                    else {
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put(Constants.KEY_DATE,schedule.getDate());
                        hashMap.put(Constants.KEY_BG_TIME,binding.edtBgTime.getText().toString());
                        hashMap.put(Constants.KEY_ED_TIME,binding.edtEdTime.getText().toString());
                        hashMap.put(Constants.KEY_DURATION,binding.edtDuration.getText().toString());
                        hashMap.put(Constants.KEY_SUM_TIME,binding.edtSum.getText().toString());
                        hashMap.put(Constants.KEY_INTERRUPT_TIME, binding.edtInterruptTime.getText().toString());
                        scheduleDBM.update(schedule.getKey(),hashMap).addOnSuccessListener(suc ->
                        {
                            Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_LONG).show();
                            finish();
                        }).addOnFailureListener(er ->
                        {
                            Toast.makeText(getApplicationContext(), "" + er.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                    Intent back_intent=new Intent(EditScheduleActivity.this,MainActivity.class);
                    back_intent.putExtra("DATE",schedule.getDate());
                    startActivity(back_intent);
                }
            }
        });
    }

    private boolean isValid() {

        if(binding.edtBgTime.getText().toString().isEmpty()||binding.edtEdTime.getText().toString().isEmpty()||
        binding.edtInterruptTime.getText().toString().isEmpty()||binding.edtSum.getText().toString().isEmpty()||
        binding.edtDuration.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),"Thiếu dữ liệu nhập",Toast.LENGTH_LONG).show();
            return false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        try {
            timeEd = dateFormat.parse(binding.edtEdTime.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            timeBg = dateFormat.parse(binding.edtBgTime.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff=timeEd.getTime()-timeBg.getTime();
        if (diff<0){
            Toast.makeText(getApplicationContext(), "Thời gian kết thúc trước thời gian bắt đầu", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            int timeInSeconds = (int) diff / 1000;
            int hours, minutes;
            hours = timeInSeconds / 3600;
            timeInSeconds = timeInSeconds - (hours * 3600);
            minutes = timeInSeconds / 60;
            minutes+=hours*60;
            if (minutes<Integer.parseInt(binding.edtSum.getText().toString())){
                Toast.makeText(getApplicationContext(), "Tổng thời gian tối đa được sử dụng nhiều hơn khoảng thời gian cho phép", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (minutes < Integer.parseInt(binding.edtDuration.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Thời gian tối đa được sử dụng lớn hơn khoảng thời gian cho phép", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (Integer.parseInt(binding.edtDuration.getText().toString()) > Integer.parseInt(binding.edtSum.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Tổng thời gian tối đa ít hơn thời gian tối da được sử dụng", Toast.LENGTH_SHORT).show();
                return false;
            }

        }
        return true;
    }

    private void selectEdTime() {
        if (schedule.getTimeEnd() == null) {
            Calendar calendar=Calendar.getInstance();
            int hour=calendar.get(Calendar.HOUR_OF_DAY);
            int minute=calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    binding.edtEdTime.setText(hourOfDay + ":" + minute );
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                        timeEd = dateFormat.parse(binding.edtEdTime.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, hour, minute, true);
            timePickerDialog.show();

        }
        else{
            Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
            try {
                timeEd = dateFormat.parse(schedule.getTimeEnd());
                calendar.setTime(timeEd);

            } catch (ParseException e) {
            }
            TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    binding.edtEdTime.setText(hourOfDay + ":" + minute );
                    try {
                        timeEd = dateFormat.parse(binding.edtEdTime.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            };
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }

    }

    private void selectBgTime() {
        if (schedule.getTimeStart() == null) {
            Calendar calendar=Calendar.getInstance();
            int hour=calendar.get(Calendar.HOUR_OF_DAY);
            int minute=calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    binding.edtBgTime.setText(hourOfDay + ":" + minute );
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                        timeBg = dateFormat.parse(binding.edtBgTime.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, hour, minute, true);
            timePickerDialog.show();

        }
        else {
            Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
            try {
                timeBg = dateFormat.parse(schedule.getTimeStart());
                calendar.setTime(timeBg);

            } catch (ParseException e) {
            }
            TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    binding.edtBgTime.setText(hourOfDay + ":" + minute );
                    try {
                        timeBg = dateFormat.parse(binding.edtBgTime.getText().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            };
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                    timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }

    }
}