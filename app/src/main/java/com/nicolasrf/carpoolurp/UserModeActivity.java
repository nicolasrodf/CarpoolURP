package com.nicolasrf.carpoolurp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nicolasrf.carpoolurp.Common.Common;
import com.nicolasrf.carpoolurp.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserModeActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference users;

    String userMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_mode);

        setTitle("Modo de Usuario");

        //init firebase database
        database = FirebaseDatabase.getInstance();
        users = database.getReference("users");

        final Switch userModeSwitch = findViewById(R.id.user_mode_switch);
        Button okButton = findViewById(R.id.ok_button);

        //Get user mode.
        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        String userMode = user.getUserMode();
                        if (userMode.equals("rider")) {
                            userModeSwitch.setChecked(false);
                        } else if (userMode.equals("driver")) {
                            userModeSwitch.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(UserModeActivity.this, "Error: "+databaseError, Toast.LENGTH_SHORT).show();
                    }
                });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,Object> updateInfo = new HashMap<>();

                if(!userModeSwitch.isChecked()){
                    userMode = "rider";
                } else {
                    userMode = "driver";
                }

                updateInfo.put("userMode", userMode);
                Common.currentUser.setUserMode(userMode);

                //Update on Firebase database
                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    Toast.makeText(UserModeActivity.this, "User mode changed !", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UserModeActivity.this, "Error. Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
