package com.example.kontabaiproject.Activities;

import static com.example.kontabaiproject.Classes.GetImageExtension.getExtension;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.example.kontabaiproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverSideActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST =121 ;
    AlertDialog alertDialog;
    private static final int PERMISSION_CAMERA_CODE = 111;
    EditText fullname,carnumber,mobilenumber;
    TextView submit;
    ImageView addCarImage;
    CircleImageView driverImage;
    Uri imageUri,imageUriCar,imageUriDriver;
    StorageTask<UploadTask.TaskSnapshot> uploadTask;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    String whichImage;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_side);
        initViews();

        addCarImage.setOnClickListener(view -> {
            whichImage="car";
            popMenu(addCarImage);
        });
        driverImage.setOnClickListener(view -> {
            whichImage="driver";
            popMenu(driverImage);
        });
        submit.setOnClickListener(view -> {
            String fullName=fullname.getText().toString();
            String phonenumber=mobilenumber.getText().toString();
            String carNumber=carnumber.getText().toString();
            if(fullName.equals("")) {
                fullname.setError("write your name!");
            }if(carNumber.length()!=10){
                carnumber.setError("Invalid car number");
            }if(carNumber.equals("")){
                carnumber.setError("Enter the car number!");
            }if(phonenumber.equals("")){
                mobilenumber.setEnabled(true);
                mobilenumber.setError("Enter the number!");
            }
            setUpDataInformation(fullName,phonenumber,carNumber);
        });
    }

    private void setUpDataInformation(String fullName, String phonenumber, String carNumber)
    {
        if(imageUriCar!=null){
            setTheCarImage(imageUriCar);
        }if(imageUriDriver!=null){
            setTheDriverImage(imageUriDriver);
        }
        if(imageUriDriver==null || imageUriCar==null){
            providePhoto();
        }
        saveTheData(fullName,phonenumber,carNumber);
    }

    private void saveTheData(String fullName, String phonenumber, String carNumber) {
       if(fullName.equals("") || carNumber.equals("")){
           Toast.makeText(DriverSideActivity.this, "Fill the all required field..", Toast.LENGTH_SHORT).show();
       }else {
           DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("AllUsers").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
           HashMap<String,Object> hashMap=new HashMap<>();
           hashMap.put("name",fullName);
           hashMap.put("number",phonenumber);
           hashMap.put("carnumber",carNumber);
           databaseReference.child("info").updateChildren(hashMap);
           databaseReference1.child("Saved").setValue("driver");
           showAccountActivation();
       }
    }

    private void setTheDriverImage(Uri imageUriDriver) {
        final StorageReference file=storageReference.child(System.currentTimeMillis()+"."+getExtension(this,imageUriDriver));
        uploadTask=file.putFile(imageUriDriver);
        uploadTask.continueWithTask(task -> {
            if(!task.isSuccessful())
            {
                throw Objects.requireNonNull(task.getException());
            }
            return file.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri downloaduri= task.getResult();
                assert downloaduri != null;
                String mUri= downloaduri.toString();
                databaseReference.child("driverimage").setValue(mUri);
            } else
            {
                Toast.makeText(DriverSideActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(DriverSideActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void setTheCarImage(Uri imageUriCar) {
        final StorageReference file=storageReference.child(System.currentTimeMillis()+"."+getExtension(this,imageUriCar));
        uploadTask=file.putFile(imageUri);
        uploadTask.continueWithTask(task -> {
            if(!task.isSuccessful())
            {
                throw Objects.requireNonNull(task.getException());
            }
            return file.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri downloaduri= task.getResult();
                assert downloaduri != null;
                String mUri= downloaduri.toString();
                databaseReference.child("carimage").setValue(mUri);
            } else
            {
                Toast.makeText(DriverSideActivity.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(DriverSideActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }


    private void initViews() {
        fullname=findViewById(R.id.fullDriverName);
        carnumber=findViewById(R.id.driverCarNumber);
        mobilenumber=findViewById(R.id.phoneDriverNumber);
        submit=findViewById(R.id.createDriverProfile);
        addCarImage=findViewById(R.id.carImage);
        driverImage=findViewById(R.id.profileDriverImage);
        storageReference = FirebaseStorage.getInstance().getReference(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber()));
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Drivers").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
    }

    private void popMenu(ImageView imageView) {
        PopupMenu menu=new PopupMenu(DriverSideActivity.this,imageView);
        menu.getMenuInflater().inflate(R.menu.popmenu_imageview,menu.getMenu());
        menu.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getItemId()==R.id.fromCamera){
                openCamera();
                return true;
            }else if(menuItem.getItemId()==R.id.fromGallery){
                SelectImage();
                return true;
            }
            return false;
        });
        menu.show();
    }
    private void SelectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }
    private void openCamera() {
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent1, PERMISSION_CAMERA_CODE);
        intent1.setType("image/*");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PERMISSION_CAMERA_CODE && resultCode==RESULT_OK  && data != null && data.getData() != null ){
            imageUri=data.getData();
            Bitmap mImageUri = null;
            try {
                mImageUri = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(mImageUri==null){
                Toast.makeText(DriverSideActivity.this, "Please Select the image", Toast.LENGTH_SHORT).show();
            }else {
                if(whichImage.equals("driver")){
                    imageUriDriver=imageUri;
                driverImage.setImageBitmap(mImageUri);
                whichImage="";}
                if(whichImage.equals("car")){
                    imageUriCar=imageUri;
                    addCarImage.setImageBitmap(mImageUri);
                    whichImage="";
                }
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            try {// Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                if(imageUri==null){
                    Toast.makeText(DriverSideActivity.this, "Please Select the image", Toast.LENGTH_SHORT).show();
                }else {
                    if(whichImage.equals("driver")){
                        imageUriDriver=imageUri;
                        driverImage.setImageBitmap(bitmap);
                        whichImage="";}
                    if(whichImage.equals("car")){
                        imageUriCar=imageUri;
                        addCarImage.setImageBitmap(bitmap);
                        whichImage="";
                    }}
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String number= FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        mobilenumber.setText(number);
        mobilenumber.setEnabled(false);
    }

    private void providePhoto()
    {
        AlertDialog alertDialog=new AlertDialog.Builder(this,R.style.verification_done).create();
        View view=getLayoutInflater().inflate(R.layout.photo_alertdialogbox,null,false);
        alertDialog.setView(view);
        alertDialog.show();
        alertDialog.setCancelable(false);
        TextView okButton=view.findViewById(R.id.okPhotoButton);
        okButton.setOnClickListener(view1 -> alertDialog.dismiss());
    }

    @SuppressLint("SetTextI18n")
    private void showAccountActivation(){
        alertDialog=new AlertDialog.Builder(DriverSideActivity.this,R.style.verification_done).create();
        View view=getLayoutInflater().inflate(R.layout.confirmation_dialogbox,null,false);
        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.show();
        TextView okButton=view.findViewById(R.id.okButton);
        TextView heading=view.findViewById(R.id.confirmationHeading);
        heading.setText("Account activate succesfully!");
        okButton.setOnClickListener(view1 -> {
            Intent intent=new Intent(DriverSideActivity.this,MainActivity.class);
            startActivity(intent);
            alertDialog.dismiss();
            finish();
        });
    }
}