package com.softgames.popat.models;

public class GameRequest {

    private PopatPlayer host;
    private PopatPlayer client;
    private int popatLevel;
    private boolean open;
    private boolean hostRecognized;
    private String roomId;
    private long createdTime;


    public GameRequest() {
    }

    public GameRequest(PopatPlayer host, int popatLevel, boolean open, boolean hostRecognized, String roomId, long createdTime) {
        this.host = host;
        this.popatLevel = popatLevel;
        this.open = open;
        this.hostRecognized = hostRecognized;
        this.roomId = roomId;
        this.createdTime = createdTime;
    }

    public PopatPlayer getHost() {
        return host;
    }

    public void setHost(PopatPlayer host) {
        this.host = host;
    }

    public int getPopatLevel() {
        return popatLevel;
    }

    public void setPopatLevel(int popatLevel) {
        this.popatLevel = popatLevel;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isHostRecognized() {
        return hostRecognized;
    }

    public void setHostRecognized(boolean hostRecognized) {
        this.hostRecognized = hostRecognized;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public PopatPlayer getClient() {
        return client;
    }

    public void setClient(PopatPlayer client) {
        this.client = client;
    }
}
