package com.argame.model;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class Database {

    static private Database INSTANCE;

    private boolean hasBeenInitialized = false;

    // Realtime database locations
    private static final String USERS_CONNECTION_STATUS = "users_connection_status";

    private Database(){}

    synchronized static public Database getInstance(){
        if (INSTANCE == null){
            INSTANCE = new Database();
        }

        return INSTANCE;
    }

    synchronized public void initialize() {

        // Control if database has been already initialized
        if(this.hasBeenInitialized)
            return;

        // Retrieve data only if user is logged
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null) {
            return;
        }

        // Set up real time database
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference onlineStatusReference = FirebaseDatabase.getInstance().getReference(USERS_CONNECTION_STATUS).child(firebaseUser.getUid());
        onlineStatusReference.setValue(1);
        onlineStatusReference.onDisconnect().setValue(0);

        // Detect when user connection status changed
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    onlineStatusReference.setValue(1);
                }
                else
                    onlineStatusReference.setValue(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Set up firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Configure persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        this.hasBeenInitialized = true;
    }
}
