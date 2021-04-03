package com.softgames.popat.broadCasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

public class EarphoneReceiver extends BroadcastReceiver {
    private AudioManager audioManager;

    public EarphoneReceiver(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch(state) {
                case(0):
                    //earphones notConnected
                    audioManager.setSpeakerphoneOn(true);

                    Log.d("HHGD", "onReceive: "+audioManager.isSpeakerphoneOn());

                    break;
                case(1):
                    //earphones connected
                    audioManager.setSpeakerphoneOn(false);
                    Log.d("HHGD", "onReceive: "+1);

                    break;
                default:
            }
        }
    }
}
