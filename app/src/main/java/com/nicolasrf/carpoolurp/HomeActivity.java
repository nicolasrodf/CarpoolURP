package com.nicolasrf.carpoolurp;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nicolasrf.carpoolurp.Common.Common;
import com.nicolasrf.carpoolurp.model.User;

import java.util.Date;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "HomeActivity";

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference users, user_trips;

    String userMode;
    String address, dateString, timeString, tripId;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");
        user_trips = database.getReference("user_trips");

        //Get user mode
        getUserMode();

        Button createTripButton = findViewById(R.id.create_trip_button);
        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CreateTripActivity.class));
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

        //VIEW ACTIVE TRIP.-

        final TextView addressTextView = findViewById(R.id.address_text_view);
        final TextView dateTextView = findViewById(R.id.date_text_view);
        final TextView timeTextView = findViewById(R.id.time_text_view);

        LinearLayout activeTripLinearLayout = findViewById(R.id.active_trip_linear_layout);
        activeTripLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass data to next activity
                Intent intent = new Intent(HomeActivity.this, DetailActiveTripActivity.class);
                intent.putExtra("address", address);
                intent.putExtra("dateString", dateString);
                intent.putExtra("timeString", timeString);
                intent.putExtra("tripId", tripId);
                startActivity(intent);
            }
        });

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //query firebase trip data
        //Todo. Pendiente Crear getActiveTrip() para el OnResume tambien.-
        Query query = user_trips.child(userID)
                .orderByChild("active")
                .equalTo(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Recorrer los nodos aunque tendremos solo un viaje activo siempre.
                for(DataSnapshot dSnapshot : dataSnapshot.getChildren()) {
                    address = dSnapshot.child("address").getValue(String.class);
//                    Double latitude = dSnapshot.child("latLng").getValue(LatLng.class).latitude;
//                    Double longitude = dSnapshot.child("latLng").getValue(LatLng.class).longitude;
                    String latLngString = dSnapshot.child("latLng").getValue(String.class);

                    //En el siguiente activity obtendremos el latLng así:
                    //String[] latLngSplitted = latLngString.split(",");
                    //LatLng latLng = new LatLng(Double.parseDouble(latLngSplitted[0]),Double.parseDouble(latLngSplitted[1]));


                    Date date = dSnapshot.child("date").getValue(Date.class);
                    dateString = dSnapshot.child("dateString").getValue(String.class);
                    timeString = dSnapshot.child("timeString").getValue(String.class);
                    Integer seats = dSnapshot.child("seats").getValue(Integer.class);
                    Integer cost = dSnapshot.child("cost").getValue(Integer.class);
                    Boolean isActive = dSnapshot.child("active").getValue(Boolean.class);
                    tripId = dSnapshot.getKey();
                    //userID: userID ya fue buscado anteriormente.

                    //set text views
                    addressTextView.setText(address);
                    dateTextView.setText(dateString);
                    timeTextView.setText(timeString);


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

    private void getUserMode() {
        users.child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        userMode = user.getUserMode();
                        Common.currentUser.setUserMode(userMode); //Asigno al objet User
                        Log.d(TAG, "onDataChange: USER MODE: " +userMode);

                        //Hide or show Request Navigation Item
                        hideOrShowRequestItem();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
            startActivity(new Intent(HomeActivity.this, TripHistoryActivity.class));

        } else if (id == R.id.nav_requests){
            startActivity(new Intent(HomeActivity.this, RequestsActivity.class));

        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));

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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //Todo. Problema al volver desde Profile no actualiza el user mode.-
    @Override
    protected void onPostResume() {
        super.onPostResume();
        getUserMode();

        Log.d(TAG, "onPostResume: USER MODE " +userMode);
    }
}
