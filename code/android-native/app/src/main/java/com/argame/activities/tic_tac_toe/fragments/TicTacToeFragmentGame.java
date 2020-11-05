package com.argame.activities.tic_tac_toe.fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.argame.R;
import com.viro.core.ARScene;
import com.viro.core.ClickListener;
import com.viro.core.ClickState;
import com.viro.core.Node;
import com.viro.core.OmniLight;
import com.viro.core.Texture;
import com.viro.core.Vector;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeFragmentGame extends Fragment {

    private ViroView viroView;
    private ARScene arScene;

    public static TicTacToeFragmentGame newInstance() {
        return new TicTacToeFragmentGame();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.viroView = new ViroViewARCore(this.getActivity(), new ViroViewARCore.StartupListener() {
            @Override
            public void onSuccess() {
                Log.d("debugg", "created");
                displayScene();
            }

            @Override
            public void onFailure(ViroViewARCore.StartupError error, String errorMessage) {
                Log.e("debugg", "Error initializing AR\n" + errorMessage);
            }
        });

        return this.viroView;
    }


    private void displayScene() {
        this.arScene = new ARScene();
        this.arScene.displayPointCloud(true);

        // Add some lights to the scene; this will give the Android's some nice illumination.
        Node rootNode = this.arScene.getRootNode();
        List<Vector> lightPositions = new ArrayList<Vector>();
        lightPositions.add(new Vector(-10,  10, 1));
        lightPositions.add(new Vector(10,  10, 1));

        float intensity = 300;
        List<Integer> lightColors = new ArrayList();
        lightColors.add(Color.WHITE);
        lightColors.add(Color.WHITE);

        for (int i = 0; i < lightPositions.size(); i++) {
            OmniLight light = new OmniLight();
            light.setColor(lightColors.get(i));
            light.setPosition(lightPositions.get(i));
            light.setAttenuationStartDistance(20);
            light.setAttenuationEndDistance(30);
            light.setIntensity(intensity);
            rootNode.addLight(light);
        }

        this.viroView.setScene(arScene);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.viroView.onActivityStarted(this.getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        this.viroView.onActivityResumed(this.getActivity());
    }

    @Override
    public void onPause(){
        super.onPause();
        this.viroView.onActivityPaused(this.getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        this.viroView.onActivityStopped(this.getActivity());
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.viroView.onActivityDestroyed(this.getActivity());
    }
}
