package com.argame.model.remote_structures;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import com.argame.model.TicTacToeGameController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserCurrentGame {

    private static UserCurrentGame INSTANCE;

    // Collection name
    public static final String COLLECTION_USERS_CURRENT_GAME = "users_current_game";

    // Fields' name
    public static final String IS_ACTIVE_FIELD = "isActive";
    public static final String TYPE_FIELD = "type";
    public static final String GAME_ID_FIELD = "gameID";
    public static final String OWNER_ID_FIELD = "ownerID";

    private boolean isInitialized = false;
    private boolean isFirstRead = true;
    private Context context;
    private LayoutInflater layoutInflater;

    private UserCurrentGame() {}

    synchronized public static UserCurrentGame getInstance() {
        if(INSTANCE == null)
            INSTANCE = new UserCurrentGame();
        return INSTANCE;
    }

    synchronized public UserCurrentGame initialize() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null || this.isInitialized)
            return this;

        // Set up firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Add listener to user current game
        firestore.collection(COLLECTION_USERS_CURRENT_GAME).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener((snapshotCurrentGame, exceptionCurrentGame) -> {
                    if (exceptionCurrentGame != null) {
                        Log.w("debugg", "Retrieve current game data failed", exceptionCurrentGame);
                        return;
                    }
                    if (snapshotCurrentGame != null && snapshotCurrentGame.exists() && snapshotCurrentGame.getData() != null) {

                        Boolean isActive = (Boolean) snapshotCurrentGame.getData().get(UserCurrentGame.IS_ACTIVE_FIELD);
                        String ownerID = String.valueOf(snapshotCurrentGame.getData().get(UserCurrentGame.OWNER_ID_FIELD));

                        // If game isn't active or if the owner is this user returns (there is no game in action or the current user has created the match and the notification is useless)
                        if(!isActive) {
                            return;
                        }

                        // Add listener to current game (based of correct type)
                        switch (snapshotCurrentGame.getData().get(UserCurrentGame.TYPE_FIELD).toString()) {
                            case TicTacToeGameController.COLLECTION_TIC_TAC_TOE_GAMES:
                                // If this user is the opponent, ask for game
                                if (!ownerID.equals(CurrentUser.getInstance().getCurrentUser().getUid()))
                                    TicTacToeGameController.getInstance().askNewTicTacToeGame(snapshotCurrentGame.get(GAME_ID_FIELD).toString(), this.context, this.layoutInflater);
                                else
                                    TicTacToeGameController.getInstance().checkGameResume(snapshotCurrentGame.get(GAME_ID_FIELD).toString());
                                return;
                        }
                    } else {
                        Log.w("debugg", "User current game data: null");
                    }
                });
        this.isInitialized = true;

        return this;
    }

    // This method is called by the opponent and set inactive the current game
    synchronized public void matchRefused(String ownerID) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection(COLLECTION_USERS_CURRENT_GAME).document(CurrentUser.getInstance().getCurrentUser().getUid())
                .update(IS_ACTIVE_FIELD, false);

        firestore.collection(COLLECTION_USERS_CURRENT_GAME).document(ownerID)
                .update(IS_ACTIVE_FIELD, false);
    }

    synchronized public void setContextAndInflater(Context context, LayoutInflater layoutInflater) {
        this.context = context;
        this.layoutInflater = layoutInflater;
    }
}
