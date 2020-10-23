package com.argame.activities.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.argame.R;
import com.argame.rtc_engine.RTCEngineEventHandler;

import io.agora.rtc.RtcEngine;

public class TicTacToeActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 22;

    private boolean permission_camera = false;
    private boolean permission_microphone = false;

    private RtcEngine rtcEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set window styles for fullscreen-window size.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tic_tac_toe);

        // Request permissions (camera and microphone)
        String[] permissions = new String[]{"", ""};

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

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);

        // Initialize RTCEngine
        try {
            this.rtcEngine = RtcEngine.create(this.getBaseContext(), getString(R.string.agora_app_id), new RTCEngineEventHandler());
        } catch (Exception e) {
            Log.e("debugg", Log.getStackTraceString(e));
        }
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
}