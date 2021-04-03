package com.softgames.popat.models;

import org.webrtc.PeerConnection;

public class IceCandidateServer {

    private PeerConnection.AdapterType adapterType;
    private String sdp;
    private int sdpMLineIndex;
    private String sdpMid;
    private String serverUrl;

    public IceCandidateServer() {
    }

    public IceCandidateServer(PeerConnection.AdapterType adapterType, String sdp, int sdpMLineIndex, String sdpMid, String serverUrl) {
        this.adapterType = adapterType;
        this.sdp = sdp;
        this.sdpMLineIndex = sdpMLineIndex;
        this.sdpMid = sdpMid;
        this.serverUrl = serverUrl;
    }

    public PeerConnection.AdapterType getAdapterType() {
        return adapterType;
    }

    public void setAdapterType(PeerConnection.AdapterType adapterType) {
        this.adapterType = adapterType;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public int getSdpMLineIndex() {
        return sdpMLineIndex;
    }

    public void setSdpMLineIndex(int sdpMLineIndex) {
        this.sdpMLineIndex = sdpMLineIndex;
    }

    public String getSdpMid() {
        return sdpMid;
    }

    public void setSdpMid(String sdpMid) {
        this.sdpMid = sdpMid;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

}
