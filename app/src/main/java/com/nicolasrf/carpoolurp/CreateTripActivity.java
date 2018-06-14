package com.nicolasrf.carpoolurp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.nicolasrf.carpoolurp.Common.Common;
import com.nicolasrf.carpoolurp.adapters.NothingSelectedSpinnerAdapter;
import com.nicolasrf.carpoolurp.fragments.DatePickerFragment;
import com.nicolasrf.carpoolurp.fragments.TimePickerFragment;
import com.nicolasrf.carpoolurp.remote.IGoogleService;
import com.nicolasrf.carpoolurp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTripActivity extends AppCompatActivity implements TimePickerFragment.TimeDialogListener,
        TimePickerFragment.OnFragmentInteractionListener, DatePickerFragment.DateDialogListener,
        DatePickerFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "CreateTripActivity";

    private static final int LOCATION_REQUEST_CODE = 9998;
    private static final int PLAY_SERVICES_REQUEST = 9997;

    TextView dateTextView;
    TextView timeTextView;
    int numericNumberOfSeats;
    int numericTravelCost;
    Date dateDataGregorian;
    String updatedTimeData;
    String updatedDateStringFormat;
    String numberOfSeatSelected;
    String travelCostSelected;
    Spinner numberOfSeatsSpinner;
    Spinner travelCostSpinner;
    Button goToDriverMenuButton;
    Boolean activeTrip = false;

    String placeAddress;
    LatLng placeLatLng;

    private static final String DIALOG_TIME = "CreateTripDetailsActivity.TimeDialog";
    private static final String DIALOG_DATE = "CreateTripDetailsActivity.DateDialog";

    //Location
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static final int UPDATE_INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;

    //Declare Google map Api Retrofit
    IGoogleService mGoogleMapService;

    int localDay, localMonth, localYear, localHour, localMinute; //for add in gregorian calendar.-


    //**Metodos de TimePicker y DatePicker fragments.-

    //Esta es la forma de pasar datos desde un fragment a un activity, implementando una interfaz.

    @Override
    public void onTimeData(final int hourData, final int minuteData) { //final para q se puedan acceder desde el query.

        updatedTimeData = Utils.updateTime(hourData, minuteData);
        localHour = hourData;
        localMinute = minuteData;
        timeTextView.setText(updatedTimeData);
    }

    @Override
    public void onDateData(int day, int mon, int year) {
        //prevenir q muestre meses de 0 a 11.
        int monthCorrected = mon + 1;
        updatedDateStringFormat = getString(R.string.date, day, monthCorrected, year);
        dateTextView.setText(updatedDateStringFormat); //Muestra y corrige la fecha a mostrar. CLAVE.
        localDay = day;
        localMonth = mon;
        localYear = year;
        //objeto data gregorian lo creo al presionar el boton para agregar tambien la hora y minuto seleccionado con el timePicker!
    }

    //finaliza dialogo
    @Override
    public void onFinishDialog(java.util.Date date) {
        Toast.makeText(this, "Selected Date :"+ Utils.formatDate(date), Toast.LENGTH_SHORT).show();
    }

    //finaliza dialogo
    @Override
    public void onFinishDialog(String time) {
        Toast.makeText(this, "Selected Time : "+ time, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Runtime permission
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_REQUEST_CODE);
        } else {
            if(checkPlayServices()){ //if have play services on device
                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        //Init google api service
        mGoogleMapService = Common.getGoogleMapsAPI();

        //Show search address fragment
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.enterAddressAutocompleteFragment);
        //Set text size
        autocompleteFragment.setHint("Buscar dirección");
        //Limit Serch to Peru
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("PE")
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                placeAddress = place.getAddress().toString(); //asigno address
                placeLatLng = place.getLatLng(); //asigno latlng
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
                Toast.makeText(CreateTripActivity.this, "An error an ocurred", Toast.LENGTH_SHORT).show();
            }
        });

        //Importante: Activar Geocoding API en google console y usar ApiKey value registrada alli (para activar uso ilimitado)
        final String apiKey = "AIzaSyDP-XXVrZHto423L_iqHT7k6aSzIg0OQZY";

        ImageView requestToAddressImageView = findViewById(R.id.request_to_address_image);
        requestToAddressImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get address from user LatLng.
                mGoogleMapService.getAddressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?key="+apiKey+"&latlng=%s,%s&sensor=false",
                        mLastLocation.getLatitude(),
                        mLastLocation.getLongitude()))
