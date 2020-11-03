package com.argame.model.remote_structures;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import com.argame.model.Database;
import com.argame.model.TicTacToeGameController;
import com.argame.model.data_structures.tic_tac_toe_game.TicTacToeGame;
import com.argame.model.data_structures.user_data.IUser;
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

    private boolean isInitialized = false;
    private Context context;
    private LayoutInflater layoutInflater;

    private UserCurrentGame() {}

    synchronized public static UserCurrentGame getInstance() {
        if(INSTANCE == null)
            INSTANCE = new UserCurrentGame();
        return INSTANCE;
    }

    synchronized public void initialize(Context context, LayoutInflater layoutInflater) {
        if(FirebaseAuth.getInstance().getCurrentUser() == null || this.isInitialized)
            return;

        this.context = context;
        this.layoutInflater = layoutInflater;

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

                        // Check if data comes from cache: in this case return
                        if(snapshotCurrentGame.getMetadata().isFromCache())
                            return;

                        Boolean isActive = (Boolean) snapshotCurrentGame.getData().get(UserCurrentGame.IS_ACTIVE_FIELD);

                        // if game isn't active returns (there is no game in action)
                        if(!isActive) {
                            return;
                        }

                        // Add listener to current game (based of correct type)
                        switch (snapshotCurrentGame.getData().get(UserCurrentGame.TYPE_FIELD).toString()) {
                            case TicTacToeGameController.COLLECTION_TIC_TAC_TOE_GAMES:
                                TicTacToeGameController.getInstance().checkNewTicTacToeGame(snapshotCurrentGame.get(GAME_ID_FIELD).toString(), this.context, this.layoutInflater);
                                return;
                        }
                    } else {
                        Log.w("debugg", "User current game data: null");
                    }
                });
        this.isInitialized = true;
    }
}
