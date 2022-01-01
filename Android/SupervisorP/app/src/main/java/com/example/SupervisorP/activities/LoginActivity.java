package com.example.SupervisorP.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.SupervisorP.databinding.ActivityLoginBinding;
import com.example.SupervisorP.firebase.UserDBM;
import com.example.SupervisorP.utilities.Constants;
import com.example.SupervisorP.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;


public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private UserDBM userDBM;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager=new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGN_IN)){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        userDBM=new UserDBM();
        setListener();
    }

    private void setListener() {
        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                if (isValid()){
                    userDBM.getPassword().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()){

                                if (binding.edtPassword.getText().toString().trim().equals(task.getResult().getValue().toString())) {
                                    preferenceManager.putBoolean(Constants.KEY_IS_SIGN_IN,true);
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                                }
                                loading(false);
                            }
                            else{
                                loading(false);
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private boolean isValid(){
        if (binding.edtPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Chưa nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void loading(boolean isLoad){
        if (isLoad){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.signInButton.setVisibility(View.INVISIBLE);
        }
        else{
            binding.signInButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}