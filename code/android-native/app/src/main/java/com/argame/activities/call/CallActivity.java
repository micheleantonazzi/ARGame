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

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

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
        PeerConnectionParameters peerConnectionParameters = new PeerConnectionParameters();
        PeerConnectionClient peerConnectionClient = new PeerConnectionClient(
                getApplicationContext(), eglBase, peerConnectionParameters);

        // Initialize peer connection factory
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        PeerConnectionFactory factory = peerConnectionClient.createPeerConnectionFactory(options);

        // Create video capturer
        CameraEnumerator cameraEnumerator = new Camera1Enumerator(false);
        VideoCapturer videoCapturer = cameraEnumerator.createCapturer(cameraEnumerator.getDeviceNames()[0], null);;

        final String[] deviceNames = cameraEnumerator.getDeviceNames();

        // Trying to find a front facing camera!
        for (String deviceName : deviceNames) {
            if (cameraEnumerator.isFrontFacing(deviceName)) {
                videoCapturer = cameraEnumerator.createCapturer(deviceName, null);
            }
        }


        // Create a VideoSource instance
        SurfaceTextureHelper surfaceTextureHelper =
                SurfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());
        VideoSource videoSource = factory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, getApplicationContext(), videoSource.getCapturerObserver());

        VideoTrack localVideoTrack = factory.createVideoTrack("100", videoSource);

        // Create AudioSource
        AudioSource audioSource = factory.createAudioSource(peerConnectionParameters.getFactory().generateAudioConstraint());
        AudioTrack localAudioTrack = factory.createAudioTrack("101", audioSource);

        videoCapturer.startCapture(1000, 1000, 30);

        localVideoTrack.setEnabled(true);
        VideoSinkProxy videoSinkProxyLocalScreen = new VideoSinkProxy();
        videoSinkProxyLocalScreen.setTarget(rendererFullScreen);
        localVideoTrack.addSink(videoSinkProxyLocalScreen);
    }
}