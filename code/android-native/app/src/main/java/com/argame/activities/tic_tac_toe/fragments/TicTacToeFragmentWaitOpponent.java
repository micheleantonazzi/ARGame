package com.argame.activities.tic_tac_toe.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.argame.R;

public class TicTacToeFragmentWaitOpponent extends Fragment {


    public static TicTacToeFragmentWaitOpponent newInstance() {
        return new TicTacToeFragmentWaitOpponent();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tic_tac_toe_wait_opponent, container, false);
    }
}