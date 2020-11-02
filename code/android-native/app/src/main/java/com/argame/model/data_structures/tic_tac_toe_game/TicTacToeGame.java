package com.argame.model.data_structures.tic_tac_toe_game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicTacToeGame implements ITicTacToeGame {

    // Fields name
    public static final String OWNER_ID_FIELD = "ownerID";
    public static final String OPPONENT_ID_FIELD = "opponentID";
    public static final String AGORA_CHANNEL_FIELD = "agoraChannel";
    public static final String ACCEPTED_FIELD = "accepted";
    public static final String TERMINATED_FIELD = "terminated";
    public static final String AGORA_TOKEN_FIELD = "agoraToken";
    public static final String MATRIX_FIELD = "matrix";

    private String matchID = "";
    private String ownerID = "";
    private String opponentID = "";
    private String agoraChannel = "";
    private String agoraToken = "";
    private boolean accepted = false;
    private boolean terminated = false;
    private List<Integer> matrix = new ArrayList<>(Collections.nCopies(9, -1));


    public static Map<String, Object> getInitialFieldMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(OWNER_ID_FIELD, "");
        map.put(OPPONENT_ID_FIELD, "");
        map.put(ACCEPTED_FIELD, false);
        map.put(TERMINATED_FIELD, false);
        map.put(AGORA_TOKEN_FIELD, "");
        map.put(AGORA_CHANNEL_FIELD, "");
        map.put(MATRIX_FIELD, new ArrayList<>(Collections.nCopies(9, -1)));
        return map;
    }
    @Override
    public String getMatchID() {
        return matchID;
    }

    public TicTacToeGame setMatchID(String matchID) {
        this.matchID = matchID;
        return this;
    }

    @Override
    public boolean isAccepted() {
        return accepted;
    }

    public TicTacToeGame setAccepted(boolean accepted) {
        this.accepted = accepted;
        return this;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    public TicTacToeGame setTerminated(boolean terminated) {
        this.terminated = terminated;
        return this;
    }

    @Override
    public String getOwnerID() {
        return ownerID;
    }

    public TicTacToeGame setOwnerID(String ownerID) {
        this.ownerID = ownerID;
        return this;
    }

    @Override
    public String getOpponentID() {
        return opponentID;
    }

    public TicTacToeGame setOpponentID(String opponentID) {
        this.opponentID = opponentID;
        return this;
    }

    @Override
    public String getAgoraChannel() {
        return agoraChannel;
    }

    public TicTacToeGame setAgoraChannel(String agoraChannel) {
        this.agoraChannel = agoraChannel;
        return this;
    }

    @Override
    public String getAgoraToken() {
        return agoraToken;
    }

    public TicTacToeGame setAgoraToken(String agoraToken) {
        this.agoraToken = agoraToken;
        return this;
    }
}
