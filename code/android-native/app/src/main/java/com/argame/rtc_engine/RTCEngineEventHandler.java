package com.argame.rtc_engine;

import android.util.Log;

import io.agora.rtc.IRtcEngineEventHandler;

public class RTCEngineEventHandler extends IRtcEngineEventHandler {

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
    }

    @Override
    // Listen for the onUserOffline callback.
    // This callback occurs when the remote user leaves the channel or drops offline.
    public void onUserOffline(final int uid, int reason) {
        Log.i("debugg","User offline, uid: " + (uid & 0xFFFFFFFFL));
    }
}
