package com.argame.webrtc.parameters;

import android.util.Log;

class PeerConnectionParametersFactoryImpl implements PeerConnectionParametersFactory{

    // Parameters translations
    private static final String VIDEO_FLEXFEC_FIELDTRIAL =
            "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/";
    private static final String VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/";
    private static final String DISABLE_WEBRTC_AGC_FIELDTRIAL =
            "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/";

    private PeerConnectionParameters parameters;

    public PeerConnectionParametersFactoryImpl(PeerConnectionParameters parameters) {
        this.parameters = parameters;
    }


    @Override
    public String generateFieldTrials() {
        String fieldTrials = "";
        if (this.parameters.isVideoFlexfecEnable()) {
            fieldTrials += VIDEO_FLEXFEC_FIELDTRIAL;
            Log.d("debugg", "Enable FlexFEC field trial.");
        }
        fieldTrials += VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL;
        if (this.parameters.isWebRtcAGCAndHPFEnable()) {
            fieldTrials += DISABLE_WEBRTC_AGC_FIELDTRIAL;
            Log.d("debugg", "Disable WebRTC AGC field trial.");
        }
        return fieldTrials;
    }
}
