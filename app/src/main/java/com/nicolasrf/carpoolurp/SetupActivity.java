package com.nicolasrf.carpoolurp;

import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nicolasrf.carpoolurp.Common.Common;
import com.nicolasrf.carpoolurp.model.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.security.Permissions;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SetupActivity extends AppCompatActivity {
    private static final String TAG = "SetupActivity";
    private static final int PICK_IMAGE_REQUEST = 1000;
    private static final int MY_CAMERA_REQUEST_CODE = 1001;

    EditText nameSetup;
    EditText phoneSetup;
    Switch userModeSwitch;
    CircleImageView setupImage;

    private ProgressBar setup_progress;
    private ProgressBar loadingProgress;

    //Firebase storage
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Uri saveUri;
    private Bitmap compressedImageFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        setTitle("Setup Activity");

        //Init Firebase storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        nameSetup = findViewById(R.id.setup_name);
        phoneSetup = findViewById(R.id.setup_phone);
        userModeSwitch = findViewById(R.id.user_mode_switch);
        setupImage = findViewById(R.id.setup_image);
        setup_progress = findViewById(R.id.setup_progress);
        loadingProgress = findViewById(R.id.loading_progress);

        loadingProgress.setVisibility(View.VISIBLE);

        showUserInformation();

        Button button = findViewById(R.id.setup_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setupUserInformation();

                uploadImageToStorage();

            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: launching camera.");
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_REQUEST_CODE);
                }
                dispatchTakePictureIntent();

//                chooseImageAndUpload();

            }
        });



    }

    private void uploadImageToStorage() {
        if (saveUri != null) {
            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString(); //Random name image upload
            final StorageReference imageFolder = storageReference.child("images/profile_images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            mDialog.dismiss();

                            imageFolder.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri uri) {

                                            Map<String, Object> avatarUpdate = new HashMap<>();
                                            avatarUpdate.put("avatarUrl", uri.toString());

                                            //Update on Firebase database
                                            DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("users");
                                            userInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .updateChildren(avatarUpdate)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if(task.isSuccessful()){
                                                                Toast.makeText(SetupActivity.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(SetupActivity.this, "Uploaded error.", Toast.LENGTH_SHORT).show();
                                                            }

                                                            setup_progress.setVisibility(View.INVISIBLE);
                                                        }
                                                    });

                                        }
                                    });

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            mDialog.setMessage("Uploaded: " + String.format("%.0f", progress) + "%");

                        }
                    });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            setupImage.setImageBitmap(imageBitmap);

            saveUri = data.getData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, MY_CAMERA_REQUEST_CODE);
        }
    }

    private void showUserInformation() {
        //set name and phone
        nameSetup.setText(Common.currentUser.getName());
        phoneSetup.setText(Common.currentUser.getPhone());

        //set user mode
        if(Common.currentUser.getUserMode().equals("rider")){
            userModeSwitch.setChecked(false);
        } else if (Common.currentUser.getUserMode().equals("driver")){
            userModeSwitch.setChecked(true);
        }


        Log.d(TAG, "showUserInformation: " + Common.currentUser.getAvatarUrl());
        //But with Avatar, we just check it with null or empty
        if (Common.currentUser.getAvatarUrl() != null && !TextUtils.isEmpty(Common.currentUser.getAvatarUrl())) {
            Picasso.with(this)
                    .load(Common.currentUser.getAvatarUrl())
                    .into(setupImage);
        }

        loadingProgress.setVisibility(View.INVISIBLE);

    }



//    private void chooseImageAndUpload() {
//
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//            startActivityForResult(Intent.createChooser(intent, "Select Picture: "), PICK_IMAGE_REQUEST);
//        }
//    }
//

    private void setupUserInformation() {

        String name = nameSetup.getText().toString();
        String phone = phoneSetup.getText().toString();

        setup_progress.setVisibility(View.VISIBLE);

        Map<String,Object> updateInfo = new HashMap<>();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)){
            updateInfo.put("name", name);
            updateInfo.put("phone", phone);
        }

        if(!userModeSwitch.isChecked()){
            updateInfo.put("userMode", "rider");
        } else {
            updateInfo.put("userMode", "driver");
        }

        //Update on Firebase database
        DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("users");
        userInformation.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(updateInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(SetupActivity.this, "Information updated !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SetupActivity.this, "Information update failed.", Toast.LENGTH_SHORT).show();
                        }

                        setup_progress.setVisibility(View.INVISIBLE);
                    }
                });

    }
}
