package com.nicolasrf.carpoolurp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nicolasrf.carpoolurp.Common.Common;
import com.nicolasrf.carpoolurp.model.Request;
import com.nicolasrf.carpoolurp.model.Trip;
import com.nicolasrf.carpoolurp.model.User;
import com.nicolasrf.carpoolurp.utils.FirebaseMethods;
import com.nicolasrf.carpoolurp.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomeActivity";

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference users, driver_trips, trips;

    NavigationView navigationView;

    //Widgets
    TextView addressTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView noTripMessageTextView;
    LinearLayout activeTripLinearLayout;
    Button createTripButton;

    Intent profileIntent;

    Trip trip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
        driver_trips = database.getReference("driver_trips");
        trips = database.getReference("trips");

        addressTextView = findViewById(R.id.address_text_view);
        dateTextView = findViewById(R.id.date_text_view);
        timeTextView = findViewById(R.id.time_text_view);
        noTripMessageTextView = findViewById(R.id.no_active_trip_text_view);
        activeTripLinearLayout = findViewById(R.id.active_trip_linear_layout);
        createTripButton = findViewById(R.id.create_trip_button);

        //Get user mode
        getUserModeAndActiveTrip();

        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CreateTripActivity.class));
            }
        });

        activeTripLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass data to next activity
                Intent intent = new Intent(HomeActivity.this, DetailActiveTripActivity.class);
