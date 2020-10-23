package com.argame.activities.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.argame.R;
import com.argame.rtc_engine.RTCEngineEventHandler;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class TicTacToeActivity extends AppCompatActivity {

    private final IRtcEngineEventHandler rtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        // Listen for the onJoinChannelSuccess callback.
        // This callback occurs when the local user successfully joins the channel.
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            Log.i("debugg","Join channel success, uid: " + (uid & 0xFFFFFFFFL));
        }

        @Override
        // Listen for the onFirstRemoteVideoDecoded callback.
        // This callback occurs when the first video frame of a remote user is received and decoded after the remote user successfully joins the channel.
        // You can call the setupRemoteVideo method in this callback to set up the remote video view.
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            Log.i("debugg","First remote video decoded, uid: " + (uid & 0xFFFFFFFFL));
            setRemoteVideo(uid);
        }

        @Override
        // Listen for the onUserOffline callback.
        // This callback occurs when the remote user leaves the channel or drops offline.
        public void onUserOffline(final int uid, int reason) {
            Log.i("debugg","User offline, uid: " + (uid & 0xFFFFFFFFL));
        }
    };

    private static final int PERMISSION_REQUEST = 22;

    private boolean permission_camera = false;
    private boolean permission_microphone = false;

    private RtcEngine rtcEngine;

    // UI components
    SurfaceView remoteSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set window styles for fullscreen-window size.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        setContentView(R.layout.activity_tic_tac_toe);

        // Request permissions (camera and microphone)
        String[] permissions = new String[]{"", "", ""};

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            permissions[0] = Manifest.permission.CAMERA;
        else
            this.permission_camera = true;

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
            permissions[1] = Manifest.permission.RECORD_AUDIO;
        else
            this.permission_microphone = true;

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            permissions[2] = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);

        // Initialize RTCEngine
        try {
            this.rtcEngine = RtcEngine.create(this.getBaseContext(), getString(R.string.agora_app_id), rtcEventHandler);
        } catch (Exception e) {
            Log.e("debugg", Log.getStackTraceString(e));
        }

        // Enable RTC video and audio
        if(this.rtcEngine != null && this.permission_camera)
            this.rtcEngine.enableVideo();

        if(this.rtcEngine != null && this.permission_microphone)
            this.rtcEngine.enableAudio();

        // Display local video
        SurfaceView localSurfaceView = findViewById(R.id.surface_view_local_video);
        this.remoteSurfaceView = findViewById(R.id.surface_view_remote_video);
        localSurfaceView.setZOrderMediaOverlay(true);
        VideoCanvas localVideoCanvas = new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        this.rtcEngine.setupLocalVideo(localVideoCanvas);

        // Join in a channel
        this.rtcEngine.joinChannel("00629740b29ac4d480e9ff663b48521191bIABCm9EIr7cWzLwaib4EodAkiEKDX/UhoLCqQkod9apzQAOv7D8AAAAAEAAKvMYLVCiUXwEAAQBUKJRf", "channelprova", "Extra Optional Data", 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        rtcEngine.leaveChannel();
        RtcEngine.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    this.permission_camera = true;
                else if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    this.permission_microphone = true;
                return;
        }
    }

    private void setRemoteVideo(int userID) {
        this.rtcEngine.setupRemoteVideo(new VideoCanvas(this.remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, userID));
    }
}