package com.nicolasrf.carpoolurp;

import android.*;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.nicolasrf.carpoolurp.model.Car;
import com.nicolasrf.carpoolurp.model.User;
import com.squareup.picasso.Callback;
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



public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int MY_CAMERA_REQUEST_CODE = 1001;

    EditText nameSetup;
    EditText phoneSetup;
    Switch userModeSwitch;
    CircleImageView setupImage;

    private ProgressBar setup_progress;
    private ProgressBar loadingInfoProgress;
    private ProgressBar loadingImageProgress;

    FirebaseDatabase database;
    DatabaseReference users;
    DatabaseReference cars;

    //Firebase storage
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    private Bitmap compressedImageFile;
    Uri imageUri;
    private boolean isChanged = false;
    String avatarUrl; //para leer y para poder borrarlo

    Button driverInfoButton;

    String userID;

    boolean carExists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        //Database
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
        cars = database.getReference("cars");

        //Init Firebase storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        nameSetup = findViewById(R.id.setup_name);
        phoneSetup = findViewById(R.id.setup_phone);
        userModeSwitch = findViewById(R.id.user_mode_switch);
        setupImage = findViewById(R.id.setup_image);
        setup_progress = findViewById(R.id.setup_progress);
        loadingInfoProgress = findViewById(R.id.loading_info_progress);
        loadingImageProgress = findViewById(R.id.loading_image_progress);

        loadingInfoProgress.setVisibility(View.VISIBLE);
        loadingImageProgress.setVisibility(View.VISIBLE);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        showUserInformation();

        Button saveSettingsButton = findViewById(R.id.setup_btn);
        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToStorage();
                setupUserInformation();
            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: launching camera.");
                //Check permission
                checkPermissions();
            }
        });

        //Check switch status when change on / off
        userModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Check user mode locally checked
                if(!isChecked){
                    driverInfoButton.setVisibility(View.GONE);

                } else {
                    //Show "Datos de Conductor" Button
                    driverInfoButton.setVisibility(View.VISIBLE);
                    //Initialize button and show dialog to get and set driver information!
                }
            }
        });

        driverInfoButton = findViewById(R.id.driver_info_button);

        driverInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Dialog.-
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Datos de conductor");
                View itemView = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.driver_info_layout,null);

                final TextInputEditText carBrandTextInput = itemView.findViewById(R.id.car_brand_text_input);
                final TextInputEditText carModelTextInput = itemView.findViewById(R.id.car_model_text_input);
                final TextInputEditText carLicenseTextInput = itemView.findViewById(R.id.car_license_text_input);
                final TextInputEditText carColorTextInput = itemView.findViewById(R.id.car_color_text_input);

                final ProgressBar carProgressBar = itemView.findViewById(R.id.car_progress_bar);
                carProgressBar.setVisibility(View.VISIBLE);

                //getCarInformation.-
                cars.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Toast.makeText(ProfileActivity.this, "Carro existe.", Toast.LENGTH_SHORT).show();
                            carExists = true;
                            //get information
                            cars.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Car car = dataSnapshot.getValue(Car.class);
                                    String brand = car.getBrand();
                                    String model = car.getModel();
                                    String color = car.getColor();
                                    String license = car.getLicense();
                                    carBrandTextInput.setText(brand);
                                    carModelTextInput.setText(model);
                                    carColorTextInput.setText(color);
                                    carLicenseTextInput.setText(license);

                                    carProgressBar.setVisibility(View.INVISIBLE);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(ProfileActivity.this, "Error: "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            });

                        } else {
                            carExists = false;
                            //Toast.makeText(ProfileActivity.this, "NO existe carro asociado.", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ProfileActivity.this, "Database error." + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        carProgressBar.setVisibility(View.INVISIBLE);
                    }
                });

                builder.setView(itemView);
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String brand = carBrandTextInput.getText().toString();
                        String model = carModelTextInput.getText().toString();
                        String color = carColorTextInput.getText().toString();
                        String license = carLicenseTextInput.getText().toString();

                        if(!TextUtils.isEmpty(brand)&&!TextUtils.isEmpty(model)&&!TextUtils.isEmpty(color)
                                &&!TextUtils.isEmpty(license)) {

                            Car car = new Car();
                            car.setBrand(carBrandTextInput.getText().toString());
                            car.setModel(carModelTextInput.getText().toString());
                            car.setColor(carColorTextInput.getText().toString());
                            car.setLicense(carLicenseTextInput.getText().toString());

                            if (!carExists) {

                                //add to firebase database.
                                cars.child(userID)
                                        .setValue(car)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProfileActivity.this, "Car created.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProfileActivity.this, "Error creating car: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                //Update car info
                                Map<String, Object> updateCarInfo = new HashMap<>();

                                updateCarInfo.put("brand", brand);
                                updateCarInfo.put("model", model);
                                updateCarInfo.put("color", color);
                                updateCarInfo.put("license", license);

                                cars.child(userID)
                                        .updateChildren(updateCarInfo)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProfileActivity.this, "Information updated.", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProfileActivity.this, "Error: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        } else {
                            Toast.makeText(ProfileActivity.this, "Debe completar todos los campos.", Toast.LENGTH_SHORT).show();
                        }
                    }


                });

                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ProfileActivity.this, "Cancelado.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });


    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(ProfileActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                bringImagePicker();
            }
        } else {
            bringImagePicker();
        }
    }

    private void bringImagePicker() {

        //Set crop properties.
        CropImage.activity()
                .setInitialCropWindowPaddingRatio(0)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setActivityTitle("RECORTAR")
                .setCropMenuCropButtonTitle("OK")
                .start(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ProfileActivity.this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToStorage() {

        Log.d(TAG, "uploadImageToStorage: started.");

        //checuqeo antes si no son nulos
        if (imageUri != null) {

            final ProgressDialog mDialog = new ProgressDialog(ProfileActivity.this);
            mDialog.setMessage("Uploading...");
            mDialog.show();

            //Si cambio la image.
            if (isChanged) {

                final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                File newImageFile = new File(imageUri.getPath());
                try {
                    compressedImageFile = new Compressor(ProfileActivity.this)
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
                                                Toast.makeText(ProfileActivity.this, "Uploaded !", Toast.LENGTH_SHORT).show();
                                                //Todo. intent hacia Home.-
                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Uploaded error.", Toast.LENGTH_SHORT).show();
                                            }

                                            setup_progress.setVisibility(View.INVISIBLE);
                                        }
                                    });


                        } else {

                            String error = task.getException().getMessage();
                            Toast.makeText(ProfileActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();
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

            }

        } else {
            Toast.makeText(ProfileActivity.this, "No image selected.", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                setupImage.setImageURI(imageUri);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(ProfileActivity.this, "Error crop image.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showUserInformation() {
        Log.d(TAG, "showUserInformation: LOADING ACCOUNT SETTINGS");

        //Todo. el loading progress no esta funcionando. (creo)
//        loadingInfoProgress.setVisibility(View.VISIBLE);

        //Todo. si User no ha puesto foto en el InitialSetup, al ver su Setup se reinicia la app!
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
                }

                //avatarUrl
                avatarUrl = user.getAvatarUrl();
                Common.currentUser.setAvatarUrl(avatarUrl);
                Picasso.with(ProfileActivity.this)
                        .load(Common.currentUser.getAvatarUrl())
                        .fit()
                        .into(setupImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                loadingImageProgress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(ProfileActivity.this, "Error loading image.", Toast.LENGTH_SHORT).show();
                            }
                        });

                loadingInfoProgress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Database error." + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setupUserInformation() {

        String name = nameSetup.getText().toString();
        String phone = phoneSetup.getText().toString();

        setup_progress.setVisibility(View.VISIBLE);

        Map<String, Object> updateInfo = new HashMap<>();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {
            updateInfo.put("name", name);
            updateInfo.put("phone", phone);
        }

        if (!userModeSwitch.isChecked()) {
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

                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Information updated !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Information update failed.", Toast.LENGTH_SHORT).show();
                        }

                        setup_progress.setVisibility(View.INVISIBLE);
                    }
                });

    }
}