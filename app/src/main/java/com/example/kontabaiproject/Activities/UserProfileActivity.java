package com.example.kontabaiproject.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 111;
    TextView createProfile,createAsDriver;
    EditText fullname,phonenumber;
    CircleImageView imageView;
    Uri imageUri;

    private static final int PERMISSION_CAMERA_CODE=121;
    StorageTask<UploadTask.TaskSnapshot> uploadTask;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initViews();
        imageView.setOnClickListener(v-> setImageView());
        createAsDriver.setOnClickListener(view -> startActivity(new Intent(UserProfileActivity.this,DriverSideActivity.class).addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
        )));
        createProfile.setOnClickListener(view -> {
            String fullName=fullname.getText().toString().trim();
            String num=phonenumber.getText().toString().trim();
            if(fullName.equals("")){
                fullname.setError("Type your name");
            }
            phonenumber.setEnabled(num.equals(""));
            SaveUserInformation(fullName,num);
        });
    }

    private void openCamera() {
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent1, PERMISSION_CAMERA_CODE);
        intent1.setType("image/*");
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, PERMISSION_CAMERA_CODE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PERMISSION_CAMERA_CODE && resultCode==RESULT_OK){
            Uri uri=data.getData();
            imageUri=uri;
            if(imageUri!=null){
                Bitmap bitmap=(Bitmap)data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
            }else{
                Toast.makeText(UserProfileActivity.this, "Image not selected", Toast.LENGTH_SHORT).show();
            }
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
        databaseReference= FirebaseDatabase.getInstance().getReference().child("OnlyUsers").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        storageReference = FirebaseStorage.getInstance().getReference(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber()));

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
        String number= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();
        phonenumber.setText(number);
    }

    private void SaveUserInformation(String name,String number){
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait few seconds we setup your profile!");
        progressDialog.show();

        if(imageUri!=null)
        {
            //DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference().child("AllUsers").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            final StorageReference file=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
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
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("name",name);
                    map.put("number",number);
                    map.put("imageurl",mUri);
                    databaseReference.child("Saved").setValue("user");
                    startActivity(new Intent(UserProfileActivity.this,MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                } else
                {
                    Toast.makeText(UserProfileActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                progressDialog.dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        }else {
            providePhoto();
            progressDialog.dismiss();
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver= UserProfileActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap= MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void providePhoto()
    {
        AlertDialog alertDialog=new AlertDialog.Builder(this,R.style.verification_done).create();
        View view=getLayoutInflater().inflate(R.layout.photo_alertdialogbox,null,false);
        alertDialog.setView(view);
        alertDialog.show();
        alertDialog.setCancelable(false);
        TextView okButton=view.findViewById(R.id.okPhotoButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
}