package com.example.chatfirebase;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountProfileActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    FirebaseUser mUser;

    CircleImageView imgProfile;
    TextView tvDisplayName, tvStatus;
    Button btnChangeImage, btnChangeStatus;

    public static final int GALLERY_PICK = 1;

    StorageReference mStorage;

    ProgressDialog mDialog;

    String downloadImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);

        AnhXa();

        mDialog = new ProgressDialog(AccountProfileActivity.this);
        mDialog.setTitle("Uploading");
        mDialog.setMessage("Waiting for upload image.");
        mDialog.setCanceledOnTouchOutside(false);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("img_avatar").getValue().toString();
                String name = dataSnapshot.child("name").getValue().toString();
                final String status = dataSnapshot.child("status").getValue().toString();

                tvDisplayName.setText(name);
                tvStatus.setText(status);
                if (image.equals("default")) {
                    imgProfile.setImageResource(R.drawable.avatar);
                }else {
                    Picasso.with(AccountProfileActivity.this).load(image).placeholder(R.drawable.avatar).into(imgProfile);
                }
                btnChangeStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AccountProfileActivity.this, StatusActivity.class);
                        intent.putExtra("status", status);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStorage = FirebaseStorage.getInstance().getReference();

        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String uid = mUser.getUid();

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mDialog.show();

                File thumb_filePath = new File(resultUri.getPath());

                String curent_uid = mUser.getUid();

                final StorageReference filePath = mStorage.child("profile_images").child(uid + ".jpg");
                UploadTask uploadTask = filePath.putFile(resultUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AccountProfileActivity.this, "Profile image uploaded successfully.",Toast.LENGTH_SHORT).show();
                            downloadImageUrl = task.getResult().toString();

                            mDatabase.child("img_avatar")
                                    .setValue(downloadImageUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(AccountProfileActivity.this,"Image save in database, Successfuly...",Toast.LENGTH_SHORT).show();
                                                mDialog.dismiss();}
                                            else{
                                                String message = task.getException().toString();
                                                Toast.makeText(AccountProfileActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();

                                                mDialog.dismiss();
                                            }
                                        }
                                    });
                        } else {
                            String message = task.getException().toString();

                            Toast.makeText(AccountProfileActivity.this, "Error: "+message,Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void AnhXa() {
        imgProfile = findViewById(R.id.profile_image);
        tvDisplayName = findViewById(R.id.tvDisplayName);
        tvStatus = findViewById(R.id.tvStatus);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        btnChangeStatus = findViewById(R.id.btnChangeStatus);
    }
}
