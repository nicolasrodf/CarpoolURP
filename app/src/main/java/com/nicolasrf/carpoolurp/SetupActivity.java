package com.nicolasrf.carpoolurp;

import android.Manifest;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import java.io.ByteArrayOutputStream;
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

    FirebaseDatabase database;
    DatabaseReference users;

    //Firebase storage
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    FirebaseAuth mAuth;

    Bitmap imageBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        setTitle("Setup Activity");

        mAuth = FirebaseAuth.getInstance();
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
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermission();
                    } else {
                        dispatchTakePictureIntent();
                    }
                } else {
                    dispatchTakePictureIntent();
                }

//                chooseImageAndUpload();

            }
        });

    }

    //*Camera permission*//
    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                MY_CAMERA_REQUEST_CODE);

    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SetupActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    dispatchTakePictureIntent();
                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }

                }
                break;
        }

    }

    private void uploadImageToStorage() {

        if(imageBitmap!=null) {
            Log.d(TAG, "uploadImageToStorage: started");
            Log.d(TAG, "uploadImageToStorage: IMAGE BITMAP " + imageBitmap);

            final ProgressDialog mDialog = new ProgressDialog(this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

            /*Estoy alomacenando el bitmap de la foto*/
            /*Para el caso de las imagenes de carpeta, se almacena el Uri de la imagen (en el blogApp la comprimen!)*/

            //get bytes from bitmap.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                        DatabaseReference userInformation = FirebaseDatabase.getInstance().getReference("users");
                        userInformation.child(user_id)
                                .updateChildren(avatarUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(SetupActivity.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                                            //finish(); //finish activity.
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
            Toast.makeText(this, "No hay foto.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            setupImage.setImageBitmap(imageBitmap);
            Log.d(TAG, "onActivityResult: IMAGE BITMAP " + imageBitmap);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, MY_CAMERA_REQUEST_CODE);
        }
    }

    private void showUserInformation() {

        //Todo. el loading progress no esta funcionando. (creo)
        loadingProgress.setVisibility(View.VISIBLE);


        //Todo. si User no ha puesto foto en el InitialSetup, al ver su Setup se reinicia la app!

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
                String avatarUrl = user.getAvatarUrl();
                Common.currentUser.setAvatarUrl(avatarUrl);
                Picasso.with(getBaseContext())
                        .load(Common.currentUser.getAvatarUrl())
                        .into(setupImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SetupActivity.this, "Database error." + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
