package com.softgames.popat.models;

import androidx.annotation.NonNull;

public class PopatMove {

    private int selectedBox;
    private int round;
    private boolean move;
    private boolean start;
    private int openedBox;

    public PopatMove() {
    }

    public int getSelectedBox() {
        return selectedBox;
    }

    public void setSelectedBox(int selectedBox) {
        this.selectedBox = selectedBox;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public boolean isMove() {
        return move;
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    @NonNull
    @Override
    public String toString() {
        return " selectedBox : "+this.selectedBox+" round : "+this.round+" move : "+this.move;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public int getOpenedBox() {
        return openedBox;
    }

    public void setOpenedBox(int openedBox) {
        this.openedBox = openedBox;
    }
}
