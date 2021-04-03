package com.softgames.popat.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.softgames.popat.R;


public class MessageFragment extends Fragment {


    private Activity activity;
    private TextView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_message, container, false);

       message = view.findViewById(R.id.message);

       return view;
    }

    public void setMessage(String msg){
        //this.message.setText(msg);
    }
}