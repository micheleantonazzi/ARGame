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
import com.argame.activities.call.CallActivity;

public class FragmentGames extends Fragment {

    private FragmentGamesViewModel mViewModel;

    public static FragmentGames newInstance() {
        return new FragmentGames();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);

        // Open call activity
        Button buttonTest = view.findViewById(R.id.button_test);
        buttonTest.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CallActivity.class));
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FragmentGamesViewModel.class);
        // TODO: Use the ViewModel
    }
}