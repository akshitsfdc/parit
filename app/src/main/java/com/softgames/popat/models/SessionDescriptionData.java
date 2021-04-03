package com.softgames.popat.models;

public class SessionDescriptionData {

    private Offer answer;
    private Offer offer;

    public SessionDescriptionData() {
    }

    public SessionDescriptionData(Offer answer, Offer offer) {
        this.answer = answer;
        this.offer = offer;
    }

    public Offer getAnswer() {
        return answer;
    }

    public void setAnswer(Offer answer) {
        this.answer = answer;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

}
