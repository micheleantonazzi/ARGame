package com.argame.activities.main.fragments.fragment_friends;

import androidx.lifecycle.ViewModelProvider;

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
import com.argame.activities.main.fragments.viewmodels.FragmentFriendsViewModel;
import com.argame.utilities.FriendsListAdapter;

public class FragmentFriends extends Fragment {

    private FragmentFriendsViewModel mViewModel;

    public static FragmentFriends newInstance() {
        return new FragmentFriends();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        RecyclerView recyclerViewFriends = view.findViewById(R.id.recycle_view_friends);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewFriends.setLayoutManager(layoutManager);

        FriendsListAdapter friendsListAdapter = new FriendsListAdapter(new String[]{"ci","dsfsf", "dsfsdf"});
        recyclerViewFriends.setAdapter(friendsListAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FragmentFriendsViewModel.class);
        // TODO: Use the ViewModel
    }

}