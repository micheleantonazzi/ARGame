package com.argame.activities.tic_tac_toe.fragments.game;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.argame.R;
import com.viro.core.ARAnchor;
import com.viro.core.ARNode;
import com.viro.core.ARPlaneAnchor;
import com.viro.core.ARScene;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.ClickListener;
import com.viro.core.ClickState;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.OmniLight;
import com.viro.core.Quad;
import com.viro.core.Surface;
import com.viro.core.Texture;
import com.viro.core.Vector;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TicTacToeFragmentGame extends Fragment {

    private static final float PLAYGROUND_SCALE = 0.03f;
    private ViroView viroView;
    private ARScene arScene;
    private TextView textViewSetupEnvironment;

    private Object3D playground;
    private boolean showPlanes = true;
    private boolean playgroundPositioned = false;
    private boolean gameStarted = false;

    private boolean surfacesDetected = false;

    private ARScene.Listener trackedPlanesListener = new ARScene.Listener() {

        private HashMap<String, Node> surfaces = new HashMap<>();

        @Override
        public void onTrackingInitialized() {

        }

        @Override
        public void onTrackingUpdated(ARScene.TrackingState trackingState, ARScene.TrackingStateReason trackingStateReason) {

        }

        @Override
        public void onAmbientLightUpdate(float v, Vector vector) {

        }

        @Override
        public void onAnchorFound(ARAnchor arAnchor, ARNode arNode) {

            // Spawn a visual plane if a PlaneAnchor was found
            if (showPlanes && arAnchor.getType() == ARAnchor.Type.PLANE){
                ARPlaneAnchor planeAnchor = (ARPlaneAnchor) arAnchor;

                // Create the visual geometry representing this plane
                Vector dimensions = planeAnchor.getExtent();
                Quad plane = new Quad(1,1);
                plane.setWidth(dimensions.x);
                plane.setHeight(dimensions.z);

                // Set a default material for this plane.
                Material material = new Material();
                material.setDiffuseColor(Color.parseColor("#BF000000"));
                plane.setMaterials(Arrays.asList(material));

                // Attach it to the node
                Node planeNode = new Node();
                planeNode.setGeometry(plane);
                planeNode.setRotation(new Vector(-Math.toRadians(90.0), 0, 0));
                planeNode.setPosition(planeAnchor.getCenter());

                // Attach this planeNode to the anchor's arNode
                arNode.addChildNode(planeNode);
                this.surfaces.put(arAnchor.getAnchorId(), planeNode);

                // Attach click listeners to be notified upon a plane onClick.
                planeNode.setClickListener(new ClickListener() {
                    @Override
                    public void onClick(int i, Node node, Vector vector) {
                        if (!playgroundPositioned) {
                            createPlayground(vector);
                            showPlanes = false;
                            playgroundPositioned = true;
                        }
                    }

                    @Override
                    public void onClickState(int i, Node node, ClickState clickState, Vector vector) {
                    }
                });


                if(!surfacesDetected)
                    textViewSetupEnvironment.setText(R.string.text_view_setup_environment_put_game_playground);
            }
        }

        @Override
        public void onAnchorUpdated(ARAnchor arAnchor, ARNode arNode) {
            if (arAnchor.getType() == ARAnchor.Type.PLANE) {
                ARPlaneAnchor planeAnchor = (ARPlaneAnchor) arAnchor;

                // Update the mesh surface geometry
                Node node = this.surfaces.get(arAnchor.getAnchorId());
                Quad plane = (Quad) node.getGeometry();
                Vector dimensions = planeAnchor.getExtent();
                plane.setWidth(dimensions.x);
                plane.setHeight(dimensions.z);
            }
        }

        @Override
        public void onAnchorRemoved(ARAnchor arAnchor, ARNode arNode) {
            this.surfaces.remove(arAnchor.getAnchorId());
        }
    };

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
                displayScene();
            }

            @Override
            public void onFailure(ViroViewARCore.StartupError error, String errorMessage) {
                Log.e("debugg", "Error initializing AR\n" + errorMessage);
            }
        });

        // Inflate the view to setup the AR environment
        View viewSetupEnvironment = View.inflate(this.getActivity(), R.layout.tic_tac_toe_setup_environment_layout, this.viroView);
        this.textViewSetupEnvironment = viewSetupEnvironment.findViewById(R.id.text_view_setup_environment);

        return this.viroView;
    }


    private void displayScene() {
        this.arScene = new ARScene();
        this.arScene.displayPointCloud(false);

        // Add some lights to the scene; this will give the Android's some nice illumination.
        Node rootNode = this.arScene.getRootNode();
        List<Vector> lightPositions = new ArrayList<>();
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

        // Set listener to detect planes
        this.arScene.setListener(this.trackedPlanesListener);

        // Load graphics objects
        this.playground = new Object3D();
        this.playground.setRotation(new Vector(-Math.toRadians(90.0), 0, 0));
        this.playground.setScale(new Vector(PLAYGROUND_SCALE, PLAYGROUND_SCALE, PLAYGROUND_SCALE));
        this.playground.loadModel(viroView.getViroContext(), Uri.parse("file:///android_asset/tictactoe/playground/playground.obj"), Object3D.Type.OBJ, new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(final Object3D object, final Object3D.Type type) {
                Log.d("debugg", "Playground loaded");
            }

            @Override
            public void onObject3DFailed(String s) {
                Log.e("debugg", "Load playground failed " + s);
            }
        });
        this.viroView.setScene(arScene);
    }

    synchronized private void createPlayground(Vector position) {
        this.playground.setPosition(position);
        this.arScene.getRootNode().addChildNode(this.playground);
        Log.d("debugg", "Positioned");
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
