package com.argame.activities.main.fragments.fragment_games;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.argame.R;
import com.argame.activities.main.fragments.viewmodels.FragmentGamesViewModel;

public class FragmentGames extends Fragment {

    private FragmentGamesViewModel mViewModel;

    public static FragmentGames newInstance() {
        return new FragmentGames();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_games, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FragmentGamesViewModel.class);
        // TODO: Use the ViewModel
    }
}