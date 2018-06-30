package com.nicolasrf.carpoolurp;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nicolasrf.carpoolurp.model.Request;
import com.nicolasrf.carpoolurp.model.Trip;
import com.nicolasrf.carpoolurp.utils.FirebaseMethods;

import java.util.ArrayList;
import java.util.List;

public class DetailActiveTripActivity extends AppCompatActivity {
    private static final String TAG = "DetailActiveTripActivit";

    FirebaseDatabase database;
    DatabaseReference driver_trips, trips;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_active_trip);

        //Init firebase database
        database = FirebaseDatabase.getInstance();
        driver_trips = database.getReference("driver_trips");
        trips = database.getReference("trips");

        final TextView addressTextView = findViewById(R.id.address_text_view);
        final TextView dateTextView = findViewById(R.id.date_text_view);
        final TextView timeTextView = findViewById(R.id.time_text_view);

        Bundle extras = getIntent().getExtras();
        trip = extras.getParcelable("trip");
        String address = trip.getAddress();
        String dateString = trip.getDateString();
        String timeString = trip.getTimeString();
        String latLngString = trip.getLatLng();
        final String tripId = trip.getTrip_id();
        List<Request> requestList = trip.getRequests();

        Log.d(TAG, "onCreate: ADDRESS: "+address);
        Log.d(TAG, "onCreate: DATE: "+dateString);
        Log.d(TAG, "onCreate: TIME: "+timeString);
        Log.d(TAG, "onCreate: LATLMG: "+latLngString);
        Log.d(TAG, "onCreate: LIST : "+requestList.size());

        //set text views
        addressTextView.setText(address);
        dateTextView.setText(dateString);
        timeTextView.setText(timeString);

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Button deleteTripButton = findViewById(R.id.delete_trip_button);
        deleteTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //use FirebaseMethods util class
                FirebaseMethods firebaseMethods = new FirebaseMethods(DetailActiveTripActivity.this);
                //Delete in driver_trips node
                firebaseMethods.deleteTrip(tripId,userID);
                //Delete in trips node
                firebaseMethods.deleteTrip(tripId,null);
                //Ir al home, cerrando el activity.
                Intent intent = new Intent(DetailActiveTripActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Button setPastTripButton = findViewById(R.id.set_past_trip_button);
        setPastTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //use FirebaseMethods util class
                FirebaseMethods firebaseMethods = new FirebaseMethods(DetailActiveTripActivity.this);
                //Set in driver_trips node
                firebaseMethods.setFalseActiveTripFieldValue(tripId,userID);
                //Set in trips node
                firebaseMethods.setFalseActiveTripFieldValue(tripId,null);
            }
        });


    }
}
