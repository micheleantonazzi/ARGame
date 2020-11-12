package com.argame.arcore;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.viro.core.AsyncObject3DListener;
import com.viro.core.Object3D;
import com.viro.core.PinchState;
import com.viro.core.RotateState;
import com.viro.core.Vector;
import com.viro.core.ViroContext;
import com.viro.core.ViroView;

import java.io.IOException;
import java.io.InputStream;

abstract public class Component3D extends Object3D {

    private Context context;
    private ViroView viroView;
    Uri uri;
    private float rotateStart;
    private float scaleStart;
    private boolean editMode = false;

    private double defaultRotationX = 0;

    public Component3D(Context context, ViroView viroContext, Uri uri) {
        this.context = context;
        this.viroView = viroContext;
        this.uri = uri;
    }

    public Bitmap getBitmapFromAsset(String assetName) {
        AssetManager assetManager = this.context.getResources().getAssets();
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

    synchronized public void setEditMode(boolean editMode) {
        this.editMode = editMode;

        if (editMode) {
            this.setGesturePinchListener((i, node, scale, pinchState) -> {
                if (pinchState == PinchState.PINCH_START)
                    scaleStart = node.getScaleRealtime().x;
                else
                    node.setScale(new Vector(scaleStart * scale, scaleStart * scale, scaleStart * scale));
            });

            this.setGestureRotateListener((i, node, rotation, rotateState) -> {
                if (rotateState == RotateState.ROTATE_START)
                    rotateStart = node.getRotationEulerRealtime().y;
                float totalRotationY = rotateStart + rotation;
                node.setRotation(new Vector(this.defaultRotationX, totalRotationY, 0));
            });

            this.setDragListener((i, node, vector, vector1) -> {});
        }

        else {
            this.setGesturePinchListener(null);
            this.setGestureRotateListener(null);
            this.setDragListener(null);
        }
    }

    synchronized public boolean isEditModeEnabled() {
        return this.editMode;
    }

    abstract public void loadDefaultModel(AsyncObject3DListener listener);

    public void setDefaultRotationX(double defaultRotationX) {
        this.defaultRotationX = defaultRotationX;
    }

    public Context getContext() {
        return this.context;
    }

    public ViroView getViroView() {
        return this.viroView;
    }

    public Uri getUri() {
        return this.uri;
    }
}
