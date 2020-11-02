package com.argame.model.data_structures.tic_tac_toe_game;

public interface ITicTacToeGame {

    String getMatchID();

    boolean isAccepted();

    boolean isTerminated();

    String getOpponentID();

    String getOwnerID();

    String getAgoraToken();

    String getAgoraChannel();
}
