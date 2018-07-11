package com.nicolasrf.carpoolurp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nicolasrf.carpoolurp.adapters.TripAdapter;
import com.nicolasrf.carpoolurp.model.Request;
import com.nicolasrf.carpoolurp.model.Trip;

import java.util.ArrayList;
import java.util.Date;

public class RiderDashboardTestActivity extends AppCompatActivity {
    private static final String TAG = "RiderDashboardTestActivity";

    public final static String SELECTED_TRIP = "selected_trip"; //llave

    FirebaseDatabase database;
    DatabaseReference trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_test);

        database = FirebaseDatabase.getInstance();
        trips = database.getReference("trips");

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final ListView tripListView = (ListView) findViewById(R.id.trip_list_view);
        final ArrayList<Trip> tripList = new ArrayList<>();

        //remember: only for riders.

        //query node trips, where active = true and userId != getUserId.

        //PARA OBTENER LOS PAST SE NECESITA HACER QUERY A TODOS!! (value event listener con un ciclo for dentro)
        //query firebase trip data
        Query query = trips
                .orderByChild("active")
                .equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Recorrer todos los nodos y filtrar los que tengan un campo buscado.
                for(DataSnapshot dSnapshot : dataSnapshot.getChildren()) {
                    //Obtain all data of this trip.
                    String address = dSnapshot.child("address").getValue(String.class);
                    String latLngString = dSnapshot.child("latLng").getValue(String.class);

                    Date date = dSnapshot.child("date").getValue(Date.class);
                    String dateString = dSnapshot.child("dateString").getValue(String.class);
                    String timeString = dSnapshot.child("timeString").getValue(String.class);
                    Integer seats = dSnapshot.child("seats").getValue(Integer.class);
                    Integer cost = dSnapshot.child("cost").getValue(Integer.class);
                    Boolean isActive = dSnapshot.child("active").getValue(Boolean.class);
                    String tripId = dSnapshot.getKey();
                    String driver_id = dSnapshot.child("user_id").getValue(String.class);
                    Date dateCreated = dSnapshot.child("date_created").getValue(Date.class); //Get DateGregorian (date created)
                    //FALTARIAN DESCARGAR LOS REQUESTS?? Y SI NO LOS HAY???
                    Log.d("Rider dash", "onDataChange: TRIPD ID: " + tripId);
                    Log.d("Rider dash", "onDataChange: LATITUDE, LONGITUDE: " + latLngString.split(","));
                    //userID: userID ya fue buscado anteriormente.

                    //Setear al List SOLO LOS distintos al user actual:
                    if(!driver_id.equals(userID)){

                        tripList.add(new Trip(address, latLngString, date, dateString,
                                timeString, seats, cost, isActive, tripId, driver_id, dateCreated));
                        final TripAdapter tripAdapter = new TripAdapter(RiderDashboardTestActivity.this, R.layout.trip_list_item, tripList);
                        tripListView.setAdapter(tripAdapter);
                        tripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Trip selectedTrip = tripAdapter.getItem(position);

                                Intent intent = new Intent(RiderDashboardTestActivity.this, DetailTripActivity.class);
                                intent.putExtra(SELECTED_TRIP, selectedTrip);//no olvidar q se implementa parcelabler en la clase Trip!!
                                startActivity(intent);
                            }
                        });
                    } //sino, no hace nada.

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
