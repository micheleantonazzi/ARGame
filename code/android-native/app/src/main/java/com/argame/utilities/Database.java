package com.argame.utilities;

import android.util.Log;

import com.argame.utilities.data_structures.User;
import com.argame.utilities.data_structures.UserInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class Database {

    static private Database INSTANCE;

    // Reference
    private static String COLLECTION_USER_DATA = "users_data";

    // Application data
    User userData = new User();

    private Database(){}

    synchronized static public Database getInstance(){
        if (INSTANCE == null){
            INSTANCE = new Database();
        }

        return INSTANCE;
    }

    public void retrieveUserData() {

        // Retrieve data only if user is logged
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null) {
            return;
        }
        Log.d("debugg", firebaseUser.getUid());

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Configure persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        // Get user profile data
        final DocumentReference userDataRef = firestore.collection(COLLECTION_USER_DATA).document(firebaseUser.getUid());
        userDataRef.addSnapshotListener((snapshot, exception) -> {
            if (exception != null) {
                Log.d("debugg", "Read data user failed", exception);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                synchronized (this){
                    this.userData = snapshot.toObject(User.class);
                }
            } else {
                Log.d("debugg", "Current data: null");
            }
        });
    }

    synchronized public UserInterface getUserData(){
        return this.userData;
    }
}
