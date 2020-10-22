package com.argame.webrtc;

import android.content.Context;
import android.util.Log;

import com.argame.webrtc.parameters.PeerConnectionParameters;

import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerConnectionClient {

    private Context appContext;
    private EglBase eglBase;
    private PeerConnectionParameters parameters;

    private PeerConnectionFactory factory;

    // Executor thread is started once in private ctor and is used for all
    // peer connection API calls to ensure new peer connection factory is
    // created on the same thread as the previously destroyed factory.
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PeerConnectionClient(Context appContext, EglBase eglBase, PeerConnectionParameters parameters) {
        this.appContext = appContext;
        this.eglBase = eglBase;
        this.parameters = parameters;

        // Initialize the factory in the single thread executor
        String fieldTrials = parameters.getFactory().generateFieldTrials();
        this.executor.execute(() -> {
            Log.d("debugg", "Initialize WebRTC. Field trials: " + fieldTrials);
            PeerConnectionFactory.initialize(
                    PeerConnectionFactory.InitializationOptions.builder(appContext)
                            .setFieldTrials(fieldTrials)
                            .setEnableInternalTracer(true)
                            .createInitializationOptions());
        });
    }
    
    private JavaAudioDeviceModule generateAudioDeviceModule() {
        // Set audio record error callbacks.
        JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback = new JavaAudioDeviceModule.AudioRecordErrorCallback() {
            @Override
            public void onWebRtcAudioRecordInitError(String errorMessage) {
                Log.e("debugg", "onWebRtcAudioRecordInitError: " + errorMessage);
            }

            @Override
            public void onWebRtcAudioRecordStartError(
                    JavaAudioDeviceModule.AudioRecordStartErrorCode errorCode, String errorMessage) {
                Log.e("debugg", "onWebRtcAudioRecordStartError: " + errorCode + ". " + errorMessage);
            }

            @Override
            public void onWebRtcAudioRecordError(String errorMessage) {
                Log.e("debugg", "onWebRtcAudioRecordError: " + errorMessage);
            }
        };

        JavaAudioDeviceModule.AudioTrackErrorCallback audioTrackErrorCallback = new JavaAudioDeviceModule.AudioTrackErrorCallback() {
            @Override
            public void onWebRtcAudioTrackInitError(String errorMessage) {
                Log.e("debugg", "onWebRtcAudioTrackInitError: " + errorMessage);
            }

            @Override
            public void onWebRtcAudioTrackStartError(
                    JavaAudioDeviceModule.AudioTrackStartErrorCode errorCode, String errorMessage) {
                Log.e("debugg", "onWebRtcAudioTrackStartError: " + errorCode + ". " + errorMessage);
            }

            @Override
            public void onWebRtcAudioTrackError(String errorMessage) {
                Log.e("debugg", "onWebRtcAudioTrackError: " + errorMessage);
            }
        };

        // Set audio record state callbacks.
        JavaAudioDeviceModule.AudioRecordStateCallback audioRecordStateCallback = new JavaAudioDeviceModule.AudioRecordStateCallback() {
            @Override
            public void onWebRtcAudioRecordStart() {
                Log.i("debugg", "Audio recording starts");
            }

            @Override
            public void onWebRtcAudioRecordStop() {
                Log.i("debugg", "Audio recording stops");
            }
        };

        // Set audio track state callbacks.
        JavaAudioDeviceModule.AudioTrackStateCallback audioTrackStateCallback = new JavaAudioDeviceModule.AudioTrackStateCallback() {
            @Override
            public void onWebRtcAudioTrackStart() {
                Log.i("debugg", "Audio playout starts");
            }

            @Override
            public void onWebRtcAudioTrackStop() {
                Log.i("debugg", "Audio playout stops");
            }
        };

        return JavaAudioDeviceModule.builder(appContext)
                .setUseHardwareAcousticEchoCanceler(parameters.isHardwareEchoCancellerEnable())
                .setUseHardwareNoiseSuppressor(parameters.isNoiseSuppressorEnable())
                .setAudioRecordErrorCallback(audioRecordErrorCallback)
                .setAudioTrackErrorCallback(audioTrackErrorCallback)
                .setAudioRecordStateCallback(audioRecordStateCallback)
                .setAudioTrackStateCallback(audioTrackStateCallback)
                .createAudioDeviceModule();
    }
    
    public PeerConnectionFactory createPeerConnectionFactory(PeerConnectionFactory.Options options) {

        // Create audio module
        JavaAudioDeviceModule audioDeviceModule = this.generateAudioDeviceModule();

        // Create video decoder and encoder
        VideoEncoderFactory encoderFactory;
        VideoDecoderFactory decoderFactory;
        if (parameters.isVideoCodecHWAccelerationEnable()) {
            encoderFactory = new DefaultVideoEncoderFactory(
                    eglBase.getEglBaseContext(), true, parameters.isH264HighProfileEnable());
            decoderFactory = new DefaultVideoDecoderFactory(eglBase.getEglBaseContext());
        } else {
            encoderFactory = new SoftwareVideoEncoderFactory();
            decoderFactory = new SoftwareVideoDecoderFactory();
        }


        this.factory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(audioDeviceModule)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();

        return factory;
    }
    
    // This method is called when a user calls another one
    public void createOffer() {
        
    }
}
