package com.softgames.popat.service;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.softgames.popat.R;
import com.softgames.popat.activities.MultiPlayerGameLobbyActivity;
import com.softgames.popat.helper.CustomPeerConnectionObserver;
import com.softgames.popat.helper.CustomSdpObserver;
import com.softgames.popat.models.IceCandidateServer;
import com.softgames.popat.models.Offer;
import com.softgames.popat.models.SessionDescriptionData;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.Loggable;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiverService {


    private Activity context;
    private PeerConnection peerConnection;
    private DataChannel localDataChannel;

    private FireStoreService fireStoreService;
    private String TAG = "ReceiverService";

    private String roomId;

    private FireAuthService fireAuthService;

    private PeerConnectionFactory pcFactory;

    private MediaStream localStream;
    private MediaStream remoteStream;

    private AudioTrack localAudioTrack;
    private AudioTrack remoteAudioTrack;

    public ReceiverService(Activity context, String roomId) {
        fireStoreService = new FireStoreService();
        this.roomId = roomId;
        this.context = context;
        this.fireAuthService = new FireAuthService(context);
        this.initialize();
    }

    private void initialize() {

// .setEnableInternalTracer(true).setInjectableLogger(new Loggable() {
//            @Override
//            public void onLogMessage(String s, Logging.Severity severity, String s1) {
//                Log.d(TAG, "onLogMessage: "+s);
//            }
//        },Logging.Severity.LS_ERROR)
       // PeerConnectionFactory.initializeInternalTracer();

        PeerConnectionFactory.InitializationOptions initializationOptions = PeerConnectionFactory.InitializationOptions.builder(context)
               .createInitializationOptions();

        PeerConnectionFactory.initialize(initializationOptions);

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        pcFactory = PeerConnectionFactory.builder().setOptions(options).createPeerConnectionFactory();

        List<PeerConnection.IceServer> iceServers = new ArrayList<>();

        localStream = pcFactory.createLocalMediaStream("stream97");

        remoteStream = pcFactory.createLocalMediaStream("stream98");


        iceServers.add(PeerConnection.IceServer.builder(context.getString(R.string.stun_google_server)).createIceServer());

        iceServers.add(PeerConnection.IceServer.builder(context.getString(R.string.stun_server)).setUsername(context.getString(R.string.stun_username)).setPassword(context.getString(R.string.stun_password)).createIceServer());
        iceServers.add(PeerConnection.IceServer.builder(context.getString(R.string.turn_server)).setUsername(context.getString(R.string.turn_username)).setPassword(context.getString(R.string.turn_password)).createIceServer());
        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);

        rtcConfig.enableRtpDataChannel = false;
        rtcConfig.enableDtlsSrtp = true;

        peerConnection = pcFactory.createPeerConnection(rtcConfig, new CustomPeerConnectionObserver() {


            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);

                String iceCollectionName;
                IceCandidateServer iceCandidateServer = getIceCandidateServer(iceCandidate);

                iceCollectionName = "calleeCandidates";

                fireStoreService.getDocReference(context.getString(R.string.rooms), roomId)
                        .collection(iceCollectionName).document().set(iceCandidateServer)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Log.d("UI89", "onSuccess:  >> callee candidates pushed to server >> "+iceCollectionName);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d("UI89", "onFailure:  >> callee candidates failed to pushed to server >>"+iceCollectionName);
                            }
                        });

            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                super.onDataChannel(dataChannel);

                Log.d("MSG01", "onDataChannel:>> callee "+dataChannel);
                localDataChannel = dataChannel;
                localDataChannel.registerObserver((MultiPlayerGameLobbyActivity)context);
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                Log.d("HHJL", "onAddStream: "+mediaStream.toString());

                if(mediaStream.audioTracks.size() > 0) {
                    remoteAudioTrack = mediaStream.audioTracks.get(0);
                    remoteStream.addTrack(remoteAudioTrack);

                }
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                super.onIceConnectionChange(iceConnectionState);
                if(iceConnectionState != null){
                    if(iceConnectionState == PeerConnection.IceConnectionState.CONNECTED){

                    }
                    if(iceConnectionState == PeerConnection.IceConnectionState.CLOSED){

                    }
                    if(iceConnectionState == PeerConnection.IceConnectionState.FAILED){

                    }
                }
            }
        });
        if (peerConnection != null) {
            peerConnection.addStream(createStream());

        }
    }
