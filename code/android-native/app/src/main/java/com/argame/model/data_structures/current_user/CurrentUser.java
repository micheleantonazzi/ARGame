package com.argame.model.data_structures.current_user;

import android.util.Log;

import com.argame.model.data_structures.user_data.IUser;
import com.argame.model.data_structures.user_data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CurrentUser {

    // Collection name
    public static final String COLLECTION_USER_DATA = "users_data";

    private static CurrentUser INSTANCE;

    private boolean isInitialized = false;
    private User currentUser = new User();

    private CurrentUser() {}

    synchronized public static CurrentUser getInstance() {
        if(INSTANCE == null)
            INSTANCE = new CurrentUser();
        return INSTANCE;
    }

    synchronized public void initialize() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null || this.isInitialized)
            return;

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(COLLECTION_USER_DATA).document(userID).addSnapshotListener((snapshotUserData, exceptionUserData) -> {
            if (exceptionUserData != null) {
                Log.w("debugg", "Read data user failed", exceptionUserData);
                return;
            }

            if (snapshotUserData != null && snapshotUserData.exists() && snapshotUserData.getData() != null) {
                this.currentUser.updateData(snapshotUserData.getData()).notifyListeners();
            } else {
                Log.d("debugg", "Current data: null");
            }
        });

        this.isInitialized = true;
    }

    synchronized public void updateUserData(String name, String surname, String nickname) {
        Map<String, Object> fields = new HashMap<>(3);
        fields.put(User.NAME_FIELD, name);
        fields.put(User.SURNAME_FIELD, surname);
        fields.put(User.NICKNAME_FIELD, nickname);

        FirebaseFirestore.getInstance().collection(COLLECTION_USER_DATA).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(fields)
                .addOnCompleteListener(task -> {
                })
                .addOnFailureListener(exception -> {
                    Log.w("debugg", "Update user data failed", exception);
                });
    }

    synchronized public IUser getCurrentUser() {
        return this.currentUser;
    }

    synchronized public boolean isInitialized() {
        return this.isInitialized;
    }
}
