package com.argame.webrtc.parameters;

import org.webrtc.MediaConstraints;

public interface PeerConnectionParametersFactory {

    String generateFieldTrials();

    MediaConstraints generateAudioConstraint();
}
