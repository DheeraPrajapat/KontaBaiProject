package com.example.kontabaiproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kontabaiproject.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverSideActivity extends AppCompatActivity {
    EditText fullname,carnumber,mobilenumber;
    TextView submit;
    ImageView addCarImage;
    CircleImageView driverImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_side);
        initViews();
    }

    private void initViews() {
        fullname=findViewById(R.id.fullDriverName);
        carnumber=findViewById(R.id.driverCarNumber);
        mobilenumber=findViewById(R.id.phoneDriverNumber);
        submit=findViewById(R.id.createDriverProfile);
        addCarImage=findViewById(R.id.carImage);
        driverImage=findViewById(R.id.profileDriverImage);
    }

    private void getData(){
        String driverName=fullname.getText().toString();
        String carNo=carnumber.getText().toString();
        String mobile=mobilenumber.getText().toString();

    }
}