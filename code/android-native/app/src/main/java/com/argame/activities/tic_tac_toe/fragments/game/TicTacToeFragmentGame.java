package com.argame.activities.tic_tac_toe.fragments.game;

import android.graphics.Color;
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
import com.argame.arcore.tic_tac_toe.Hologram;
import com.argame.arcore.tic_tac_toe.Playground;
import com.argame.model.data_structures.tic_tac_toe_game.ITicTacToeGame;
import com.argame.model.remote_structures.TicTacToeGameController;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import com.viro.core.Vector;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TicTacToeFragmentGame extends Fragment {

    private ITicTacToeGame ticTacToeGame;
    private ViroView viroView;
    private ARScene arScene;
    private FloatingActionMenu menu;
    private TextView textViewSuggestions;

    // Component 3D
    private Playground playground;
    private Hologram hologram;

    private boolean showPlanes = true;
    private boolean surfacesDetected = false;
    private boolean playgroundPositioned = false;
    private boolean videocallPositioned = false;
    private boolean gameStarted = false;

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
                    public void onClick(int count, Node node, Vector vector) {
                        if (!playgroundPositioned) {

                            // Load graphics objects
                            playground = new Playground(getActivity(), viroView.getViroContext());
                            playground.loadDefaultModel(new AsyncObject3DListener() {
                                @Override
                                public void onObject3DLoaded(final Object3D object, final Object3D.Type type) {

                                    // Show the playground when the model has been loaded
                                    arScene.getRootNode().addChildNode(playground);
                                }

                                @Override
                                public void onObject3DFailed(String s) {
                                    Log.e("debugg", "Load playground failed " + s);
                                }
                            });

                            playground.setPosition(vector);
                            playground.setUserPiece(ticTacToeGame.getUserPiece());

                            textViewSuggestions.setText(R.string.text_view_suggestions_put_videocall_visualizer);
                            showPlanes = false;
                            playgroundPositioned = true;
                        } else if (!videocallPositioned) {

                            hologram = new Hologram(getActivity(), viroView.getViroContext());
                            hologram.loadDefaultModel(new AsyncObject3DListener() {

                                @Override
                                public void onObject3DLoaded(Object3D object3D, Object3D.Type type) {

                                    arScene.getRootNode().addChildNode(object3D);

                                    for (Node plane : surfaces.values())
                                        plane.removeFromParentNode();

                                    surfaces = new HashMap<>();
                                }

                                @Override
                                public void onObject3DFailed(String s) {

                                }
                            });
                            hologram.setPosition(vector);

                            videocallPositioned = true;
                            showPlanes = false;
                            setupEnvironmentTerminated();
                        }
                    }
                    @Override
                    public void onClickState(int i, Node node, ClickState clickState, Vector vector) {
                    }
                });

                if(!surfacesDetected)
                    textViewSuggestions.setText(R.string.text_view_suggestions_put_game_playground);
            }
        }

        @Override
        public void onAnchorUpdated(ARAnchor arAnchor, ARNode arNode) {
            if (showPlanes && arAnchor.getType() == ARAnchor.Type.PLANE) {
                ARPlaneAnchor planeAnchor = (ARPlaneAnchor) arAnchor;

                // Update the mesh surface geometry
                Node node = this.surfaces.get(arAnchor.getAnchorId());
                if(node != null) {
                    Quad plane = (Quad) node.getGeometry();
                    Vector dimensions = planeAnchor.getExtent();
                    plane.setWidth(dimensions.x);
                    plane.setHeight(dimensions.z);
                }
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

        this.viroView.setScene(arScene);
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
        View viewGame = View.inflate(this.getActivity(), R.layout.tic_tac_toe_game_layout, this.viroView);
        this.menu = viewGame.findViewById(R.id.menu_tic_tac_toe_game);
        this.textViewSuggestions = viewGame.findViewById(R.id.text_view_suggestions);

        // Set menu actions
        FloatingActionButton buttonEdit = viewGame.findViewById(R.id.button_edit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            private boolean edit = false;
            private int colorAccent = getActivity().getTheme().obtainStyledAttributes(new int[] {R.attr.colorAccent}).getColor(0, Color.TRANSPARENT);
            private int colorPrimary = getActivity().getTheme().obtainStyledAttributes(new int[] {R.attr.colorPrimary}).getColor(0, Color.TRANSPARENT);


            @Override
            public void onClick(View v) {
                this.edit = !this.edit;

                if(this.edit) {
                    buttonEdit.setColorNormal(this.colorAccent);
                    buttonEdit.setColorPressed(this.colorAccent);
                    buttonEdit.setLabelText(getResources().getString(R.string.menu_item_edit_scene_return_to_game));
                }
                else {
                    buttonEdit.setColorNormal(this.colorPrimary);
                    buttonEdit.setColorPressed(this.colorPrimary);
                    buttonEdit.setLabelText(getResources().getString(R.string.menu_item_edit_scene));
                }

                if (playground != null)
                    playground.setEditMode(this.edit);

                if (hologram != null)
                    hologram.setEditMode(this.edit);
            }
        });

        this.ticTacToeGame = TicTacToeGameController.getInstance().getCurrentTicTacToeGame();
        TicTacToeGameController.getInstance().setSetupNotCompleted();
        return this.viroView;
    }

    private void setupEnvironmentTerminated() {

        this.ticTacToeGame.addOnSetupCompletedStatusListener(gameOnSetupCompletedChange -> {
            if (gameOnSetupCompletedChange.isStarted()) {
                this.playground.setMatrix(this.ticTacToeGame.getMatrix());
                if (this.ticTacToeGame.isMyTurn()) {
                    this.textViewSuggestions.setText(R.string.text_view_suggestion_is_my_turn);
                    this.playground.isMyTurn(true);
                }
                else {
                    this.textViewSuggestions.setText(R.string.text_view_suggestion_is_not_my_turn);
                    this.playground.isMyTurn(false);
                }
            }
            else {
                this.textViewSuggestions.setText(R.string.text_view_suggestions_wait_opponent);
                this.playground.isMyTurn(false);

            }
        });

        TicTacToeGameController.getInstance().setSetupCompleted();
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
