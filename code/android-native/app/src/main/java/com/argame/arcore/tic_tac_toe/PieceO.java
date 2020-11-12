package com.argame.arcore.tic_tac_toe;

import android.content.Context;
import android.net.Uri;

import com.argame.arcore.Component3D;
import com.viro.core.AsyncObject3DListener;
import com.viro.core.Object3D;
import com.viro.core.ViroContext;
import com.viro.core.ViroView;

public class PieceO extends Component3D {

    public PieceO(Context context, ViroView viroView) {
        super(context, viroView, Uri.parse("file:///android_asset/tictactoe/o/o.obj"));
    }

    @Override
    public void loadDefaultModel(AsyncObject3DListener listener) {
        this.loadModel(this.getViroView().getViroContext(), this.getUri() , Object3D.Type.OBJ, new AsyncObject3DListener() {
            @Override
            public void onObject3DLoaded(Object3D object3D, Type type) {
                listener.onObject3DLoaded(object3D, type);
            }

            @Override
            public void onObject3DFailed(String s) {
                listener.onObject3DFailed(s);
            }
        });
    }
}
