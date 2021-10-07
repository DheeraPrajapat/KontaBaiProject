package com.example.kontabaiproject.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private static final int PERMISSION_CAMERA_CODE =121 ;
    String[] manifest={Manifest.permission.CAMERA};
    AlertDialog alertDialog;
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

    private void checkPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,manifest, RegistrationActivity.PERMISSION_CAMERA_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CAMERA_CODE && grantResults[0]==PackageManager.PERMISSION_DENIED){
            Log.d("alert","yes");
            alertDialog=new AlertDialog.Builder(this).create();
            View alertView=getLayoutInflater().inflate(R.layout.settings_alert,null,false);
            alertDialog.setView(alertView);
            alertDialog.show();
            alertDialog.setCancelable(false);
            TextView textView=alertView.findViewById(R.id.settingsButton);
            textView.setOnClickListener(v->{
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                alertDialog.dismiss();
                startActivity(intent);
            });
        }else{
            Log.d("alert","no");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }
}