package com.softgames.popat.models;

public class PopatMoveResponse {

    private int openedBox;
    private int round;
    private boolean result;
    private boolean move;

    public PopatMoveResponse() {
    }



    public int getOpenedBox() {
        return openedBox;
    }

    public void setOpenedBox(int openedBox) {
        this.openedBox = openedBox;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean isMove() {
        return move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }
}
