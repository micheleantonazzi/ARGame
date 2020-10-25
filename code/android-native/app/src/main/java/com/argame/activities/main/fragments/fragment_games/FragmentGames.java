package com.argame.activities.main.fragments.fragment_games;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.argame.R;
import com.argame.activities.main.MainActivity;
import com.argame.activities.main.fragments.fragment_friends.FriendsListAdapter;
import com.argame.activities.settings.account.AccountSettingsActivity;
import com.argame.activities.tic_tac_toe.TicTacToeActivity;
import com.argame.model.Database;

public class FragmentGames extends Fragment {

    private AlertDialog.Builder alertDialogOpponentBuilder;

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
            //startActivity(new Intent(this.getActivity(), TicTacToeActivity.class));

            // Build alert dialog opponent
            alertDialogOpponentBuilder = new AlertDialog.Builder(this.getContext());
            View viewAlertDialogOpponent = getLayoutInflater().inflate(R.layout.alert_dialog_opponent_layout, null);

            // Setup friends recycle view
            RecyclerView recyclerViewFriends = viewAlertDialogOpponent.findViewById(R.id.recycle_view_friends);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerViewFriends.setLayoutManager(layoutManager);
            recyclerViewFriends.setHasFixedSize(true);

            // Set view model
            FriendsListAdapter friendsListAdapter = new FriendsListAdapter();
            recyclerViewFriends.setAdapter(friendsListAdapter);

            // Submit friend list to adapter
            friendsListAdapter.submitList(Database.getInstance().getUserFriends().getFriendsList());

            AlertDialog alertDialogOpponent = alertDialogOpponentBuilder.setView(viewAlertDialogOpponent)
            .setPositiveButton(R.string.button_confirm_text, (dialog, which) -> {
                Log.d("debugg", "confirm");
            })
            .setNegativeButton(R.string.button_cancel_text, (dialog, which) -> {
                Log.d("debugg", "cancel");
            }).create();

            alertDialogOpponent.show();

            alertDialogOpponent.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FragmentGamesViewModel.class);
    }
}