package com.argame.arcore;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;

import com.viro.core.AsyncObject3DListener;
import com.viro.core.Material;
import com.viro.core.Node;
import com.viro.core.Object3D;
import com.viro.core.Quad;
import com.viro.core.Vector;
import com.viro.core.ViroContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Playground extends Component3D {

    public static final Vector INITIAL_SCALE = new Vector(0.03f, 0.03f, 0.03f);

    private List<Node> planesClickable = new ArrayList<>(9);

    public Playground(Context context, ViroContext viroContext, AsyncObject3DListener listener) {
        super(context);

        this.loadModel(viroContext, Uri.parse("file:///android_asset/tictactoe/playground/playground.obj"), Object3D.Type.OBJ, new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(Object3D object3D, Type type) {
                listener.onObject3DLoaded(object3D, type);
            }

            @Override
            public void onObject3DFailed(String s) {
                listener.onObject3DFailed(s);
            }
        });
                this.createClickablePlanes();

        this.setDragType(DragType.FIXED_TO_WORLD);

        this.setDefaultRotationX(-Math.toRadians(90.0f));
        this.setScale(INITIAL_SCALE);
        this.setRotation(new Vector(-Math.toRadians(90.0), 0, 0));
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
    }
}
