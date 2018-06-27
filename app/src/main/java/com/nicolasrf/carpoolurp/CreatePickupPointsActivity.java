package com.nicolasrf.carpoolurp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nicolasrf.carpoolurp.model.Trip;

import java.util.Date;

public class CreatePickupPointsActivity extends AppCompatActivity {
    private static final String TAG = "CreatePickupPointsActiv";

    FirebaseDatabase database;
    DatabaseReference trips;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pickup_points);

        //init firebase database
        database = FirebaseDatabase.getInstance();
        trips = database.getReference("driver_trips");

        mAuth = FirebaseAuth.getInstance();
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //No volidar q antes de finalizar la creacion del viaje se debe UN CAMPO poner como ACTIVE TRIP = TRUE;

        //get data from intent
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        String placeAddress = bundle.getString("placeAddress");
//        LatLng latLng = (LatLng) bundle.get("latLng");
//        Date date  = (Date) bundle.get("date");
//        String dateString = bundle.getString("dateString");
//        String timeString = bundle.getString("timeString");
//        Integer seats  = bundle.getInt("seats");
//        Integer cost  = bundle.getInt("cost");
//
//        Log.d(TAG, "onCreate: placeAddress: " + placeAddress);
//        Log.d(TAG, "onCreate: latLng: " + latLng);
//        Log.d(TAG, "onCreate: date: " + date);
//        Log.d(TAG, "onCreate: dateString" + dateString);
//        Log.d(TAG, "onCreate: timeString: " + timeString);
//        Log.d(TAG, "onCreate: seats: " + seats);
//        Log.d(TAG, "onCreate: cost: " + cost);

        //showTripInformation;

        //uploadToFirebase;

//        Trip trip = new Trip();
//        trip.setAddress(placeAddress);
//        trip.setLatLng(latLng);
//        trip.setDate(date);
//        trip.setDateString(dateString);
//        trip.setTimeString(timeString);
//        trip.setSeats(seats);
//        trip.setCost(cost);
//        trip.setActive(true); //iniciar viaje como active.
//
//        //Se pondrá cada viaje dentro del nodo trip -> usuario.
//        driver_trips.child(user_id) //nodo user_id
//                .push() //get unique key for trip
//                .setValue(trip)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(CreatePickupPointsActivity.this, "Viaje creado exitosamente!", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(CreatePickupPointsActivity.this, "Upload failed.", Toast.LENGTH_SHORT).show();
//                    }
//                });




    }
}
