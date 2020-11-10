package com.argame.model.data_structures.tic_tac_toe_game;

import com.argame.model.data_structures.user_data.IUser;

import java.util.List;

public interface ITicTacToeGame {

    String getMatchID();

    int getAcceptedStatus();

    boolean isTerminated();

    String getOpponentID();

    String getOwnerID();

    String getAgoraToken();

    String getAgoraChannel();

    boolean isOwner();

    boolean isOpponent();

    void addOnUpdateAcceptedStatusListener(ListenerTicTacToeGameUpdate listener);

    void addOnSetupCompletedStatusListener(ListenerTicTacToeGameUpdate listener);

    void addOnTurnChangeListener(ListenerTicTacToeGameUpdate listener);

    boolean isStarted();

    boolean isMyTurn();

    long getUserPiece();

    List<Long> getMatrix();
}
