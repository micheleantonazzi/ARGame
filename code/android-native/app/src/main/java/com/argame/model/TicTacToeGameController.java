package com.argame.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.argame.R;
import com.argame.activities.tic_tac_toe.TicTacToeActivity;
import com.argame.model.data_structures.tic_tac_toe_game.ITicTacToeGame;
import com.argame.model.data_structures.tic_tac_toe_game.TicTacToeGame;
import com.argame.model.remote_structures.Friends;
import com.argame.model.remote_structures.UserCurrentGame;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

public class TicTacToeGameController {

    static private TicTacToeGameController INSTANCE;

    public static final String COLLECTION_TIC_TAC_TOE_GAMES =  "tic_tac_toe_games";

    private Context context;
    private LayoutInflater layoutInflater;
    private final TicTacToeGame currentTicTacToeGame = new TicTacToeGame();
    private ListenerRegistration gameListenerRegistration;
    private EventListener<DocumentSnapshot> gameListener = (snapshotGame, exceptionGame) -> {
        if (exceptionGame != null) {
            Log.w("debugg", "Retrieve game data failed", exceptionGame);
            return;
        }
        if (snapshotGame != null && snapshotGame.exists() && snapshotGame.getData() != null) {
            Log.d("debugg", "update game data");

            if (this.currentTicTacToeGame != null) {
                this.currentTicTacToeGame.updateData(snapshotGame.getData());

                // Different action
                if (this.currentTicTacToeGame.isOwner(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    if (this.currentTicTacToeGame.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_NOT_ANSWERED)
                        this.context.startActivity(new Intent(this.context, TicTacToeActivity.class));
                    else if (this.currentTicTacToeGame.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_REFUSED)
                        this.resetGame();
                }
                else {
                    if (this.currentTicTacToeGame.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_NOT_ANSWERED)
                        this.showDialogAcceptGame();
                    else if (this.currentTicTacToeGame.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_REFUSED)
                        this.resetGame();
                    else if (this.currentTicTacToeGame.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_ACCEPTED)
                        this.context.startActivity(new Intent(this.context, TicTacToeActivity.class));
                }
            }
            Log.d("debugg", "update game data");
        } else {
            Log.w("debugg", "Game data: null");
        }
    };


    private TicTacToeGameController() {}

    synchronized static public TicTacToeGameController getInstance(){
        if (INSTANCE == null){
            INSTANCE = new TicTacToeGameController();
        }

        return INSTANCE;
    }

    synchronized public void checkNewTicTacToeGame(String gameID, Context context, LayoutInflater layoutInflater) {

        this.context = context;
        this.layoutInflater = layoutInflater;

        this.startNewGame(gameID);
    }

    synchronized public void createTicTacToeGame(String opponentID) {

        // Prepare game data
        Map<String, Object> gameData = TicTacToeGame.getInitialFieldMap();
        gameData.put(TicTacToeGame.OWNER_ID_FIELD, FirebaseAuth.getInstance().getCurrentUser().getUid());
        gameData.put(TicTacToeGame.OPPONENT_ID_FIELD, opponentID);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Create new match
        firestore.collection(COLLECTION_TIC_TAC_TOE_GAMES)
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
            currentGame.put(UserCurrentGame.TYPE_FIELD, COLLECTION_TIC_TAC_TOE_GAMES);
            currentGame.put(UserCurrentGame.GAME_ID_FIELD, documentReference.getId());
            firestore.collection(UserCurrentGame.COLLECTION_USERS_CURRENT_GAME).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(currentGame)
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                        Log.w("debugg", "Current game owner update failed", e);
                    });

            firestore.collection(UserCurrentGame.COLLECTION_USERS_CURRENT_GAME).document(opponentID)
                    .set(currentGame)
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                        Log.w("debugg", "Current game opponent update failed", e);
                    });

            // Attach listener to new match
            this.startNewGame(documentReference.getId());
                })
                .addOnFailureListener(e -> Log.w("debugg", "TicTacToeGame creation failure", e));
    }

    synchronized private void startNewGame(String gameID) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        synchronized (this.currentTicTacToeGame) {
            this.currentTicTacToeGame.reset().setMatchID(gameID);
            this.gameListenerRegistration = firestore.collection(COLLECTION_TIC_TAC_TOE_GAMES).document(gameID)
                    .addSnapshotListener(this.gameListener);
        }
    }

    synchronized private void resetGame() {
        synchronized (this.currentTicTacToeGame) {
            // Remove old TicTAcToeGame
            if (this.gameListenerRegistration != null)
                this.gameListenerRegistration.remove();
            this.currentTicTacToeGame.reset();
        }
    }

    synchronized public ITicTacToeGame getCurrentTicTacToeGame() {
        return this.currentTicTacToeGame;
    }

    private void showDialogAcceptGame() {

        // Obtain owner data

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);
        View view = this.layoutInflater.inflate(R.layout.alert_dialog_accept_game_layout, null);

        TextView textViewMessage = view.findViewById(R.id.text_view_message);
        textViewMessage.setText(Friends.getInstance().getFriendsData().getFriend(this.currentTicTacToeGame.getOwnerID()).getNickname() + " " + this.context.getResources().getString(R.string.accept_game_message));

        alertDialogBuilder.setView(view)
                .setPositiveButton(R.string.button_accept_game, (dialog, which) -> {
                    FirebaseFirestore.getInstance().collection(COLLECTION_TIC_TAC_TOE_GAMES)
                            .document(this.currentTicTacToeGame.getMatchID()).update(TicTacToeGame.ACCEPTED_FIELD, TicTacToeGame.ACCEPT_STATUS_ACCEPTED);
                })
                .setNegativeButton(R.string.button_refuse_game, (dialog, which) -> {
                    FirebaseFirestore.getInstance().collection(COLLECTION_TIC_TAC_TOE_GAMES)
                            .document(this.currentTicTacToeGame.getMatchID()).update(TicTacToeGame.ACCEPTED_FIELD, TicTacToeGame.ACCEPT_STATUS_REFUSED);
                })
        .create().show();
    }
}
