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
import com.softgames.popat.models.CallerOffer;
import com.softgames.popat.models.IceCandidateServer;
import com.softgames.popat.models.Offer;
import com.softgames.popat.models.SessionDescriptionData;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SessionDescription;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SenderService {

    private PeerConnection peerConnection;
    private DataChannel localDataChannel;
    private String roomId;

    private Activity context;
    private FireStoreService fireStoreService;
    private FireAuthService fireAuthService;
    //    private User callee;
    private PeerConnectionFactory pcFactory;
    //***
    private MediaStream localStream;
    private MediaStream remoteStream;

    private AudioTrack localAudioTrack;
    private AudioTrack remoteAudioTrack;

    public SenderService(Activity context, String roomId) {
        this.roomId = roomId;
        this.context = context;
        this.fireStoreService = new FireStoreService();
        this.fireAuthService = new FireAuthService(context);
        this.initialize();

    }


    private void initialize() {

        PeerConnectionFactory.InitializationOptions initializationOptions = PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions();
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

        peerConnection = pcFactory.createPeerConnection(rtcConfig,
                new CustomPeerConnectionObserver() {

                    @Override
                    public void onIceCandidate(IceCandidate iceCandidate) {
                        super.onIceCandidate(iceCandidate);

                        final String iceCollectionName;
                        IceCandidateServer iceCandidateServer = getIceCandidateServer(iceCandidate);

                        iceCollectionName = "callerCandidates";

                        fireStoreService.getDocReference(context.getString(R.string.rooms), roomId)
                                .collection(iceCollectionName).document().set(iceCandidateServer)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("UI89", "onSuccess:  >> caller candidates pushed to server >> "+iceCollectionName);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("UI89", "onFailure:  >> caller candidates failed to pushed to server >>"+iceCollectionName);

                                    }
                                });

                    }

                    @Override
                    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                        super.onAddTrack(rtpReceiver, mediaStreams);


                    }

                    @Override
                    public void onAddStream(MediaStream mediaStream) {
                        super.onAddStream(mediaStream);

                        if(mediaStream.audioTracks.size() > 0) {

                            remoteAudioTrack = mediaStream.audioTracks.get(0);
                            remoteStream.addTrack(remoteAudioTrack);

                        }
                    }


                    @Override
                    public void onDataChannel(DataChannel dataChannel) {
                        super.onDataChannel(dataChannel);

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

        DataChannel.Init dcInit = new DataChannel.Init();

        dcInit.ordered = false;
        dcInit.negotiated = false;

        if (peerConnection != null) {
            localDataChannel = peerConnection.createDataChannel("1", dcInit);
            localDataChannel.registerObserver((MultiPlayerGameLobbyActivity)context);
//**********
            peerConnection.addStream(createStream());

//**********

        }
    }

    //**********
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
    //**********
    public void createOffer(){

        peerConnection.createOffer(new CustomSdpObserver() {
            //((SendActivity)context).startSignalling();
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);

                Offer offer = new Offer(sessionDescription.description, sessionDescription.type);
                callerSignalling(offer);

                Log.d("OFFER34", "BEFORE>onCreateSuccess: "+offer);

                Log.d("OFFER34", "AFTER>onCreateSuccess: "+offer);

                peerConnection.setLocalDescription(new CustomSdpObserver(), sessionDescription);
                // sessionDescription.description is string which needs to the shared across network
            }

            @Override
            public void onCreateFailure(String s) {

                super.onCreateFailure(s);
                // Offer creation failed
            }
        }, new MediaConstraints());
    }

    private void callerSignalling(Offer offer){

        fireStoreService.setData(context.getString(R.string.rooms), roomId, new CallerOffer(offer))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        fireStoreService.getDocReference(context.getString(R.string.rooms), roomId)
                                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                        @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            return;
                                        }

                                        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                                                ? "Local" : "Server";

                                        if (snapshot != null && snapshot.exists()) {

                                            SessionDescriptionData sessionDescriptionData = snapshot.toObject(SessionDescriptionData.class);

                                            if (sessionDescriptionData != null && sessionDescriptionData.getAnswer() != null) {

                                                SessionDescription sessionDescription = new SessionDescription(sessionDescriptionData.getAnswer().getType(), sessionDescriptionData.getAnswer().getSdp());
                                                peerConnection.setRemoteDescription(new CustomSdpObserver(), sessionDescription);

                                            }
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

//        fireStoreService.getDocReference("rooms", roomId).collection("calleeCandidates")
//                .document(callee.getUserID())
//                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                        if (value != null && value.exists()) {
//                            IceCandidateServer iceCandidateServer = value.toObject(IceCandidateServer.class);
//                            if (iceCandidateServer != null){
//                                peerConnection.addIceCandidate(getIceCandidate(iceCandidateServer));
//
//                            }
//                        }
//                    }
//
//                });

        fireStoreService.getDocReference(context.getString(R.string.rooms), roomId).collection(context.getString(R.string.calleeCandidates))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        for (DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:

                                    IceCandidateServer iceCandidateServer = dc.getDocument().toObject(IceCandidateServer.class);
                                    peerConnection.addIceCandidate(getIceCandidate(iceCandidateServer));

                                    break;
                                case MODIFIED:
                                    Log.d("TAG2", "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d("TAG2", "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }
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
