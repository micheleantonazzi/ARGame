package com.argame.activities.tic_tac_toe.fragments.game;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.viro.core.Texture;
import com.viro.core.Vector;
import com.viro.core.ViroView;
import com.viro.core.ViroViewARCore;

import java.io.IOException;
import java.io.InputStream;
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
    private List<Node> planesClickable = new ArrayList<>(9);
    private boolean showPlanes = true;
    private boolean surfacesDetected = false;
    private boolean playgroundPositioned = false;
    private boolean videocallPositioned = false;
    private boolean gameStarted = false;
    private PlanesAnimator planesAnimator = new PlanesAnimator(this.planesClickable);

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
                        Log.d("debugg", "clicked");
                        if (!playgroundPositioned) {
                            // Load graphics objects
                            playground = new Object3D();
                            playground.loadModel(viroView.getViroContext(), Uri.parse("file:///android_asset/tictactoe/playground/playground.obj"), Object3D.Type.OBJ, new AsyncObject3DListener() {
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

                            // Create planes to click
                            for(int i = 0; i < 9; i++) {
                                Quad plane = new Quad(6.5f,6.5f);
                                Material material = new Material();
                                material.setDiffuseColor(Color.argb(0, 255, 255, 255));
                                plane.setMaterials(Arrays.asList(material));
                                Node planeNode = new Node();
                                planeNode.setGeometry(plane);
                                playground.addChildNode(planeNode);
                                planesClickable.add(planeNode);
                            }

                            // Position clickable planes
                            planesClickable.get(0).setPosition(new Vector(-8, 8, 2.28f));
                            planesClickable.get(1).setPosition(new Vector(0, 8, 2.28f));
                            planesClickable.get(2).setPosition(new Vector(8, 8, 2.28f));
                            planesClickable.get(3).setPosition(new Vector(-8, 0, 2.28f));
                            planesClickable.get(4).setPosition(new Vector(0, 0, 2.28f));
                            planesClickable.get(5).setPosition(new Vector(8, 0, 2.28f));
                            planesClickable.get(6).setPosition(new Vector(-8, -8, 2.28f));
                            planesClickable.get(7).setPosition(new Vector(0, -8, 2.28f));
                            planesClickable.get(8).setPosition(new Vector(8, -8, 2.28f));

                            // Set playground's scale and rotation
                            playground.setScale(new Vector(PLAYGROUND_SCALE, PLAYGROUND_SCALE, PLAYGROUND_SCALE));
                            playground.setRotation(new Vector(-Math.toRadians(90.0), 0, 0));
                            playground.setPosition(vector);

                            textViewSetupEnvironment.setText(R.string.text_view_setup_environment_put_videocall_visualizer);
                            showPlanes = false;
                            playgroundPositioned = true;
                        }
                        else if (!videocallPositioned) {
                            Log.d("debugg", "position hologgram");
                            final Bitmap bitmapParticle = getBitmapFromAsset(getActivity(), "hologram/particle_texture.png");
                            final Bitmap bitmapBase = getBitmapFromAsset(getActivity(), "hologram/base_texture.jpg");
                            Object3D hologram = new Object3D();
                            hologram.loadModel(viroView.getViroContext(), Uri.parse("file:///android_asset/hologram/hologram_base.obj"), Object3D.Type.OBJ, new AsyncObject3DListener() {
                                @Override
                                public void onObject3DLoaded(final Object3D object, final Object3D.Type type) {
                                    Log.d("debugg", "Hologram loaded");
                                    // Show the playground when the model has been loaded
                                    Texture particleTexture = new Texture(bitmapParticle, Texture.Format.RGBA8, false, false);
                                    Texture baseTexture = new Texture(bitmapBase, Texture.Format.RGBA8, false, false);
                                    hologram.getMaterials().get(0).setDiffuseTexture(particleTexture);
                                    hologram.getMaterials().get(1).setDiffuseTexture(baseTexture);
                                    arScene.getRootNode().addChildNode(hologram);

                                }

                                @Override
                                public void onObject3DFailed(String s) {
                                    Log.e("debugg", "Load hologram failed " + s);
                                }
                            });

                            hologram.setScale(new Vector(0.01, 0.01, 0.01));
                            hologram.setPosition(vector);

                            videocallPositioned = true;
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

        this.viroView.setScene(arScene);
    }

    private Bitmap getBitmapFromAsset(final Context context, String assetName) {
        AssetManager assetManager = context.getResources().getAssets();
        InputStream imageStream;
        try {
            imageStream = assetManager.open(assetName);
        } catch (IOException exception) {
            Log.w("debugg", "Unable to find image [" + assetName + "] in assets! Error: "
                    + exception.getMessage());
            return null;
        }
        return BitmapFactory.decodeStream(imageStream);
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
