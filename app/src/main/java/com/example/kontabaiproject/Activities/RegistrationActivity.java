package com.example.kontabaiproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kontabaiproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {
    EditText mobileNumber;
    TextView nextButton;
    FirebaseAuth firebaseAuth;
    String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
        nextButton.setOnClickListener(view -> {
            String number = mobileNumber.getText().toString();
            if (mobileNumber.getText().toString().equals("")) {
                mobileNumber.setError("Enter the mobile number!");
            } else if (mobileNumber.getText().toString().length() != 10) {
                mobileNumber.setError("Enter the valid number!");
            } else {
                registrationUserMobile(number);
            }
        });
    }
    private void registrationUserMobile(String number) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth).
                setPhoneNumber("+91" + number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
        }
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId=s;
            Intent intent=new Intent(RegistrationActivity.this,VerificationActivity.class);
            intent.putExtra("number", mobileNumber.getText().toString());
            intent.putExtra("id",verificationId);
            startActivity(intent);
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(RegistrationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
    private void initViews() {
        mobileNumber = findViewById(R.id.Mobilenumber);
        nextButton = findViewById(R.id.nextButton);
        firebaseAuth = FirebaseAuth.getInstance();
    }
}