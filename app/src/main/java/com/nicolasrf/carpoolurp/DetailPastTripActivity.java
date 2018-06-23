package com.nicolasrf.carpoolurp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.nicolasrf.carpoolurp.model.Trip;

public class DetailPastTripActivity extends AppCompatActivity {

    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_past_trip);

        Bundle extras = getIntent().getExtras();
        trip = extras.getParcelable(TripHistoryActivity.SELECTED_TRIP);

        Toast.makeText(this, trip.getAddress(), Toast.LENGTH_SHORT).show();

//        TextView startAddressTextView = (TextView) findViewById(R.id.startAddressTextView);
//        TextView endAddressTextView = (TextView) findViewById(R.id.destinationAddressTextView);
//        TextView extraInfoTextView = (TextView) findViewById(R.id.extraTripInfoTextView);
//        TextView tripCostTextView = (TextView) findViewById(R.id.tripCostTextView);
//        TextView driverNameTextView = (TextView) findViewById(R.id.driverNameTextView);
//
//        String tripCostText = "S/. " + trip.getTripCost();
//
//        startAddressTextView.setText(trip.getStartAddress());
//        endAddressTextView.setText(trip.getEndAddress());
//        extraInfoTextView.setText(trip.getExtraInfo());
//        tripCostTextView.setText(tripCostText);
//        driverNameTextView.setText(trip.getDriverName());

    }
}
