package com.softgames.popat.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

public class Routing {

    private Activity activity;
    private Bundle bundle;
    public Routing(Activity activity){

        this.activity = activity;
        this.bundle = new Bundle();
    }

    public <T> void navigate(Class<T> next, boolean shouldFinish){

        Intent i = new Intent(activity, next);
        i.putExtras(bundle);

        activity.startActivity(i);

        if(shouldFinish) {
            activity.finish();
        }
    }

    public <T> void navigateAndClear(Class<T> next){

        Intent i = new Intent(activity, next);
        i.putExtras(bundle);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        activity.startActivity(i);

        activity.finish();
    }

    public void clearParams(){
        this.bundle.clear();
    }
    public void appendParams(String key, Object object){

       if(object instanceof String){
           this.bundle.putString(key, object.toString().trim());
       }else if(object instanceof Boolean){
           this.bundle.putBoolean(key, (boolean) object);
       }else if(object instanceof Integer){
           this.bundle.putInt(key, (int) object);
       }else if(object instanceof Parcelable){
           this.bundle.putParcelable(key, (Parcelable) object);
       }else if(object instanceof Serializable){
           this.bundle.putSerializable(key, (Serializable) object);
       }else if(object instanceof Byte[]){
           this.bundle.putByteArray(key, (byte[]) object);
       }

    }
    public Object getParam(String key){
        Bundle getBundle = null;
        getBundle = activity.getIntent().getExtras();

        return getBundle.get(key);
    }
}
