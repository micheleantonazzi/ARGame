package com.argame.webrtc.parameters;

import android.util.Log;

import org.webrtc.MediaConstraints;

class PeerConnectionParametersFactoryImpl implements PeerConnectionParametersFactory{

    // Parameters translations
    private static final String VIDEO_FLEXFEC_FIELDTRIAL =
            "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/";
    private static final String VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/";
    private static final String DISABLE_WEBRTC_AGC_FIELDTRIAL =
            "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/";

    // AUDIO CONSTRAINTS
    private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";

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

    @Override
    public MediaConstraints generateAudioConstraint() {
        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(
                        AUDIO_ECHO_CANCELLATION_CONSTRAINT,
                        parameters.isAudioEchoCancellationConstraintEnable() ? "true" : "false")
        );

        mediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(
                        AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT,
                        parameters.isAudioGainControlConstraintEnable() ? "true" : "false")
        );

        mediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(
                        AUDIO_HIGH_PASS_FILTER_CONSTRAINT,
                        parameters.isAudioHighPassFilterConstraintEnable() ? "true" : "false")
        );

        mediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair(
                        AUDIO_NOISE_SUPPRESSION_CONSTRAINT,
                        parameters.isAudioNoiseSuppressionConstraint() ? "true" : "false")
        );

        return mediaConstraints;
    }
}
