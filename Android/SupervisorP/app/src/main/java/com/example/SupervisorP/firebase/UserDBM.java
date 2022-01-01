package com.example.SupervisorP.firebase;

import com.example.SupervisorP.models.History;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class UserDBM {
    private DatabaseReference databaseReference;
    public UserDBM(){
        FirebaseDatabase db =FirebaseDatabase.getInstance("https://supervisor-fa34d-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = db.getReference("User");
    }
    public Task<DataSnapshot> getPassword(){
        return databaseReference.child("passParent").get();
    }
}
