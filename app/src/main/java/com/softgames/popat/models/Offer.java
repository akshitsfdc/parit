package com.softgames.popat.models;

import org.webrtc.SessionDescription;

public class Offer {

    private String sdp;
    private SessionDescription.Type type;

    public Offer() {
    }

    public Offer(String sdp, SessionDescription.Type type) {
        this.sdp = sdp;
        this.type = type;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public SessionDescription.Type getType() {
        return type;
    }

    public void setType(SessionDescription.Type type) {
        this.type = type;
    }
}
