package com.nicolasrf.carpoolurp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nicolasrf.carpoolurp.model.Trip;

import java.util.Date;

public class TripHistoryActivity extends AppCompatActivity {
    private static final String TAG = "TripHistoryActivity";

    FirebaseDatabase database;
    DatabaseReference user_trips;
    private Trip mTrip;
    String address, dateString, timeString, tripId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        setTitle("MIS VIAJES");

        final TextView addressTextView = findViewById(R.id.address_text_view);
        final TextView dateTextView = findViewById(R.id.date_text_view);
        final TextView timeTextView = findViewById(R.id.time_text_view);

        LinearLayout activeTripLinearLayout = findViewById(R.id.active_trip_linear_layout);
        activeTripLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass data to next activity
                Intent intent = new Intent(TripHistoryActivity.this, DetailActiveTripActivity.class);
                intent.putExtra("address", address);
                intent.putExtra("dateString", dateString);
                intent.putExtra("timeString", timeString);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();
        user_trips = database.getReference("user_trips");

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

//        final ListView tripListView = (ListView) findViewById(R.id.trip_list_view);
//        final ArrayList<Trip> tripList = new ArrayList<>();
//
//        //PARA OBTENER LOS PAST SE NECESITA HACER QUERY A TODOS!! (value event listener con un ciclo for dentro)
//        //query firebase trip data
//        Query query1 = user_trips.child(userID)
//                .orderByChild("address")
//                .equalTo("calle San Martin, Miraflores, Perú");
//        query1.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //Recorrer todos los nodos y filtrar los que tengan un campo buscado.
//                for(DataSnapshot dSnapshot : dataSnapshot.getChildren()) {
//                    //Obtain all data of this trip.
//                    String address = dSnapshot.child("address").getValue(String.class);
////                    Double latitude = dSnapshot.child("latLng").getValue(LatLng.class).latitude;
////                    Double longitude = dSnapshot.child("latLng").getValue(LatLng.class).longitude;
//                    String latLngString = dSnapshot.child("latLng").getValue(String.class);

                    //En el siguiente activity obtendremos el latLng así:
//                    String[] latLngSplitted = latLngString.split(",");
//                    LatLng latLng = new LatLng(Double.parseDouble(latLngSplitted[0]),Double.parseDouble(latLngSplitted[1]));


//                    Date date = dSnapshot.child("date").getValue(Date.class);
//                    String dateString = dSnapshot.child("dateString").getValue(String.class);
//                    String timeString = dSnapshot.child("timeString").getValue(String.class);
//                    Integer seats = dSnapshot.child("seats").getValue(Integer.class);
//                    Integer cost = dSnapshot.child("cost").getValue(Integer.class);
//                    Boolean isActive = dSnapshot.child("active").getValue(Boolean.class);
//                    String tripId = dSnapshot.getKey();
//                    Log.d(TAG, "onDataChange: TRIPD ID: " + tripId);
//                    Log.d(TAG, "onDataChange: LATITUDE, LONGITUDE: " + latLngString.split(","));
//                    //userID: userID ya fue buscado anteriormente.
//
                        //Setear al List SOLO LOS FALSE:
                        //if(isActive = false){
        //                    tripList.add(new Trip(address, latLngString, date, dateString,
        //                            timeString, seats, cost, isActive, tripId, userID));
        //                    final TripAdapter tripAdapter = new TripAdapter(TripHistoryActivity.this, R.layout.trip_list_item, tripList);
        //                    tripListView.setAdapter(tripAdapter);
                        //}
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });



    }
}
