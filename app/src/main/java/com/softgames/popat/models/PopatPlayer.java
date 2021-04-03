package com.softgames.popat.models;

public class PopatPlayer {

    private String name;
    private String email;
    private String profilePic;
    private int coins;
    private int dummyPopats;
    private int level1Popats;
    private int level2Popats;
    private int games;
    private String userId;

    public PopatPlayer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getDummyPopats() {
        return dummyPopats;
    }

    public void setDummyPopats(int dummyPopats) {
        this.dummyPopats = dummyPopats;
    }

    public int getLevel1Popats() {
        return level1Popats;
    }

    public void setLevel1Popats(int level1Popats) {
        this.level1Popats = level1Popats;
    }

    public int getLevel2Popats() {
        return level2Popats;
    }

    public void setLevel2Popats(int level2Popats) {
        this.level2Popats = level2Popats;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }
}
