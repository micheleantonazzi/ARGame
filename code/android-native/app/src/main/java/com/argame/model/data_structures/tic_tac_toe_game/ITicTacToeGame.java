package com.argame.model.data_structures.tic_tac_toe_game;

import com.argame.model.data_structures.user_data.IUser;

public interface ITicTacToeGame {

    String getMatchID();

    int getAcceptedStatus();

    boolean isTerminated();

    String getOpponentID();

    String getOwnerID();

    String getAgoraToken();

    String getAgoraChannel();
}
