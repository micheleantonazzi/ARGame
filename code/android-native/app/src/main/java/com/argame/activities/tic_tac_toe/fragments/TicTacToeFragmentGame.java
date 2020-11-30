package com.argame.activities.tic_tac_toe.fragments;

import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.argame.R;
import com.argame.arcore.tic_tac_toe.Hologram;
import com.argame.arcore.tic_tac_toe.Playground;
import com.argame.model.data_structures.tic_tac_toe_game.ITicTacToeGame;
import com.argame.model.remote_structures.TicTacToeGameController;
import com.argame.model.remote_structures.UserCurrentGame;
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

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class TicTacToeFragmentGame extends Fragment {

    private static final String PERMISSION_CAMERA = "permissionCamera";
    private static final String PERMISSION_MICROPHONE = "permissionMicrophone";


    private ITicTacToeGame ticTacToeGame;
    private ViroView viroView;
    private ARScene arScene;
    private FloatingActionMenu menu;
    private TextView textViewSuggestions;
    private boolean showPlanes = true;
    private boolean surfacesDetected = false;
    private boolean playgroundPositioned = false;
    private boolean videocallPositioned = true;

    // Permission fields
    private boolean permissionCamera = false;
    private boolean permissionMicrophone = false;

    // Viro components
    private Playground playground;
    private Hologram hologram;
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
                            playground = new Playground(getActivity(), viroView);
                            playground.loadDefaultModel(new AsyncObject3DListener() {
                                @Override
                                public void onObject3DLoaded(final Object3D object, final Object3D.Type type) {

                                    for (Node plane : surfaces.values())
                                        plane.removeFromParentNode();

                                    surfaces = new HashMap<>();

                                    // Show the playground when the model has been loaded
                                    arScene.getRootNode().addChildNode(playground);
                                    setupEnvironmentTerminated();
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

                            hologram = new Hologram(getActivity(), viroView);

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

    // Agora components
    private RtcEngine rtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        // Listen for the onUserOffline callback.
        // This callback occurs when the remote user leaves the channel or drops offline.
        @Override
        public void onUserOffline(final int uid, final int reason) {
        }

        // Listen for the onUserMuterAudio callback.
        // This callback occurs when a remote user stops sending the audio stream.
        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) {
        }

        @Override
        public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
            super.onRemoteAudioStateChanged(uid, state, reason, elapsed);
        }
    };

    public static TicTacToeFragmentGame newInstance(boolean permissionCamera, boolean permissionMicrophone) {
        TicTacToeFragmentGame fragment = new TicTacToeFragmentGame();

        Bundle args = new Bundle();
        args.putBoolean(PERMISSION_CAMERA, permissionCamera);
        args.putBoolean(PERMISSION_MICROPHONE, permissionMicrophone);
        fragment.setArguments(args);

        return fragment;
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

        this.permissionCamera = this.getArguments().getBoolean(PERMISSION_CAMERA);
        this.permissionMicrophone = this.getArguments().getBoolean(PERMISSION_MICROPHONE);

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
            private int colorButtonDisabled = getActivity().getTheme().obtainStyledAttributes(new int[] {R.attr.colorButtonDisabled}).getColor(0, Color.TRANSPARENT);
            private int colorButtonEnabled = getActivity().getTheme().obtainStyledAttributes(new int[] {R.attr.colorPrimary}).getColor(0, Color.TRANSPARENT);


            @Override
            public void onClick(View v) {
                this.edit = !this.edit;

                if(this.edit) {
                    buttonEdit.setColorNormal(this.colorButtonDisabled);
                    buttonEdit.setColorPressed(this.colorButtonDisabled);
                    buttonEdit.setLabelText(getResources().getString(R.string.menu_item_edit_scene_return_to_game));
                }
                else {
                    buttonEdit.setColorNormal(this.colorButtonEnabled);
                    buttonEdit.setColorPressed(this.colorButtonEnabled);
                    buttonEdit.setLabelText(getResources().getString(R.string.menu_item_edit_scene));
                }

                if (playground != null)
                    playground.setEditMode(this.edit);

                if (hologram != null)
                    hologram.setEditMode(this.edit);
            }
        });

        FloatingActionButton buttonMicrophone = viewGame.findViewById(R.id.button_enable_microphone);
        buttonMicrophone.setOnClickListener(new View.OnClickListener() {
            private boolean microphoneEnabled = true;
            private int colorButtonDisabled = getActivity().getTheme().obtainStyledAttributes(new int[] {R.attr.colorButtonDisabled}).getColor(0, Color.TRANSPARENT);
            private int colorButtonEnabled = getActivity().getTheme().obtainStyledAttributes(new int[] {R.attr.colorPrimary}).getColor(0, Color.TRANSPARENT);

            @Override
            public void onClick(View v) {
                this.microphoneEnabled = !this.microphoneEnabled;

                if (this.microphoneEnabled) {
                    buttonMicrophone.setColorNormal(colorButtonEnabled);
                    buttonMicrophone.setColorPressed(colorButtonEnabled);
                    buttonMicrophone.setImageIcon(Icon.createWithResource(getActivity(), R.drawable.icon_mic_24));
                    buttonMicrophone.setLabelText(getResources().getString(R.string.menu_item_microphone_enabled));
                    rtcEngine.muteLocalAudioStream(false);
                }
                else {
                    buttonMicrophone.setColorNormal(colorButtonDisabled);
                    buttonMicrophone.setColorPressed(colorButtonDisabled);
                    buttonMicrophone.setImageIcon(Icon.createWithResource(getActivity(), R.drawable.icon_mic_off_24));
                    buttonMicrophone.setLabelText(getResources().getString(R.string.menu_item_microphone_disabled));
                    rtcEngine.muteLocalAudioStream(true);
                }
            }
        });

        this.ticTacToeGame = TicTacToeGameController.getInstance().getCurrentTicTacToeGame();
        TicTacToeGameController.getInstance().setSetupNotCompleted();

        // Setup agora engine

        // Initialize RTCEngine
        try {
            this.rtcEngine = RtcEngine.create(this.getActivity().getBaseContext(), getString(R.string.agora_app_id), this.mRtcEventHandler);
        } catch (Exception e) {
            Log.e("debugg", Log.getStackTraceString(e));
        }

        if(this.rtcEngine != null && this.permissionMicrophone) {
            this.rtcEngine.enableAudio();
            this.rtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
        }

        this.rtcEngine.joinChannel(this.ticTacToeGame.getAgoraToken(), this.ticTacToeGame.getAgoraChannel(), null, 0);

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

        this.ticTacToeGame.addOnTurnChangeListener(gameOnTurnChanged -> {
            if (ticTacToeGame.isMyTurn()) {
                playground.setMatrix(ticTacToeGame.getMatrix());

                if (this.ticTacToeGame.isLooser())
                    textViewSuggestions.setText(R.string.text_view_suggestion_lose);
                else if (this.ticTacToeGame.isTerminated())
                    textViewSuggestions.setText(R.string.text_view_suggestion_parity);
                else {
                    textViewSuggestions.setText(R.string.text_view_suggestion_is_my_turn);
                    playground.isMyTurn(true);
                }
            }

            else {
                if (this.ticTacToeGame.isWinner()) {
                    UserCurrentGame.getInstance().setMatchNotActive(this.ticTacToeGame.getOwnerID(), this.ticTacToeGame.getOpponentID());
                    textViewSuggestions.setText(R.string.text_view_suggestion_win);
                }
                else if (this.ticTacToeGame.isTerminated()) {
                    textViewSuggestions.setText(R.string.text_view_suggestion_parity);
                    UserCurrentGame.getInstance().setMatchNotActive(this.ticTacToeGame.getOwnerID(), this.ticTacToeGame.getOpponentID());
                }
                else
                    textViewSuggestions.setText(R.string.text_view_suggestion_is_not_my_turn);
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
        this.rtcEngine.leaveChannel();
        RtcEngine.destroy();
    }
}
