package com.example.SupervisorP.firebase;

import com.example.SupervisorP.models.Schedule;
import com.example.SupervisorP.utilities.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class ScheduleDBM {
    private DatabaseReference databaseReference;
    public ScheduleDBM()
    {
        FirebaseDatabase db =FirebaseDatabase.getInstance("https://supervisor-fa34d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = db.getReference(Schedule.class.getSimpleName());
    }
    public Task<Void> add(Schedule schedule)
    {
        return databaseReference.push().setValue(schedule);
    }

    public Task<Void> update(String key, HashMap<String ,Object> hashMap)
    {
        return databaseReference.child(key).updateChildren(hashMap);
    }
    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    public Query get(String key)
    {
        if(key == null)
        {

            return databaseReference.orderByKey().limitToFirst(8);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(8);
    }

    public Task<DataSnapshot> getFlag(String key)
    {
        return databaseReference.child(key).child(Constants.KEY_FLAG).get();
    }
}
