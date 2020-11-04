package com.argame.activities.tic_tac_toe.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.argame.R;

public class TicTacToeFragmentGame extends Fragment {


    public static TicTacToeFragmentGame newInstance() {
        return new TicTacToeFragmentGame();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tic_tac_toe_game_layout, container, false);
    }
}
