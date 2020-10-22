package com.argame.webrtc;

import org.webrtc.Logging;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

public class VideoSinkProxy implements VideoSink {

    private VideoSink target;

    synchronized public void setTarget(VideoSink target) {
        this.target = target;
    }

    @Override
    synchronized public void onFrame(VideoFrame videoFrame) {
        if (target == null) {
            Logging.d("debugg", "VideoSinkProxy: dropping frame because VideoSink target is null.");
            return;
        }
        target.onFrame(videoFrame);
    }
}
