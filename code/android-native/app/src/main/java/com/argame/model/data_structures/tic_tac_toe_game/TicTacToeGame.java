package com.argame.model.data_structures.tic_tac_toe_game;

import android.view.WindowInsets;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class TicTacToeGame implements ITicTacToeGame {

    // Status field
    public static final int ACCEPT_STATUS_NOT_ANSWERED = -1;
    public static final int ACCEPT_STATUS_REFUSED = 0;
    public static final int ACCEPT_STATUS_ACCEPTED = 1;

    // Turn
    private static final String OWNER = "OWNER";
    private static final String OPPONENT = "OPPONENT";

    // Piece X O
    public static final long PIECE_NEUTRAL = -1;
    public static final long PIECE_X = 0;
    public static final long PIECE_O = 1;
    public static final long PIECE_OWNER = PIECE_X;
    public static final long PIECE_OPPONENT = PIECE_O;

    // Fields name
    public static final String OWNER_ID_FIELD = "ownerID";
    public static final String OPPONENT_ID_FIELD = "opponentID";
    public static final String AGORA_CHANNEL_FIELD = "agoraChannel";
    public static final String ACCEPTED_FIELD = "acceptStatus";
    public static final String TERMINATED_FIELD = "terminated";
    public static final String OWNER_SETUP_COMPLETED_FIELD = "ownerSetupCompleted";
    public static final String OPPONENT_SETUP_COMPLETED_FIELD = "opponentSetupCompleted";
    public static final String TURN_FIELD = "turn";
    public static final String WINNER_FIELD = "winner";
    public static final String AGORA_TOKEN_FIELD = "agoraToken";
    public static final String MATRIX_FIELD = "matrix";

    // Remote fields
    private String matchID = "";
    private String ownerID = "";
    private String opponentID = "";
    private String agoraChannel = "";
    private String agoraToken = "";
    private boolean ownerSetupCompleted = false;
    private boolean opponentSetupCompleted = false;
    private String turn = "";
    private int accepted = -2;
    private boolean terminated = false;
    private String winner = "";
    private List<Long> matrix = new ArrayList<Long>(Collections.nCopies(9, (long) -1));

    // Fields that exist locally
    private boolean isOwner = false;
    private boolean isOpponent = false;

    private Set<ListenerTicTacToeGameUpdate> listenersAcceptedStatus = new HashSet<>();
    private Set<ListenerTicTacToeGameUpdate> listenersSetupCompleted = new HashSet<>();
    private Set<ListenerTicTacToeGameUpdate> listenersTurnChanged = new HashSet<>();

    private void notifyAcceptedStatusListeners() {
        for(ListenerTicTacToeGameUpdate listener: this.listenersAcceptedStatus)
            listener.update(this);
    }

    private void notifySetupCompletedListeners() {
        for(ListenerTicTacToeGameUpdate listener: this.listenersSetupCompleted)
            listener.update(this);
    }

    private void notifyTurnChangedListeners() {
        for(ListenerTicTacToeGameUpdate listener: this.listenersTurnChanged)
            listener.update(this);
    }

    public static Map<String, Object> getInitialFieldMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(OWNER_ID_FIELD, "");
        map.put(OPPONENT_ID_FIELD, "");
        map.put(ACCEPTED_FIELD, -1);
        map.put(TERMINATED_FIELD, false);
        map.put(AGORA_TOKEN_FIELD, "");
        map.put(AGORA_CHANNEL_FIELD, "");
        map.put(OWNER_SETUP_COMPLETED_FIELD, false);
        map.put(OPPONENT_SETUP_COMPLETED_FIELD, false);
        map.put(TURN_FIELD, new Random().nextInt(2) % 2 == 0 ? OWNER : OPPONENT);
        map.put(WINNER_FIELD, "");
        map.put(MATRIX_FIELD, new ArrayList<>(Collections.nCopies(9, -1)));
        return map;
    }

    static private boolean checkEquality(long[] array, int length) {
        for(int i = 0; i < length - 1; ++i) {
            if (array[i] != array[i + 1])
                return false;
        }
        return true;
    }

    static public String checkWinner(List<Long> matrix) {

        // Check rows
        for(int i = 0; i < 3; i++) {
            long[] row = new long[3];
            for(int y = 0; y < 3; y++) {
                row[y] = matrix.get(i * 3 + y);
            }
            if (row[0] != PIECE_NEUTRAL && checkEquality(row, 3))
                return row[0] == PIECE_OWNER ? OWNER : OPPONENT;
        }

        // Check columns
        for(int i = 0; i < 3; i++) {
            long[] column = new long[3];
            for(int y = 0; y < 3; y++) {
                column[y] = matrix.get(i + y * 3);
            }
            if (column[0] != PIECE_NEUTRAL && checkEquality(column, 3))
                return column[0] == PIECE_OWNER ? OWNER : OPPONENT;
        }

        // Check diagonals
        if (matrix.get(0) != PIECE_NEUTRAL && matrix.get(0).equals(matrix.get(4)) &&
                matrix.get(4).equals(matrix.get(8)))
            return matrix.get(0) == PIECE_OWNER ? OWNER : OPPONENT;

        if (matrix.get(2) != PIECE_NEUTRAL && matrix.get(2).equals(matrix.get(4)) &&
                matrix.get(4).equals(matrix.get(6)))
            return matrix.get(2) == PIECE_OWNER ? OWNER : OPPONENT;

        return "";
    }

    synchronized public TicTacToeGame updateData(Map<String, Object> newData) {
        this.ownerID = String.valueOf(newData.get(OWNER_ID_FIELD));
        this.opponentID = String.valueOf(newData.get(OPPONENT_ID_FIELD));
        this.agoraChannel = String.valueOf(newData.get(AGORA_CHANNEL_FIELD));
        this.agoraToken = String.valueOf(newData.get(AGORA_TOKEN_FIELD));
        this.winner = String.valueOf(newData.get(WINNER_FIELD));

        // Update fields with listeners
        int oldAcceptedStatus = this.accepted;
        this.accepted = Integer.parseInt(String.valueOf(newData.get(ACCEPTED_FIELD)));

        boolean oldOwnerSetupCompleted = this.ownerSetupCompleted;
        boolean oldOpponentSetupCompleted = this.opponentSetupCompleted;
        this.ownerSetupCompleted = Boolean.parseBoolean(String.valueOf(newData.get(OWNER_SETUP_COMPLETED_FIELD)));
        this.opponentSetupCompleted = Boolean.parseBoolean(String.valueOf(newData.get(OPPONENT_SETUP_COMPLETED_FIELD)));

        String oldTurn = this.turn;
        this.turn = String.valueOf(newData.get(TURN_FIELD));

        this.terminated = Boolean.parseBoolean(String.valueOf(newData.get(TERMINATED_FIELD)));
        this.matrix = new ArrayList<>((List<Long>) newData.get(MATRIX_FIELD));

        // Call listeners
        if (this.accepted != oldAcceptedStatus)
            this.notifyAcceptedStatusListeners();

        if (this.ownerSetupCompleted != oldOwnerSetupCompleted || this.opponentSetupCompleted != oldOpponentSetupCompleted)
            this.notifySetupCompletedListeners();

        if (!this.turn.equals(oldTurn))
            this.notifyTurnChangedListeners();

        // Update local fields
        this.isOwner = this.isOwner();
        this.isOpponent = this.isOpponent();
        return this;
    }

    @Override
    synchronized public boolean isOwner() {
        return !this.ownerID.equals("") && !this.opponentID.equals("") && this.ownerID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    @Override
    synchronized public boolean isOpponent() {
        return !this.ownerID.equals("") && !this.opponentID.equals("") && !this.ownerID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
        this.accepted = -2;
        this.terminated = false;
        this.ownerSetupCompleted = false;
        this.opponentSetupCompleted = false;
        this.turn = "";
        this.listenersAcceptedStatus = new HashSet<>();
        this.listenersSetupCompleted = new HashSet<>();
        this.listenersTurnChanged = new HashSet<>();
        this.winner = "";
        this.matrix = new ArrayList<>(Collections.nCopies(9, (long) -1));
        return this;
    }

    @Override
    synchronized public void addOnUpdateAcceptedStatusListener(ListenerTicTacToeGameUpdate listener) {
        this.listenersAcceptedStatus.add(listener);
    }

    @Override
    public void addOnSetupCompletedStatusListener(ListenerTicTacToeGameUpdate listener) {
        this.listenersSetupCompleted.add(listener);
    }

    @Override
    public void addOnTurnChangeListener(ListenerTicTacToeGameUpdate listener) {
        this.listenersTurnChanged.add(listener);
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

    @Override
    synchronized public boolean isStarted() {
        return this.ownerSetupCompleted && this.opponentSetupCompleted;
    }

    @Override
    synchronized public boolean isMyTurn() {
        return this.isOwner && this.turn.equals(OWNER) || this.isOpponent && this.turn.equals(OPPONENT);
    }

    @Override
    synchronized public long getUserPiece() {
        if (this.isOwner())
            return PIECE_X;

        return PIECE_O;
    }

    @Override
    synchronized public List<Long> getMatrix() {
        return this.matrix;
    }

    synchronized public List<Long> getMatrixMakeMove(int position) {
        List<Long> newMatrix = new ArrayList<>(this.matrix);
        newMatrix.set(position, getUserPiece());

        return newMatrix;
    }

    synchronized public String nextTurn() {
        if (this.turn.equals(OWNER))
            return OPPONENT;
        else
            return OWNER;
    }

    @Override
    synchronized public boolean isWinner() {
        return this.winner.equals(this.getRole());
    }

    @Override
    synchronized public boolean isLooser() {
        return this.winner.equals(this.getOtherPlayerRole());
    }

    @Override
    public String getRole() {
        if (this.isOwner)
            return OWNER;

        return OPPONENT;
    }

    public String getOtherPlayerRole() {
        if (this.isOwner)
            return OPPONENT;

        return OWNER;
    }
}
