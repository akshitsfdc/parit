package com.softgames.popat.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.softgames.popat.R;
import com.softgames.popat.service.FireAuthService;

public class ForgetPasswordFragment extends Fragment {

    private Button okButton, sendMailButton;
    private TextView message;
    private EditText fieldEmail;
    private LinearLayout msgLayout, editLayout;
    private FireAuthService fireAuthService;
    private View parent;
//    private Activity activity;
//
//    public ForgetPasswordFragment(Activity activity){
//        this.activity = activity;
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        fireAuthService = new FireAuthService();

        message = view.findViewById(R.id.message);
        fieldEmail = view.findViewById(R.id.fieldEmail);
        okButton = view.findViewById(R.id.okButton);
        sendMailButton = view.findViewById(R.id.sendMailButton);
        editLayout = view.findViewById(R.id.editLayout);
        msgLayout = view.findViewById(R.id.msgLayout);
        parent = view.findViewById(R.id.parent);

        sendMailButton.setOnClickListener(v->{
            sendResetMail();
        });
        okButton.setOnClickListener(v->{
            hideFragment();
        });
        parent.setOnClickListener(v->{
            hideFragment();
        });
        return view;

    }
    private void sendResetMail(){

        if(!validateForm()){
            return;
        }

        String email = fieldEmail.getText().toString().trim();
        showLoading();
        fireAuthService.sendResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    hideLoading();
                    successMail(email);
                    afterUpdate();
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    failureMail(email);
                    afterUpdate();
                });
    }
    private void successMail(String email){
        String successText = "We have sent you a password reset mail on your email address "+email+" please open your this email and follow the instructions";
        message.setText(successText);

    }
    private void failureMail(String email){
        String failText = "We could not send you password reset mail on your email address "+email+" this could be because this is not valid email address or this email address is not registered with us";
        message.setText(failText);
    }
    private void afterUpdate(){
        msgLayout.setVisibility(View.VISIBLE);
        editLayout.setVisibility(View.GONE);
    }
    private boolean validateForm(){
        boolean valid = true;

        String email = fieldEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            fieldEmail.setError("Required.");
            valid = false;
        } else {
            fieldEmail.setError(null);
        }
        if(!TextUtils.isEmpty(email) && !isValidEmail(email)){
            valid = false;
            fieldEmail.setError("Enter a valid email.");
        }else {
            fieldEmail.setError(null);
        }

        return valid;

    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private void showLoading(){
        sendMailButton.setEnabled(false);
        sendMailButton.setText("Please wait...");
    }
    private void hideLoading(){
        sendMailButton.setEnabled(true);
        sendMailButton.setText("Send Reset Email");
    }
    public void hideFragment(){

        try{
            FragmentManager fragmentManager = getFragmentManager();

            Fragment fragment = fragmentManager != null ? getFragmentManager().findFragmentByTag("forget_pwd") : null;

            if(fragment != null){
                fragmentManager.beginTransaction().remove(fragment).commit();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        //context.findViewById(R.id.loadingIndicator).setVisibility(View.GONE);
    }
}