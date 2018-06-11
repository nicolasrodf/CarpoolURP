package com.nicolasrf.carpoolurp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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
import com.google.android.gms.tasks.OnFailureListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SetupActivity extends AppCompatActivity {
    private static final String TAG = "SetupActivity";
    private static final int MY_CAMERA_REQUEST_CODE = 1001;

    EditText nameSetup;
    EditText phoneSetup;
    Switch userModeSwitch;
    CircleImageView setupImage;

    private ProgressBar setup_progress;
    private ProgressBar loadingProgress;

    FirebaseDatabase database;
    DatabaseReference users;

    //Firebase storage
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    FirebaseAuth mAuth;

    private Bitmap compressedImageFile;

    Uri imageUri;

    private boolean isChanged = false;

    String avatarUrl; //para leer y para poder borrarlo

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        setTitle("Setup Activity");

        Log.d(TAG, "onCreate: setup activity started");

        mAuth = FirebaseAuth.getInstance();
        //Database
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");

        //Init Firebase storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        nameSetup = findViewById(R.id.setup_name);
        phoneSetup = findViewById(R.id.setup_phone);
        userModeSwitch = findViewById(R.id.user_mode_switch);
        setupImage = findViewById(R.id.setup_image);
        setup_progress = findViewById(R.id.setup_progress);
        loadingProgress = findViewById(R.id.loading_progress);

        if(mAuth.getCurrentUser()!= null) {
            showUserInformation();
        }

        Button saveSettingsButton = findViewById(R.id.setup_btn);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
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

                //Check permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(SetupActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        bringImagePicker();
                    }
                } else {
                    bringImagePicker();
                }
            }
        });
    }

    private void bringImagePicker() {

        //Set crop properties.
        CropImage.activity()
                .setInitialCropWindowPaddingRatio(0)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setActivityTitle("RECORTAR")
                .setCropMenuCropButtonTitle("OK")
                .start(SetupActivity.this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToStorage() {

        Log.d(TAG, "uploadImageToStorage: started.");

        //checuqeo antes si no son nulos
        if(imageUri != null) {

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            //Si cambio la image.
            if (isChanged) {

                final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                File newImageFile = new File(imageUri.getPath());
                try {
                    compressedImageFile = new Compressor(SetupActivity.this)
                            .setMaxHeight(640)
                            .setMaxWidth(480)
                            .setQuality(50)
                            .compressToBitmap(newImageFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumbData = baos.toByteArray();


                UploadTask image_path = storageReference.child("profile_images").child(user_id + ".jpg").putBytes(thumbData);

                image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            mDialog.dismiss();

                            Uri download_uri;
                            download_uri = task.getResult().getDownloadUrl();
                            Log.d(TAG, "DOWNLOAD URI " + download_uri);

                            Map<String, Object> avatarUpdate = new HashMap<>();
                            avatarUpdate.put("avatarUrl", download_uri.toString());

                            //Update on Firebase database
                            users.child(user_id)
                                    .updateChildren(avatarUpdate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(SetupActivity.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                                                //Todo. intent hacia Home.-
                                            } else {
                                                Toast.makeText(SetupActivity.this, "Uploaded error.", Toast.LENGTH_SHORT).show();
                                            }

                                            setup_progress.setVisibility(View.INVISIBLE);
                                        }
                                    });

                        } else {

                            String error = task.getException().getMessage();
                            Toast.makeText(SetupActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();
                            setup_progress.setVisibility(View.INVISIBLE);

                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        mDialog.setMessage("Updating: " + String.format("%.0f", progress) + "%");

                    }
                });

            } else {
                Toast.makeText(this, "No image selected.", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                setupImage.setImageURI(imageUri);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "Error crop image.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showUserInformation() {
        Log.d(TAG, "showUserInformation: LOADING ACCOUNT SETTINGS");

        //Todo. el loading progress no esta funcionando. (creo)
        loadingProgress.setVisibility(View.VISIBLE);

        //Todo. si User no ha puesto foto en el InitialSetup, al ver su Setup se reinicia la app!
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "showUserInformation: USER ID " + userID);

        //**Despues de obtener la info de firebase, lo asigno al objecto User y con ese object asigno los valores a los widgets.

        //data from firebase
        users.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                //name and phone
                String name = user.getName();
                String phone = user.getPhone();
                //Reasigno los valores para seguir usandolos correctamente en la app! (lo mismo con phone y avatarUrl)
                Common.currentUser.setName(name);
                Common.currentUser.setPhone(phone);
                //Chequeo si se seteo correctamente la info al objeto User y seteo a los edit text.
                nameSetup.setText(Common.currentUser.getName());
                phoneSetup.setText(Common.currentUser.getPhone());

                //user mode
                String userMode = user.getUserMode();
                Common.currentUser.setUserMode(userMode);
                if (Common.currentUser.getUserMode().equals("rider")) {
                    userModeSwitch.setChecked(false);
                } else if (Common.currentUser.getUserMode().equals("driver")) {
                    userModeSwitch.setChecked(true);
                    Common.currentUser.setUserMode("driver");
                }

                //avatarUrl
                avatarUrl = user.getAvatarUrl();
                Common.currentUser.setAvatarUrl(avatarUrl);
                Picasso.with(getBaseContext())
                        .load(Common.currentUser.getAvatarUrl())
                        .fit()
                        .into(setupImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SetupActivity.this, "Database error." + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        loadingProgress.setVisibility(View.INVISIBLE);
    }

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
        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
