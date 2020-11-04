package com.argame.activities.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.argame.R;
import com.argame.activities.tic_tac_toe.fragments.TicTacToeFragmentGame;
import com.argame.activities.tic_tac_toe.fragments.TicTacToeFragmentWaitOpponent;
import com.argame.model.TicTacToeGameController;
import com.argame.model.data_structures.tic_tac_toe_game.ITicTacToeGame;
import com.argame.model.data_structures.tic_tac_toe_game.TicTacToeGame;

public class TicTacToeActivity extends AppCompatActivity {

    private ITicTacToeGame ticTacToeGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("debugg", "oncreate");
        // Set window styles for fullscreen-window size
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        this.ticTacToeGame = TicTacToeGameController.getInstance().getCurrentTicTacToeGame();

        setContentView(R.layout.tic_tac_toe_activity_layout);
        if (savedInstanceState == null) {
            if (this.ticTacToeGame.isOwner())
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, TicTacToeFragmentWaitOpponent.newInstance())
                        .commit();
            else
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, TicTacToeFragmentGame.newInstance())
                .commit();
        }

        // Attach listener to game
        if (this.ticTacToeGame.isOwner()) {
            this.ticTacToeGame.addOnUpdateAcceptedStatus(gameAcceptedStatusChanged -> {
                if (gameAcceptedStatusChanged.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_REFUSED)
                    finish();
                else if (this.ticTacToeGame.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_ACCEPTED){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, TicTacToeFragmentGame.newInstance())
                            .commit();
                }
            });
        }
    }
}