//******

    private MediaStream createStream() {
//        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
//        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);

        if (WebRtcAudioUtils.isNoiseSuppressorSupported()){
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);
        }
        if (WebRtcAudioUtils.isAcousticEchoCancelerSupported()){
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
        }

        MediaConstraints audioConstraints = new MediaConstraints();
//        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "true"));
//        audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));

        AudioSource audioSource = pcFactory.createAudioSource(audioConstraints);
        localAudioTrack = pcFactory.createAudioTrack("ARDAMSa0", audioSource);
        localStream.addTrack(localAudioTrack);

        return localStream;
    }

    //*****
    public void createAnswer(){

        peerConnection.createAnswer(new CustomSdpObserver(){

            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);

                Log.d(TAG, "onCreateSuccess: "+sessionDescription);

                SessionDescription sessionDescription1 = new SessionDescription(SessionDescription.Type.ANSWER, sessionDescription.description);
                Offer answer = new Offer(sessionDescription1.description, sessionDescription1.type);

                peerConnection.setLocalDescription(new CustomSdpObserver(), sessionDescription1);

                try{

                    calleeSignalling(answer);

                }catch (Exception e){
                    e.printStackTrace();
                }

                // sessionDescription.description is string which needs to the shared across network
            }

            @Override
            public void onCreateFailure(String s) {
                super.onCreateFailure(s);
                Log.d(TAG, "onCreateFailure: "+s);
            }
        },new MediaConstraints());
    }

    private void calleeSignalling(Offer answer){

        Map<String, Object> map = new HashMap<>();
        map.put("answer", answer);

        fireStoreService.updateData(context.getString(R.string.rooms), roomId, map)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "calleeSignalling: >> callee candidate added !");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "calleeSignalling: >> callee candidate failed !");
                });

//        fireStoreService.getDocReference("rooms", roomId).collection("callerCandidates")
//                .document(roomId)
//                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                            if (value != null && value.exists()) {
//                                IceCandidateServer iceCandidateServer = value.toObject(IceCandidateServer.class);
//                                if (iceCandidateServer != null){
//                                    peerConnection.addIceCandidate(getIceCandidate(iceCandidateServer));
//                                }
//                            }
//                        }
//
//                });

        fireStoreService.getDocReference(context.getString(R.string.rooms), roomId).collection("callerCandidates")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (value != null) {

                            for (DocumentChange dc : value.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        IceCandidateServer iceCandidateServer = dc.getDocument().toObject(IceCandidateServer.class);
                                        peerConnection.addIceCandidate(getIceCandidate(iceCandidateServer));

                                        break;
                                    case MODIFIED:
                                        Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                        break;
                                }
                            }
                        }
                    }
                });

    }

    public void setOffer(){
        fireStoreService.getData(context.getString(R.string.rooms), roomId)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        SessionDescriptionData sessionDescriptionData = documentSnapshot.toObject(SessionDescriptionData.class);
                        if (sessionDescriptionData != null && sessionDescriptionData.getOffer() != null) {
                            SessionDescription sessionDescription = new SessionDescription(sessionDescriptionData.getOffer().getType(), sessionDescriptionData.getOffer().getSdp());
                            peerConnection.setRemoteDescription(new CustomSdpObserver(), sessionDescription);
                            createAnswer();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: getting offer failed!");
                    }
                });
    }

    private IceCandidateServer getIceCandidateServer(IceCandidate iceCandidate){
        return  new IceCandidateServer(iceCandidate.adapterType, iceCandidate.sdp, iceCandidate.sdpMLineIndex, iceCandidate.sdpMid, iceCandidate.serverUrl);
    }
    private IceCandidate getIceCandidate(IceCandidateServer iceCandidateServer){
        return  new IceCandidate(iceCandidateServer.getSdpMid(), iceCandidateServer.getSdpMLineIndex(), iceCandidateServer.getSdp());
    }

    public DataChannel getDataChannel(){
        return localDataChannel;
    }

    public PeerConnection getPeerConnection(){
        return peerConnection;
    }

    public void setPeerConnection(PeerConnection peerConnection) {
        this.peerConnection = peerConnection;
    }

    public void close(){
        try{
            if(peerConnection != null){
                peerConnection.close();
            }
            if(localDataChannel != null){
                localDataChannel.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public AudioTrack getLocalAudioTrack(){
        return localAudioTrack;
    }

    public AudioTrack getRemoteAudioTrack(){
        return remoteAudioTrack;
    }

}
