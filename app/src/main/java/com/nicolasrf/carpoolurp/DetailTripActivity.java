package com.nicolasrf.carpoolurp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nicolasrf.carpoolurp.model.Request;
import com.nicolasrf.carpoolurp.model.Trip;
import com.nicolasrf.carpoolurp.utils.Utils;

import java.util.Date;
import java.util.List;

public class DetailTripActivity extends AppCompatActivity {

    Trip trip;

    FirebaseDatabase database;
    DatabaseReference driver_trips, trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trip);

        database = FirebaseDatabase.getInstance();
        driver_trips = database.getReference("driver_trips");
        trips = database.getReference("trips");

        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        TextView textView = findViewById(R.id.textView);
        Button requestButton = findViewById(R.id.request_trip_button);

        Bundle extras = getIntent().getExtras();
        trip = extras.getParcelable(RiderDashboardTestActivity.SELECTED_TRIP);
        String address = trip.getAddress();
        String dateString = trip.getDateString();
        String timeString = trip.getTimeString();
        String latLngString = trip.getLatLng();
        final String driver_id = trip.getUser_id();
        final String tripId = trip.getTrip_id();

        Toast.makeText(this, trip.getUser_id(), Toast.LENGTH_SHORT).show();

        textView.setText(address);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newRequestKey = trips.child(tripId).child("requests").push().getKey(); //generate unique key for this request.

                //set active value false in trips node
                Date currentDateGregorian = Utils.getDateGregorian();
                Request request = new Request();
                request.setUser_id(user_id);
                request.setDate_created(currentDateGregorian);
                request.setRequest_id(newRequestKey);

                trips.child(tripId)
                        .child("requests")
                        .child(newRequestKey)
                        .setValue(request)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DetailTripActivity.this, "Request sent.", Toast.LENGTH_SHORT).show();
                            }
                        });
                //
                driver_trips.child(driver_id)
                        .child(tripId)
                        .child("requests")
                        .child(newRequestKey)
                        .setValue(request);

            }
        });


    }
}
