package com.argame.activities.call;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.argame.R;
import com.argame.utilities.UnhandledExceptionHandler;
import com.argame.webrtc.PeerConnectionClient;
import com.argame.webrtc.VideoSinkProxy;
import com.argame.webrtc.parameters.PeerConnectionParameters;

import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

public class CallActivity extends AppCompatActivity {

    private SurfaceViewRenderer rendererFullScreen;
    private SurfaceViewRenderer rendererSmall;

    private VideoSinkProxy remoteVideoSink = new VideoSinkProxy();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set thread handler exception
        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));

        // Set window styles for fullscreen-window size.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getSupportActionBar().hide();

        // Set content view
        setContentView(R.layout.activity_call);

        this.rendererFullScreen = findViewById(R.id.renderer_fullscreen);
        this.rendererSmall = findViewById(R.id.renderer_small);

        final EglBase eglBase = EglBase.create();

        // Init video renderers components
        rendererFullScreen.init(eglBase.getEglBaseContext(), null);
        rendererFullScreen.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        rendererSmall.init(eglBase.getEglBaseContext(), null);
        rendererSmall.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);

        // Initialize PeerConnectionClient
        PeerConnectionClient peerConnectionClient = new PeerConnectionClient(
                getApplicationContext(), eglBase, new PeerConnectionParameters());

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        peerConnectionClient.createPeerConnectionFactory(options);






    }
}