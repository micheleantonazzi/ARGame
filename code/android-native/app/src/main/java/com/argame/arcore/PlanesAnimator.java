package com.argame.arcore;

import android.graphics.Color;

import com.viro.core.Animation;
import com.viro.core.AnimationTimingFunction;
import com.viro.core.AnimationTransaction;
import com.viro.core.Node;

import java.util.List;

public class PlanesAnimator {

    private List<Node> planes;
    private AnimationTransaction animationIncrease;
    private AnimationTransaction animationDecrease;
    private boolean animationIsIncreasing = true;

    public PlanesAnimator(List<Node> planes) {
        this.planes = planes;
    }

    public void startAnimation() {
        this.startIncreaseAnimation();
    }

    synchronized public void stopAnimation() {
        if (this.animationIsIncreasing)
            this.animationIncrease.terminate();
        else
            this.animationDecrease.terminate();
        this.resetColors();
    }

    private void resetColors() {
        for(Node plane: planes)
            plane.getGeometry().getMaterials().get(0).setDiffuseColor(Color.parseColor("#00C6AA56"));
    }

    synchronized private void startIncreaseAnimation() {
        this.animationIsIncreasing = true;
        AnimationTransaction.begin();
        AnimationTransaction.setAnimationDuration(1000);
        AnimationTransaction.setTimingFunction(AnimationTimingFunction.Linear);
        for(Node plane: this.planes)
            plane.getGeometry().getMaterials().get(0).setDiffuseColor(Color.parseColor("#99C6AA56"));

        AnimationTransaction.setListener(animationTransaction -> startDecreaseAnimation());

        this.animationIncrease = AnimationTransaction.commit();
    }

    synchronized private void startDecreaseAnimation() {
        this.animationIsIncreasing = false;
        AnimationTransaction.begin();
        AnimationTransaction.setAnimationDuration(1000);
        AnimationTransaction.setTimingFunction(AnimationTimingFunction.Linear);
        for(Node plane: this.planes)
            plane.getGeometry().getMaterials().get(0).setDiffuseColor(Color.parseColor("#10C6AA56"));

        AnimationTransaction.setListener(animationTransaction -> startIncreaseAnimation());
        this.animationDecrease = AnimationTransaction.commit();
    }
}
