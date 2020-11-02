package com.argame.activities.main.fragments.fragment_games;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.argame.R;
import com.argame.model.FriendsListAdapter;
import com.argame.model.Database;
import com.argame.model.GameController;
import com.argame.model.data_structures.user_data.IUser;

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

            // Build alert dialog opponent
            alertDialogOpponentBuilder = new AlertDialog.Builder(this.getContext());
            View viewAlertDialogOpponent = getLayoutInflater().inflate(R.layout.alert_dialog_opponent_layout, null);

            // Setup friends recycle view
            RecyclerView recyclerViewFriends = viewAlertDialogOpponent.findViewById(R.id.recycle_view_friends);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerViewFriends.setLayoutManager(layoutManager);
            recyclerViewFriends.setHasFixedSize(true);

            // Set view model
            FriendsListAdapter friendsListAdapter = new FriendsListAdapter(true,
                    null);
            recyclerViewFriends.setAdapter(friendsListAdapter);

            // Submit friend list to adapter
            friendsListAdapter.submitList(Database.getInstance().getUserFriends().getFriendsList());

            AlertDialog alertDialogOpponent = alertDialogOpponentBuilder.setView(viewAlertDialogOpponent)
            .setPositiveButton(R.string.button_confirm_text, (dialog, which) -> {

                // Create new game
                IUser opponent = friendsListAdapter.getSelectedItem();

                GameController.getInstance().createTicTacToeGame(opponent.getUid());
            })
            .setNegativeButton(R.string.button_cancel_text, (dialog, which) -> {
            }).create();

            alertDialogOpponent.show();

            // Set the runnable to enable positive button
            friendsListAdapter.setOnItemClick(
                    () -> alertDialogOpponent.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true));

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