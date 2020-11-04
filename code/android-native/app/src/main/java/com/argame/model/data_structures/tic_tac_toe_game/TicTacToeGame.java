package com.argame.model.data_structures.tic_tac_toe_game;

import com.argame.model.data_structures.user_data.IUser;
import com.argame.model.data_structures.user_data.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicTacToeGame implements ITicTacToeGame {

    // Status field
    public static final int ACCEPT_STATUS_NOT_ANSWERED = -1;
    public static final int ACCEPT_STATUS_REFUSED = 0;
    public static final int ACCEPT_STATUS_ACCEPTED = 1;

    // Fields name
    public static final String OWNER_ID_FIELD = "ownerID";
    public static final String OPPONENT_ID_FIELD = "opponentID";
    public static final String AGORA_CHANNEL_FIELD = "agoraChannel";
    public static final String ACCEPTED_FIELD = "accept_status";
    public static final String TERMINATED_FIELD = "terminated";
    public static final String AGORA_TOKEN_FIELD = "agoraToken";
    public static final String MATRIX_FIELD = "matrix";

    private String matchID = "";
    private String ownerID = "";
    private String opponentID = "";
    private String agoraChannel = "";
    private String agoraToken = "";
    private int accepted = -1;
    private boolean terminated = false;
    private List<Integer> matrix = new ArrayList<>(Collections.nCopies(9, -1));


    public static Map<String, Object> getInitialFieldMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(OWNER_ID_FIELD, "");
        map.put(OPPONENT_ID_FIELD, "");
        map.put(ACCEPTED_FIELD, -1);
        map.put(TERMINATED_FIELD, false);
        map.put(AGORA_TOKEN_FIELD, "");
        map.put(AGORA_CHANNEL_FIELD, "");
        map.put(MATRIX_FIELD, new ArrayList<>(Collections.nCopies(9, -1)));
        return map;
    }

    synchronized public TicTacToeGame updateData(Map<String, Object> newData) {
        this.ownerID = String.valueOf(newData.get(OWNER_ID_FIELD));
        this.opponentID = String.valueOf(newData.get(OPPONENT_ID_FIELD));
        this.agoraChannel = String.valueOf(newData.get(AGORA_CHANNEL_FIELD));
        this.agoraToken = String.valueOf(newData.get(AGORA_TOKEN_FIELD));
        this.accepted = Integer.parseInt(String.valueOf(newData.get(ACCEPTED_FIELD)));
        this.terminated = Boolean.parseBoolean(String.valueOf(newData.get(TERMINATED_FIELD)));
        this.matrix = new ArrayList<>((List<Integer>) newData.get(MATRIX_FIELD));
        return this;
    }

    synchronized public boolean isOwner(String playerID) {
        return !this.ownerID.equals("") && !this.opponentID.equals("") && this.ownerID.equals(playerID);
    }

    synchronized public String getOtherPlayerID(String currentPlayerID) {
        if(currentPlayerID.equals(this.ownerID))
            return this.opponentID;
        else if(currentPlayerID.equals(this.opponentID))
            return this.ownerID;
        else
            return "";
    }

    synchronized public TicTacToeGame reset() {
        this.matchID = "";
        this.ownerID = "";
        this.opponentID = "";
        this.agoraChannel = "";
        this.agoraToken = "";
        this.accepted = -1;
        this.terminated = false;
        this.matrix = new ArrayList<>(Collections.nCopies(9, -1));
        return this;
    }

    @Override
    synchronized public String getMatchID() {
        return matchID;
    }

    public TicTacToeGame setMatchID(String matchID) {
        this.matchID = matchID;
        return this;
    }

    @Override
    synchronized public int getAcceptedStatus() {
        return accepted;
    }

    public TicTacToeGame setAccepted(int accepted) {
        this.accepted = accepted;
        return this;
    }

    @Override
    synchronized public boolean isTerminated() {
        return terminated;
    }

    public TicTacToeGame setTerminated(boolean terminated) {
        this.terminated = terminated;
        return this;
    }

    @Override
    synchronized public String getOwnerID() {
        return ownerID;
    }

    synchronized public TicTacToeGame setOwnerID(String ownerID) {
        this.ownerID = ownerID;
        return this;
    }

    @Override
    synchronized public String getOpponentID() {
        return opponentID;
    }

    synchronized public TicTacToeGame setOpponentID(String opponentID) {
        this.opponentID = opponentID;
        return this;
    }

    @Override
    synchronized public String getAgoraChannel() {
        return agoraChannel;
    }

    synchronized public TicTacToeGame setAgoraChannel(String agoraChannel) {
        this.agoraChannel = agoraChannel;
        return this;
    }

    @Override
    synchronized public String getAgoraToken() {
        return agoraToken;
    }

    synchronized public TicTacToeGame setAgoraToken(String agoraToken) {
        this.agoraToken = agoraToken;
        return this;
    }
}