//                intent.putExtra("address", address);
//                intent.putExtra("dateString", dateString);
//                intent.putExtra("timeString", timeString);
//                intent.putExtra("tripId", tripId);
                Bundle bundle = new Bundle();
                //bundle.putParcelableArrayList("request", (ArrayList<? extends Parcelable>)  requests);
                bundle.putParcelable("trip", trip);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //Navi Drawer init
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void getUserModeAndActiveTrip() {

        final String user_id = mAuth.getCurrentUser().getUid();
        //Get user mode
        users.child(user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);
                        String userMode = user.getUserMode();
                        Common.currentUser.setUserMode(userMode); //Asigno al objet User
                        Log.d(TAG, "onDataChange: USER MODE: " +userMode);
                        //Hide or show Request Navigation Item
                        hideOrShowRequestItem();

                        //Check active trips if user is driver.
                        if(userMode.equals("driver")) {

                            final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            //query firebase trip data
                            Query query = driver_trips.child(userID)
                                    .orderByChild("active")
                                    .equalTo(true);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {

                                        //Recorrer los nodos aunque tendremos solo un viaje activo siempre.
                                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                                            String address = singleSnapshot.child("address").getValue(String.class);
//                    Double latitude = singleSnapshot.child("latLng").getValue(LatLng.class).latitude;
//                    Double longitude = singleSnapshot.child("latLng").getValue(LatLng.class).longitude;
                                            String latLngString = singleSnapshot.child("latLng").getValue(String.class);

                                            //En el siguiente activity obtendremos el latLng así:
                                            //String[] latLngSplitted = latLngString.split(",");
                                            //LatLng latLng = new LatLng(Double.parseDouble(latLngSplitted[0]),Double.parseDouble(latLngSplitted[1]));


                                            Date date = singleSnapshot.child("date").getValue(Date.class);
                                            String dateString = singleSnapshot.child("dateString").getValue(String.class);
                                            String timeString = singleSnapshot.child("timeString").getValue(String.class);
                                            Integer seats = singleSnapshot.child("seats").getValue(Integer.class);
                                            Integer cost = singleSnapshot.child("cost").getValue(Integer.class);
                                            Boolean isActive = singleSnapshot.child("active").getValue(Boolean.class);
                                            Date dateCreated = singleSnapshot.child("date_created").getValue(Date.class); //Get DateGregorian (date created)
                                            String tripId = singleSnapshot.getKey();
                                            //userID: userID ya fue buscado anteriormente.

                                            //set text views
                                            addressTextView.setText(address);
                                            dateTextView.setText(dateString);
                                            timeTextView.setText(timeString);

                                            ArrayList<Request> requests = new ArrayList<>();
                                            for (DataSnapshot dSnapshot : singleSnapshot.child("requests").getChildren()){
                                                Request request = new Request();
                                                request.setUser_id(dSnapshot.getValue(Request.class).getUser_id()); //solo me sirve el userId-
                                                requests.add(request);
                                            }

                                            trip = new Trip(address, latLngString, date, dateString,
                                                    timeString, seats, cost, isActive, tripId, userID, dateCreated, requests);

                                            Log.d(TAG, "onDataChange: REQUESTS TRIP SIZE " + requests.size());

                                        }

                                    } else {

                                        activeTripLinearLayout.setVisibility(View.GONE);
                                        noTripMessageTextView.setVisibility(View.VISIBLE);
                                        noTripMessageTextView.setText("No tiene viaje creado." + "\n"
                                                + "Para crear un nuevo viaje, presione el botón 'Crear Viaje'");
                                        createTripButton.setVisibility(View.VISIBLE);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {

                            //Si es rider entonces
                            //Mostrar viaje solicitado.
                            //1. Check if have trip requested.

                                //If yes, show trip requested.


                                //If no, show all trips
                                //Mostrar los viajes disponibles en su Red.???
                                //addressTextView.setText(" MOSTRAR VIAJE SOLICITADO: ");

                            //2.

                            Query query = trips
                                    .orderByChild("active")
                                    .equalTo(true);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                                                if(singleSnapshot.child("requests").exists()) {

                                                    for (DataSnapshot dSnapshot : singleSnapshot.child("requests").getChildren()) {
                                                        if (dSnapshot.getValue(Request.class).getUser_id().equals(user_id)) {
                                                            //Setear widgets
                                                           //String address = singleSnapshot.getValue(Trip.class).getAddress();
                                                            String address = singleSnapshot.child("address").getValue(String.class);
                                                            addressTextView.setText("TIENE UN VIAJE SOLICITADO: ");
                                                            dateTextView.setText("ADDRESS: " + address);
                                                            //Toast.makeText(HomeActivity.this, "Viaje solicitado: "+address, Toast.LENGTH_SHORT).show();

                                                        } else {
                                                            addressTextView.setText("NO TIENE UN VIAJE SOLICITADO.");
                                                        }

                                                    }
                                                } else {
                                                    Log.d(TAG, "onDataChange: NO HAY REQUESTS");
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        //        final ListView tripListView = (ListView) findViewById(R.id.trip_list_view);
//        final ArrayList<Trip> tripList = new ArrayList<>();
//
//        //PARA OBTENER LOS PAST SE NECESITA HACER QUERY A TODOS!! (value event listener con un ciclo for dentro)
//        //query firebase trip data
//        Query query1 = driver_trips.child(userID)
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

    private void hideOrShowRequestItem() {

        Menu nav_Menu = navigationView.getMenu();

        //Uso el valor asignado al objeto User al hacer la consulta en getUserMode()
        if(Common.currentUser.getUserMode().equals("rider")){
            //Hide Requests item
            nav_Menu.findItem(R.id.nav_requests).setVisible(false);

        } else if(Common.currentUser.getUserMode().equals("driver")){
            //Show Requests item
            nav_Menu.findItem(R.id.nav_requests).setVisible(true);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
                super.onBackPressed(); //salir de la app
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_trips) {
            if(Utils.isNetworkAvailable(getApplicationContext()))
                startActivity(new Intent(HomeActivity.this, DriverTripsActivity.class));

        } else if (id == R.id.nav_requests){
            if(Utils.isNetworkAvailable(getApplicationContext()))
                startActivity(new Intent(HomeActivity.this, RequestsActivity.class));

        } else if (id == R.id.nav_profile) {
            if (Utils.isNetworkAvailable(getApplicationContext()))
                //primero cerrar el activity
//                profileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
//                profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

        }else if (id == R.id.nav_trips_test) {
                if(Utils.isNetworkAvailable(getApplicationContext()))
                    startActivity(new Intent(HomeActivity.this, RiderDashboardTestActivity.class));

        } else if (id == R.id.nav_sign_out) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Cerrar todos los activities.
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserModeAndActiveTrip();
        //Log.d(TAG, "onPostResume: USER MODE " +userMode);
    }
}
