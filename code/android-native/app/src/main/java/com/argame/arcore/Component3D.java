package com.argame.arcore;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.viro.core.Object3D;
import com.viro.core.PinchState;
import com.viro.core.RotateState;
import com.viro.core.Vector;

import java.io.IOException;
import java.io.InputStream;

public class Component3D extends Object3D {

    private Context context;
    private float rotateStart;
    private float scaleStart;
    private boolean editMode = false;

    private double defaultRotationX = 0;

    public Component3D(Context context) {
        this.context = context;
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

    public void setDefaultRotationX(double defaultRotationX) {
        this.defaultRotationX = defaultRotationX;
    }
}
