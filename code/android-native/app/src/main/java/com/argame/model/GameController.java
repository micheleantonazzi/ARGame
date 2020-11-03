package com.argame.model;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.argame.R;
import com.argame.model.data_structures.tic_tac_toe_game.ITicTacToeGame;
import com.argame.model.data_structures.tic_tac_toe_game.TicTacToeGame;
import com.argame.model.data_structures.user_data.IUser;
import com.argame.model.data_structures.users_current_game.UserCurrentGame;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GameController {

    static private GameController INSTANCE;

    private Context context = null;
    private LayoutInflater layoutInflater = null;
    private boolean hasBeenInitialized = false;
    private TicTacToeGame currentTicTacToeGame = null;

    private GameController() {}

    private GameController(Context context, LayoutInflater layoutInflater) {
        this.context = context;
        this.layoutInflater = layoutInflater;
    }

    synchronized static public GameController getInstance(Context applicationContext, LayoutInflater layoutInflater){
        if (INSTANCE == null){
            INSTANCE = new GameController(applicationContext, layoutInflater);
        }

        return INSTANCE;
    }

    synchronized static public GameController getInstance(){
        if (INSTANCE == null){
            INSTANCE = new GameController();
        }

        return INSTANCE;
    }

    synchronized public void initialize() {
        if(this.hasBeenInitialized)
            return;

        // Initialize only if user is logged
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null) {
            return;
        }

        // Set up firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Add listener to user current game
        firestore.collection(Database.COLLECTION_USERS_CURRENT_GAME).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                            case Database.COLLECTION_TIC_TAC_TOE_GAMES:

                                this.currentTicTacToeGame = new TicTacToeGame().setMatchID(snapshotCurrentGame.getData().get(UserCurrentGame.GAME_ID_FIELD).toString());
                                firestore.collection(snapshotCurrentGame.getData().get(UserCurrentGame.TYPE_FIELD).toString())
                                        .document(snapshotCurrentGame.getData().get(UserCurrentGame.GAME_ID_FIELD).toString())
                                        .addSnapshotListener((snapshotGame, exceptionGame) -> {
                                            if (exceptionGame != null) {
                                                Log.w("debugg", "Retrieve game data failed", exceptionGame);
                                                return;
                                            }
                                            if (snapshotGame != null && snapshotGame.exists() && snapshotGame.getData() != null) {
                                                Log.d("debugg", "update game data");
                                                if(this.currentTicTacToeGame != null) {
                                                    this.currentTicTacToeGame.updateData(snapshotGame.getData());

                                                    // Obtain owner data
                                                    IUser otherPlayer = Database.getInstance().getUserFriends().getFriend(
                                                            this.currentTicTacToeGame.getOtherPlayerID(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    );

                                                    this.currentTicTacToeGame.setOtherPlayer(otherPlayer);

                                                    // Show dialog to accept the
                                                    if(this.currentTicTacToeGame.getOpponentID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
                                                            this.currentTicTacToeGame.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_NOT_ANSWERED) {
                                                        this.showDialogAcceptGame();
                                                    }
                                                }
                                                Log.d("debugg", "update game data");
                                            }
                                            else {
                                                Log.w("debugg", "Game data: null");
                                            }
                                        });
                                return;
                        }

                    } else {
                        Log.w("debugg", "User current game data: null");
                    }
                });

        this.hasBeenInitialized = true;
    }

    synchronized public void createTicTacToeGame(String opponentID) {

        // Prepare game data
        Map<String, Object> gameData = TicTacToeGame.getInitialFieldMap();
        gameData.put(TicTacToeGame.OWNER_ID_FIELD, FirebaseAuth.getInstance().getCurrentUser().getUid());
        gameData.put(TicTacToeGame.OPPONENT_ID_FIELD, opponentID);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Create new match
        firestore.collection(Database.COLLECTION_TIC_TAC_TOE_GAMES)
                .add(gameData).addOnSuccessListener(documentReference -> {
                    /*
                    Cloud function not working with the spark plan, use a test channel for agora

                    // Get agora token for video call
                    Map<String, Object> data = new HashMap<>();
                    data.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid().hashCode());
                    data.put("channel_name", documentReference.getId());
                    FirebaseFunctions.getInstance()
                            .getHttpsCallable("createAgoraToken")
                            .call(data)
                            .continueWith(task -> {
                                // This continuation runs on either success or failure, but if the task
                                // has failed then getResult() will throw an Exception which will be
                                // propagated down.

                                if(!(task.getResult().getData() instanceof HashMap)) {
                                    HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                                    Log.d("debugg", "token: " + result.getClass().getName());
                                    return result;
                                }
                                else
                                    return new HashMap<String, Object>();

                            })
                            .addOnSuccessListener(resultMap -> {
                                if(resultMap.keySet().size() == 0)
                                    Log.w("debugg", "Agora token creation failed");
                                else {
                                    Log.d("debugg", "agora token " + resultMap.get("token"));
                                }
                            });
                     */

            // Update agora channel name and token
            documentReference.update(TicTacToeGame.AGORA_CHANNEL_FIELD, "test");
            documentReference.update(TicTacToeGame.AGORA_TOKEN_FIELD, "00629740b29ac4d480e9ff663b48521191bIAC4/+nqf30nZzEUZDQtAKR3j27wx3VCG67bl+SJ9quvzgx+f9gAAAAAEADJ+bHOvmChXwEAAQC+YKFf");

            // Add match to users
            Map<String, Object> currentGame = new HashMap<>(3);
            currentGame.put(UserCurrentGame.IS_ACTIVE_FIELD, true);
            currentGame.put(UserCurrentGame.TYPE_FIELD, Database.COLLECTION_TIC_TAC_TOE_GAMES);
            currentGame.put(UserCurrentGame.GAME_ID_FIELD, documentReference.getId());
            firestore.collection(Database.COLLECTION_USERS_CURRENT_GAME).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(currentGame)
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                        Log.w("debugg", "Current game owner update failed", e);
                    });

            firestore.collection(Database.COLLECTION_USERS_CURRENT_GAME).document(opponentID)
                    .set(currentGame)
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                        Log.w("debugg", "Current game opponent update failed", e);
                    });
                })
                .addOnFailureListener(e -> Log.w("debugg", "TicTacToeGame creation failure", e));
    }

    synchronized public ITicTacToeGame getCurrentTicTacToeGame() {
        return this.currentTicTacToeGame;
    }

    private void showDialogAcceptGame() {

        // Obtain owner data

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
        View view = this.layoutInflater.inflate(R.layout.alert_dialog_accept_game_layout, null);

        TextView textViewMessage = view.findViewById(R.id.text_view_message);
        textViewMessage.setText(this.currentTicTacToeGame.getOtherPlayer().getNickname() + " " + this.context.getResources().getString(R.string.accept_game_message) + " tris");

        alertDialogBuilder.setView(view)
                .setPositiveButton(R.string.button_accept_game, (dialog, which) -> {
                    FirebaseFirestore.getInstance().collection(Database.COLLECTION_TIC_TAC_TOE_GAMES)
                            .document(this.currentTicTacToeGame.getMatchID()).update(TicTacToeGame.ACCEPTED_FIELD, TicTacToeGame.ACCEPT_STATUS_ACCEPTED);
                })
                .setNegativeButton(R.string.button_refuse_game, (dialog, which) -> {
                    FirebaseFirestore.getInstance().collection(Database.COLLECTION_TIC_TAC_TOE_GAMES)
                            .document(this.currentTicTacToeGame.getMatchID()).update(TicTacToeGame.ACCEPTED_FIELD, TicTacToeGame.ACCEPT_STATUS_REFUSED);
                })
        .create().show();
    }
}
