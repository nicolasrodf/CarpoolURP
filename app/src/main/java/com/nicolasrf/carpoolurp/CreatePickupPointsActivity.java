package com.nicolasrf.carpoolurp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class CreatePickupPointsActivity extends AppCompatActivity {
    private static final String TAG = "CreatePickupPointsActiv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pickup_points);

        //No volidar q antes de finalizar la creacion del viaje se debe UN CAMPO poner como ACTIVE TRIP = TRUE;

        //get data from intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String placeAddress = bundle.getString("placeAddress");
        LatLng placeLatLng = (LatLng) bundle.get("placeLatLng");
        Date date  = (Date) bundle.get("date");
        String time  = bundle.getString("time");
        Integer seats  = bundle.getInt("seats");
        Integer cost  = bundle.getInt("cost");

        Log.d(TAG, "onCreate: placeAddress: " + placeAddress);
        Log.d(TAG, "onCreate: placeLatLng: " + placeLatLng);
        Log.d(TAG, "onCreate: date: " + date);
        Log.d(TAG, "onCreate: time: " + time);
        Log.d(TAG, "onCreate: seats: " + seats);
        Log.d(TAG, "onCreate: cost: " + cost);


    }
}
