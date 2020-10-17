package com.argame.utilities;

import android.util.Log;

import com.argame.utilities.data_structures.user_data.User;
import com.argame.utilities.data_structures.user_data.UserInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class Database {

    static private Database INSTANCE;

    private boolean hasBeenInitialized = false;

    // Firestore locations
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

    synchronized public void retrieveUserData() {

        // Control if database has been already initialized
        if(this.hasBeenInitialized)
            return;

        // Retrieve data only if user is logged
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null) {
            return;
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Configure persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);

        // Get user profile data
        firestore.collection(COLLECTION_USER_DATA).document(firebaseUser.getUid()).addSnapshotListener((snapshot, exception) -> {
            if (exception != null) {
                Log.w("debugg", "Read data user failed", exception);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("debugg", "chiamo snapshot");
                this.userData.updateData(snapshot.getData());
            } else {
                Log.d("debugg", "Current data: null");
            }
        });

        this.hasBeenInitialized = true;
    }

    public void updateUserData(String name, String surname, String nickname) {
        Map<String, Object> fields = new HashMap<>(3);
        fields.put(User.NAME_FIELD, name);
        fields.put(User.SURNAME_FIELD, surname);
        fields.put(User.NICKNAME_FIELD, nickname);

        FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(fields)
                .addOnCompleteListener(task -> {
                })
                .addOnFailureListener(exception -> {
                    Log.w("debugg", "Update user data filed", exception);
                });
    }

    public UserInterface getUserData(){
        return this.userData;
    }


}