//                    mGoogleMapService.getAddressName("https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyDP-XXVrZHto423L_iqHT7k6aSzIg0OQZY&latlng=-12.143023,-77.0093788&sensor=false")
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                //If fetchAPI ok
                                try {
                                    Log.d(TAG, "onResponse: RESPONSE: " +response.body());
                                    JSONObject jsonObject = new JSONObject(response.body());
                                    JSONArray resultsArray = jsonObject.getJSONArray("results");
                                    JSONObject firstObject = resultsArray.getJSONObject(0);
                                    placeAddress = firstObject.getString("formatted_address"); //asigno address
                                    //Set this address to edit text
                                    ((EditText)autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_input))
                                            .setText(placeAddress);

                                } catch (NullPointerException e) { //atrapar el error y mostrarlo y q no crashee app.
                                    Log.d(TAG, "onResponse: NullPointerException: " + e.getMessage());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(CreateTripActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                placeLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()); //asigno latlng
            }

        });

        dateTextView = findViewById(R.id.date_text_view);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateChoiceOnClick();
            }
        });
        timeTextView = findViewById(R.id.time_text_view);
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeChoiceOnClick();
            }
        });

        //Crea spinner para numero de asientos con un adapter q crea un nombre al iniciar el spinner.
        numberOfSeatsSpinner = (Spinner) findViewById(R.id.number_of_seats_spinner);
        ArrayAdapter<CharSequence> numberOfSeatsAdapter = ArrayAdapter.createFromResource(this,
                R.array.number_of_seats_array, android.R.layout.simple_spinner_item);
        numberOfSeatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfSeatsSpinner.setPrompt("Número de asientos");

        numberOfSeatsSpinner.setAdapter(new NothingSelectedSpinnerAdapter(numberOfSeatsAdapter, R.layout.contact_seats_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this));

        //Crea spinner para tarifa con un adapter q crea un nombre al iniciar el spinner.
        travelCostSpinner = (Spinner) findViewById(R.id.travel_cost_spinner);
        ArrayAdapter<CharSequence> travelCostAdapter = ArrayAdapter.createFromResource(this,
                R.array.travel_cost_array, android.R.layout.simple_spinner_item);
        travelCostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travelCostSpinner.setPrompt("Tarifa (S/.)");

        travelCostSpinner.setAdapter(new NothingSelectedSpinnerAdapter(travelCostAdapter, R.layout.contact_cost_spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                this));

        Button nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                //Estos dos campos se deben calcular dentro del boton. se seleccionan de cada spinner y se convierten a numerico.
                numberOfSeatSelected = numberOfSeatsSpinner.getSelectedItem().toString();
                numericNumberOfSeats = Utils.numberOfSeatsToNumeric(numberOfSeatSelected);
                travelCostSelected = travelCostSpinner.getSelectedItem().toString();
                numericTravelCost = Utils.travelCostToNumeric(travelCostSelected);

                dateDataGregorian = new GregorianCalendar(localYear, localMonth, localDay, localHour, localMinute).getTime();

                Intent passInfoIntent = new Intent(CreateTripActivity.this, CreatePickupPointsActivity.class);
                passInfoIntent.putExtra("placeAddress", placeAddress);
                passInfoIntent.putExtra("placeLatLng", placeLatLng);
                passInfoIntent.putExtra("date", dateDataGregorian);
                passInfoIntent.putExtra("dateString", updatedDateStringFormat);
                passInfoIntent.putExtra("timeString", updatedTimeData);
                passInfoIntent.putExtra("seats", numericNumberOfSeats);
                passInfoIntent.putExtra("cost", numericTravelCost);
                startActivity(passInfoIntent);

            }
        });

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void displayLocation() {

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            return;
        } else {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {

                Log.d(TAG, "displayLocation: " + mLastLocation.getLatitude()+","+mLastLocation.getLongitude());

                final double latitude = mLastLocation.getLatitude();
                final double longitude = mLastLocation.getLongitude();


            } else {
                Log.d(TAG, "displayLocation: Cannot get your location");
            }
        }
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationRequest();
//                        displayLocation();
                    }
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Open reloj
    private void timeChoiceOnClick () {
        TimePickerFragment dialog = new TimePickerFragment();
        dialog.show(getSupportFragmentManager(), DIALOG_TIME);
    }

    //Open calendario
    public void dateChoiceOnClick () {
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.show(getSupportFragmentManager(), DIALOG_DATE);
    }

}
