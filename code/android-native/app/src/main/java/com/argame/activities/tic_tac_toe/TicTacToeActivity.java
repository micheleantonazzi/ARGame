package com.argame.activities.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.argame.R;
import com.argame.activities.tic_tac_toe.fragments.TicTacToeFragmentGame;
import com.argame.activities.tic_tac_toe.fragments.TicTacToeFragmentWaitOpponent;
import com.argame.model.remote_structures.TicTacToeGameController;
import com.argame.model.data_structures.tic_tac_toe_game.ITicTacToeGame;
import com.argame.model.data_structures.tic_tac_toe_game.TicTacToeGame;
//import com.viro.core.ViroView;

public class TicTacToeActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 22;

    private boolean permissionCamera = false;
    private boolean permissionMicrophone = false;
    private ITicTacToeGame ticTacToeGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set window styles for fullscreen-window size
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        if(getSupportActionBar() != null)
            getSupportActionBar().hide();

        this.ticTacToeGame = TicTacToeGameController.getInstance().getCurrentTicTacToeGame();

        setContentView(R.layout.tic_tac_toe_activity_layout);

        this.requestPermissions();

        if (savedInstanceState == null) {
            if (this.ticTacToeGame.isOwner() && this.ticTacToeGame.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_NOT_ANSWERED) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, TicTacToeFragmentWaitOpponent.newInstance())
                        .commit();
                this.ticTacToeGame.addOnUpdateAcceptedStatusListener(gameAcceptedStatusChanged -> {
                    if (gameAcceptedStatusChanged.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_REFUSED)
                        finish();
                    else if (this.ticTacToeGame.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_ACCEPTED){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, TicTacToeFragmentGame.newInstance(this.permissionCamera, this.permissionMicrophone))
                                .commit();
                    }
                });
            }
            else if (this.ticTacToeGame.isOwner() && this.ticTacToeGame.getAcceptedStatus() == TicTacToeGame.ACCEPT_STATUS_ACCEPTED)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, TicTacToeFragmentGame.newInstance(this.permissionCamera, this.permissionMicrophone))
                        .commit();
            else
                getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, TicTacToeFragmentGame.newInstance(this.permissionCamera, this.permissionMicrophone))
                .commit();
        }
    }

    private void requestPermissions() {
        // Request permissions (camera and microphone)
        String[] permissions = new String[]{"", "", ""};

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            permissions[0] = Manifest.permission.CAMERA;
        else
            this.permissionCamera = true;

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED)
            permissions[1] = Manifest.permission.RECORD_AUDIO;
        else
            this.permissionMicrophone = true;

        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            permissions[2] = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    this.permissionCamera = true;
                else if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    this.permissionMicrophone = true;
                return;
        }
    }

    @Override
    public void onBackPressed() {
        if (TicTacToeGameController.getInstance().getCurrentTicTacToeGame().isTerminated())
            super.onBackPressed();
        else
            Toast.makeText(this, R.string.on_back_pressed_finish_game, Toast.LENGTH_LONG).show();
    }
}