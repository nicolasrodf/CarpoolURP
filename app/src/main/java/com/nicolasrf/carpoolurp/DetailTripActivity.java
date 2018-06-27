package com.nicolasrf.carpoolurp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nicolasrf.carpoolurp.model.Trip;

public class DetailTripActivity extends AppCompatActivity {

    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_trip);

        TextView textView = findViewById(R.id.textView);
        Button requestButton = findViewById(R.id.request_trip_button);

        Bundle extras = getIntent().getExtras();
        trip = extras.getParcelable(DriverTripsActivity.SELECTED_TRIP);
        String address = trip.getAddress();

        Toast.makeText(this, trip.getAddress(), Toast.LENGTH_SHORT).show();

        textView.setText(address);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
}
