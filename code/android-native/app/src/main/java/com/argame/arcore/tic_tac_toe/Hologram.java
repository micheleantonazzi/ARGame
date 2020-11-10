package com.argame.arcore.tic_tac_toe;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.argame.arcore.Component3D;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.Object3D;
import com.viro.core.Texture;
import com.viro.core.Vector;
import com.viro.core.ViroContext;

public class Hologram extends Component3D {

    static public final Vector INITIAL_SCALE = new Vector(0.01f, 0.01f, 0.01f);

    public Hologram(Context context, ViroContext viroContext) {
        super(context, viroContext, Uri.parse("file:///android_asset/hologram/hologram_base.obj"));

        this.setScale(INITIAL_SCALE);
    }

    @Override
    public void loadDefaultModel(AsyncObject3DListener listener) {
        final Bitmap bitmapParticle = getBitmapFromAsset("hologram/particle_texture.png");
        final Bitmap bitmapBase = getBitmapFromAsset("hologram/base_texture.jpg");
        this.loadModel(super.getViroContext(), this.getUri(), Object3D.Type.OBJ, new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(final Object3D object, final Object3D.Type type) {

                // Show the playground when the model has been loaded
                Texture particleTexture = new Texture(bitmapParticle, Texture.Format.RGBA8, false, false);
                Texture baseTexture = new Texture(bitmapBase, Texture.Format.RGBA8, false, false);
                getMaterials().get(0).setDiffuseTexture(particleTexture);
                getMaterials().get(1).setDiffuseTexture(baseTexture);

                listener.onObject3DLoaded(object, type);
            }

            @Override
            public void onObject3DFailed(String s) {
                listener.onObject3DFailed(s);
            }
        });
    }
}
