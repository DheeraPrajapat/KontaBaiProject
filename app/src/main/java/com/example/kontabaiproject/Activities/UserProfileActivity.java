package com.example.kontabaiproject.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.kontabaiproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 111;
    TextView createProfile,createAsDriver;
    EditText fullname,phonenumber;
    CircleImageView imageView;
    String[] manifest={Manifest.permission.CAMERA};
    Uri imageUri;
    AlertDialog alertDialog;
    private static final int PERMISSION_CAMERA_CODE=121;
    StorageReference UserProfileRef;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    Uri ImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initViews();
        imageView.setOnClickListener(v->{
            setImageView();
        });
        createAsDriver.setOnClickListener(view -> startActivity(new Intent(UserProfileActivity.this,DriverSideActivity.class).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        )));
        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName=fullname.getText().toString().trim();
                String num=phonenumber.getText().toString().trim();
                if(fullName.equals("")){
                    fullname.setError("Type your name");
                }
                phonenumber.setEnabled(num.equals(""));
                SaveUserInformation(fullName,num);
            }
        });
    }

    private void openCamera() {
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent1, PERMISSION_CAMERA_CODE);
        intent1.setType("image/*");
    }

    private void checkPermission(int permissionCameraCode)
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,manifest,permissionCameraCode);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PERMISSION_CAMERA_CODE && resultCode==RESULT_OK){
            Bitmap mImageUri = (Bitmap) data.getExtras().get("data");
            if(mImageUri==null){
                Toast.makeText(UserProfileActivity.this, "Please Select the image", Toast.LENGTH_SHORT).show();
            }else {
            imageView.setImageBitmap(mImageUri);}
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            try {// Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                if(imageUri==null){
                    Toast.makeText(UserProfileActivity.this, "Please Select the image", Toast.LENGTH_SHORT).show();
                }else {
                imageView.setImageBitmap(bitmap);}
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void initViews() {
        createProfile=findViewById(R.id.createProfile);
        createAsDriver=findViewById(R.id.createProfileDriver);
        fullname=findViewById(R.id.fullName);
        phonenumber=findViewById(R.id.phoneNumber);
        imageView=findViewById(R.id.profileImage);
        phonenumber.setEnabled(false);
        UserProfileRef= FirebaseStorage.getInstance().getReference().child("Profile Images").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        databaseReference= FirebaseDatabase.getInstance().getReference().child("OnlyUsers");
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
    private void setImageView(){
        checkPermission(PERMISSION_CAMERA_CODE);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PopupMenu popupMenu=new PopupMenu(UserProfileActivity.this,imageView);
        popupMenu.getMenuInflater().inflate(R.menu.popmenu_imageview,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if(menuItem.getItemId()==R.id.fromCamera){
                openCamera();
                return true;
            }else if(menuItem.getItemId()==R.id.fromGallery){
                SelectImage();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }
    @Override
    protected void onStart() {
        super.onStart();
        String number=FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        phonenumber.setText(number);
    }

    private void SaveUserInformation(String name,String number){
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait few seconds we setup your profile!");
        progressDialog.show();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("number",number);
        hashMap.put("imageurl",imageUri.toString());
        if(imageUri!=null){
            UserProfileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                startActivity(new Intent(UserProfileActivity.this,MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}