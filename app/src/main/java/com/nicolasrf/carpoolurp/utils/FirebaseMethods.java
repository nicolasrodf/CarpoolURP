package com.nicolasrf.carpoolurp.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nicolasrf.carpoolurp.DetailTripActivity;
import com.nicolasrf.carpoolurp.model.Request;

/**
 * Created by Nicolas on 22/06/2018.
 */

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference driver_trips, trips;

    private String userID;

    private Context mContext;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        driver_trips = database.getReference("driver_trips");
        trips = database.getReference("trips");
        //mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void deleteTrip(String tripId, String userID){
        //delete in trip node
        if(tripId!=null) {
            trips.child(tripId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(mContext, "Trip deleted.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Trip deleted failed.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onFailure: TRIP DELETED FAILED: " + e.getMessage());
                        }
                    });
        }
        //remove in driver_trips node
        if(tripId != null && userID != null) {
            driver_trips.child(userID)
                    .child(tripId)
                    .removeValue()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mContext, "Trip deleted failed.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onFailure: TRIP DELETED FAILED: " + e.getMessage());
                        }
                    });
        }
    }

    public void setFalseActiveTripFieldValue(String tripId, String userID){

        if(tripId != null){
            //set active value false in trips node
            trips.child(tripId)
                    .child("active")
                    .setValue(false);
        }

        if(tripId != null && userID != null){
            //set active value false in driver_trips node
            driver_trips.child(userID)
                    .child(tripId)
                    .child("active")
                    .setValue(false);
        }
    }

    public void sendRequest(String tripId, String requestKey, Request request, String driverId){

        if(tripId!=null && requestKey!=null && request!=null) {
            trips.child(tripId)
                    .child("requests")
                    .child(requestKey)
                    .setValue(request)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(mContext, "Request sent.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        //
        if(tripId!=null && requestKey!=null && request!=null && driverId!=null) {
            driver_trips.child(driverId)
                    .child(tripId)
                    .child("requests")
                    .child(requestKey)
                    .setValue(request);
        }
    }

}
