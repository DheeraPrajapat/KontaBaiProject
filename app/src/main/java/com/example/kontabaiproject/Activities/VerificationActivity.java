package com.example.kontabaiproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kontabaiproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class VerificationActivity extends AppCompatActivity {
    TextView verifyButton,backButton;
    EditText verificationCode;
    AlertDialog alertDialog;
    FirebaseAuth firebaseAuth;
    String mobilenumber,verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        initViews();
        verifyButton.setOnClickListener(view -> {
            String verification=verificationCode.getText().toString();
            if(verification.equals("")){
                verificationCode.setError("Enter verification code!");
            }else if(verification.length()!=6){
                verificationCode.setError("Enter valid code!");
            }else {
                verifyCode(verification);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerificationActivity.this,RegistrationActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }

    private void initViews()
    {
        verifyButton=findViewById(R.id.submitButton);
        firebaseAuth=FirebaseAuth.getInstance();
        backButton=findViewById(R.id.backButton);
        verificationCode=findViewById(R.id.verificationCode);
        mobilenumber=getIntent().getStringExtra("mobile");
        verificationId=getIntent().getStringExtra("id");
    }
    private void verifyCode(String code) {
        PhoneAuthCredential authCredential= PhoneAuthProvider.getCredential(verificationId,code);
        signWithPhoneCredentail(authCredential);
    }

    private void signWithPhoneCredentail(PhoneAuthCredential authCredential)
    {
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    alertDialog=new AlertDialog.Builder(VerificationActivity.this).create();
                    View view=getLayoutInflater().inflate(R.layout.confirmation_dialogbox,null,false);
                    alertDialog.setView(view);
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    TextView okButton=view.findViewById(R.id.okButton);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent=new Intent(VerificationActivity.this,UserProfileActivity.class);
                            intent.putExtra("number",mobilenumber);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            alertDialog.dismiss();
                            finish();
                        }
                    });
                }else{
                    Toast.makeText(VerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}