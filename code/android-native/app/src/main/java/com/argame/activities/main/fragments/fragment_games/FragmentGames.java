package com.argame.activities.main.fragments.fragment_games;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.argame.R;
import com.argame.activities.main.MainActivity;
import com.argame.activities.settings.account.AccountSettingsActivity;
import com.argame.activities.tic_tac_toe.TicTacToeActivity;

public class FragmentGames extends Fragment {

    private FragmentGamesViewModel mViewModel;

    public static FragmentGames newInstance() {
        return new FragmentGames();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);

        Button buttonStartGame = view.findViewById(R.id.button_start_game);
        buttonStartGame.setOnClickListener(v -> {
            startActivity(new Intent(this.getActivity(), TicTacToeActivity.class));
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FragmentGamesViewModel.class);
    }
}