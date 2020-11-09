package com.argame.arcore;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.viro.core.AsyncObject3DListener;
import com.viro.core.Object3D;
import com.viro.core.Texture;
import com.viro.core.Vector;
import com.viro.core.ViroContext;

public class Hologram extends Component3D {

    static public final Vector INITIAL_SCALE = new Vector(0.01f, 0.01f, 0.01f);

    private Context context;
    private Boolean editMode = false;

    public Hologram(Context context, ViroContext viroContext, AsyncObject3DListener listener) {
        super(context);

        final Bitmap bitmapParticle = getBitmapFromAsset("hologram/particle_texture.png");
        final Bitmap bitmapBase = getBitmapFromAsset("hologram/base_texture.jpg");
        this.loadModel(viroContext, Uri.parse("file:///android_asset/hologram/hologram_base.obj"), Object3D.Type.OBJ, new AsyncObject3DListener() {
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

        this.setScale(INITIAL_SCALE);
    }


}
