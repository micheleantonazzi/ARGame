package com.argame.arcore.tic_tac_toe;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import com.argame.arcore.Component3D;
import com.argame.model.data_structures.tic_tac_toe_game.TicTacToeGame;
import com.argame.model.remote_structures.TicTacToeGameController;
import com.viro.core.AnimationTimingFunction;
import com.viro.core.AnimationTransaction;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.ClickListener;
import com.viro.core.ClickState;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.Quad;
import com.viro.core.Vector;
import com.viro.core.ViroContext;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Playground extends Component3D {

    public static final Vector INITIAL_SCALE = new Vector(0.03f, 0.03f, 0.03f);

    private boolean isMyTurn = false;
    private int userPiece = -1;
    private List<Node> planesClickable = new ArrayList<>(9);

    private PlanesAnimator planesAnimator = new PlanesAnimator(planesClickable);


    public Playground(Context context, ViroContext viroContext) {
        super(context, viroContext, Uri.parse("file:///android_asset/tictactoe/playground/playground.obj"));

        this.setDragType(DragType.FIXED_TO_WORLD);

        this.setDefaultRotationX(-Math.toRadians(90.0f));
        this.setScale(INITIAL_SCALE);
        this.setRotation(new Vector(-Math.toRadians(90.0), 0, 0));
    }

    @Override
    public void loadDefaultModel(AsyncObject3DListener listener) {
        this.loadModel(this.getViroContext(), this.getUri(), Type.OBJ, new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(Object3D object3D, Type type) {
                createClickablePlanes();
                listener.onObject3DLoaded(object3D, type);
            }

            @Override
            public void onObject3DFailed(String s) {
                listener.onObject3DFailed(s);
            }
        });
    }

    private void createClickablePlanes() {
        // Create planes to click
        for(int i = 0; i < 9; i++) {
            Quad plane = new Quad(6.5f,6.5f);
            Material material = new Material();
            material.setDiffuseColor(Color.argb(0, 255, 255, 255));
            plane.setMaterials(Arrays.asList(material));
            Node planeNode = new Node();
            planeNode.setGeometry(plane);
            this.addChildNode(planeNode);
            this.planesClickable.add(planeNode);
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

        // Add listener
        for(Node plane: this.planesClickable)
            plane.setClickListener(new ClickListener() {
                @Override
                public void onClick(int i, Node node, Vector vector) {

                    if (!isMyTurn || Playground.this.isEditModeEnabled())
                        return;

                    if (userPiece == TicTacToeGame.PIECE_X) {
                        PieceX pieceX = new PieceX(getContext(), getViroContext());
                        pieceX.loadDefaultModel(new AsyncObject3DListener() {
                            @Override
                            public void onObject3DLoaded(Object3D object3D, Type type) {
                                Playground.this.addChildNode(pieceX);
                            }

                            @Override
                            public void onObject3DFailed(String s) {

                            }
                        });
                        pieceX.setPosition(plane.getPositionRealtime());
                    }
                    else if (userPiece == TicTacToeGame.PIECE_O) {
                        PieceO pieceO = new PieceO(getContext(), getViroContext());
                        pieceO.loadDefaultModel(new AsyncObject3DListener() {
                            @Override
                            public void onObject3DLoaded(Object3D object3D, Type type) {
                                Playground.this.addChildNode(pieceO);
                            }

                            @Override
                            public void onObject3DFailed(String s) {

                            }
                        });
                        pieceO.setPosition(plane.getPositionRealtime());
                    }

                    // Delete clickable plane
                    int planePosition = planesClickable.indexOf(plane);
                    plane.removeFromParentNode();
                    planesClickable.add(planePosition, null);

                    TicTacToeGameController.getInstance().makeMove(planePosition);
                    isMyTurn(false);
                }

                @Override
                public void onClickState(int i, Node node, ClickState clickState, Vector vector) {

                }
            });
    }

    public void isMyTurn(boolean isMyTurn) {
        this.isMyTurn = isMyTurn;

        if (this.isMyTurn)
            this.planesAnimator.startAnimation();
        else
            this.planesAnimator.stopAnimation();
    }

    public void setUserPiece(int userPiece) {
        this.userPiece = userPiece;
    }

    private class PlanesAnimator {

        private List<Node> planes;
        private AnimationTransaction animationIncrease;
        private AnimationTransaction animationDecrease;
        private boolean isAnimationActive = true;

        public PlanesAnimator(List<Node> planes) {
            this.planes = planes;
        }

        synchronized public void startAnimation() {
            this.isAnimationActive = true;
            this.startIncreaseAnimation();
        }

        synchronized public void stopAnimation() {
            this.isAnimationActive = false;

            if (this.animationIncrease != null)
                this.animationIncrease.terminate();
            if (this.animationDecrease != null)
                this.animationDecrease.terminate();
            this.resetColors();
        }

        private void resetColors() {
            for(Node plane: planes)
                if (plane != null)
                    plane.getGeometry().getMaterials().get(0).setDiffuseColor(Color.parseColor("#00C6AA56"));
        }

        synchronized private void startIncreaseAnimation() {
            if (!this.isAnimationActive)
                return;

            AnimationTransaction.begin();
            AnimationTransaction.setAnimationDuration(1000);
            AnimationTransaction.setTimingFunction(AnimationTimingFunction.Linear);
            for(Node plane: this.planes)
                if (plane != null)
                    plane.getGeometry().getMaterials().get(0).setDiffuseColor(Color.parseColor("#99C6AA56"));

            AnimationTransaction.setListener(animationTransaction -> startDecreaseAnimation());

            this.animationIncrease = AnimationTransaction.commit();
        }

        synchronized private void startDecreaseAnimation() {

            if (!this.isAnimationActive)
                return;

            AnimationTransaction.begin();
            AnimationTransaction.setAnimationDuration(1000);
            AnimationTransaction.setTimingFunction(AnimationTimingFunction.Linear);
            for(Node plane: this.planes)
                if (plane != null)
                    plane.getGeometry().getMaterials().get(0).setDiffuseColor(Color.parseColor("#10C6AA56"));

            AnimationTransaction.setListener(animationTransaction -> startIncreaseAnimation());
            this.animationDecrease = AnimationTransaction.commit();
        }
    }
}
