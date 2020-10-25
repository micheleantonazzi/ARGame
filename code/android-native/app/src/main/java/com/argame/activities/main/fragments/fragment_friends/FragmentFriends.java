package com.argame.activities.main.fragments.fragment_friends;

import androidx.lifecycle.Lifecycle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.argame.R;
import com.argame.model.Database;
import com.argame.model.data_structures.user_data.ListenerUserUpdate;
import com.argame.model.data_structures.user_data.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class FragmentFriends extends Fragment {

    public static FragmentFriends newInstance() {
        return new FragmentFriends();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        // Get recycle view and set layout manager
        RecyclerView recyclerViewFriends = view.findViewById(R.id.recycle_view_friends);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewFriends.setLayoutManager(layoutManager);
        recyclerViewFriends.setHasFixedSize(true);

        // Set view model
        FriendsListAdapter friendsListAdapter = new FriendsListAdapter();
        recyclerViewFriends.setAdapter(friendsListAdapter);

        // Submit friend list to adapter
        friendsListAdapter.submitList(Database.getInstance().getUserFriends().getFriendsList());

        // Add update listener to friends
        ListenerUserUpdate listenerUserUpdate =
                newFriend -> friendsListAdapter.notifyItemChanged(Database.getInstance().getUserFriends().getFriendOrderedNumber(newFriend));
        for(UserInterface friend: Database.getInstance().getUserFriends().getFriendsList())
            friend.addOnUpdateListenerLifecycle(this, Lifecycle.Event.ON_STOP, listenerUserUpdate);

        // Set listener to notify update
        Database.getInstance().getUserFriends().addOnUpdateListenerLifecycle(this, Lifecycle.Event.ON_STOP, userFriends -> {

            // Set update listener to new users
            List<UserInterface> newFriends = new ArrayList<>(userFriends.getFriendsList());
            newFriends.removeAll(friendsListAdapter.getCurrentList());
            for(UserInterface newFriend: newFriends) {
                newFriend.addOnUpdateListenerLifecycle(this, Lifecycle.Event.ON_STOP, listenerUserUpdate);
            }

            friendsListAdapter.submitList(Database.getInstance().getUserFriends().getFriendsList());
        });
        return view;
    }
}