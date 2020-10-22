package com.argame.webrtc.parameters;

public class PeerConnectionParameters {

    private boolean videoFlexfecEnable = false;
    private boolean webRtcAGCAndHPFEnable = true;

    // VIDEO
    private boolean videoCodecHWAccelerationEnable = true;
    private boolean H264HighProfileEnable = false;

    //AUDIO
    private boolean hardwareEchoCancellerEnable = true;
    private boolean noiseSuppressorEnable = true;

    public PeerConnectionParametersFactory getFactory() {
        return new PeerConnectionParametersFactoryImpl(this);   
    }
    
    public boolean isVideoFlexfecEnable() {
        return videoFlexfecEnable;
    }

    public void setVideoFlexfecEnable(boolean videoFlexfecEnable) {
        this.videoFlexfecEnable = videoFlexfecEnable;
    }


    public boolean isWebRtcAGCAndHPFEnable() {
        return webRtcAGCAndHPFEnable;
    }

    public void setWebRtcAGCAndHPFEnable(boolean webRtcAGCAndHPFEnable) {
        this.webRtcAGCAndHPFEnable = webRtcAGCAndHPFEnable;
    }

    public boolean isHardwareEchoCancellerEnable() {
        return hardwareEchoCancellerEnable;
    }

    public void setHardwareEchoCancellerEnable(boolean hardwareEchoCancellerEnable) {
        this.hardwareEchoCancellerEnable = hardwareEchoCancellerEnable;
    }

    public boolean isNoiseSuppressorEnable() {
        return noiseSuppressorEnable;
    }

    public void setNoiseSuppressorEnable(boolean noiseSuppressorEnable) {
        this.noiseSuppressorEnable = noiseSuppressorEnable;
    }

    public boolean isVideoCodecHWAccelerationEnable() {
        return videoCodecHWAccelerationEnable;
    }

    public void setVideoCodecHWAccelerationEnable(boolean videoCodecHWAccelerationEnable) {
        this.videoCodecHWAccelerationEnable = videoCodecHWAccelerationEnable;
    }

    public boolean isH264HighProfileEnable() {
        return H264HighProfileEnable;
    }

    public void setH264HighProfileEnable(boolean h264HighProfileEnable) {
        H264HighProfileEnable = h264HighProfileEnable;
    }
}
