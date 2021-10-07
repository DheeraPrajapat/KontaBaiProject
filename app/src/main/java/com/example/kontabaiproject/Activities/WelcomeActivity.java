package com.example.kontabaiproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.kontabaiproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }
    @Override
    protected void onStart() {
        super.onStart();
        Handler handler=new Handler();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser==null){
            handler.postDelayed(() -> {
                startActivity(new Intent(WelcomeActivity.this,RegistrationActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            },1000);
        }  if(firebaseUser!=null){
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("OnlyUsers").child(firebaseUser.getPhoneNumber());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        startActivity(new Intent(WelcomeActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }else{
                        startActivity(new Intent(WelcomeActivity.this,UserProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                    finish();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(WelcomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}