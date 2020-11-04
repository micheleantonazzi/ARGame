package com.argame.activities.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.argame.R;
import com.argame.activities.tic_tac_toe.fragments.TicTacToeFragmentWaitOpponent;

public class TicTacToeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tic_tac_toe_activity_layout);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TicTacToeFragmentWaitOpponent.newInstance())
                    .commitNow();
        }
    }
}