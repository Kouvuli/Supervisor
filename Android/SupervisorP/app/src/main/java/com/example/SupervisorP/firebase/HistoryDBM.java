package com.example.SupervisorP.firebase;

import com.example.SupervisorP.models.History;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class HistoryDBM {
    private DatabaseReference databaseReference;
    public HistoryDBM()
    {
        FirebaseDatabase db =FirebaseDatabase.getInstance("https://supervisor-fa34d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = db.getReference(History.class.getSimpleName());
    }
    public Task<Void> add(History history)
    {
        return databaseReference.push().setValue(history);
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

    public Query get()
    {
        return databaseReference.orderByKey();
    }
}
