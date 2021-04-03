package com.softgames.popat.activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.softgames.popat.R;
import com.softgames.popat.models.PopatPlayer;
import com.softgames.popat.service.FireAuthService;

import java.util.Date;

public class MainActivity extends BaseActivity {

    private FirebaseUser user;
    private FireAuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }catch (Exception e){
            e.printStackTrace();
        }

        authService = new FireAuthService();
        user = authService.getCurrentUser();

        int SPLASH_DISPLAY_LENGTH = 3000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                routeUser();
            }
        }, SPLASH_DISPLAY_LENGTH);

        routeUser();

    }

    private void routeUser(){

        if(user == null){
            routing.navigate(LoginEmail.class, true);
        }else {
            if(!user.isEmailVerified()){
                sendVerificationEmail();
            }else {
                routing.navigate(Home.class, true);
            }

        }
    }
    private void sendVerificationEmail(){

        authService.sendEmailVerification(user)
                .addOnSuccessListener(aVoid -> {
                    navigateToEmailVerification(true);
                })
                .addOnFailureListener(e -> {
                    navigateToEmailVerification(false);
                });

    }
    private void navigateToEmailVerification(boolean isEmailSent){

        routing.appendParams("isEmailSent", isEmailSent);
        routing.navigate(AccountVerificationActivity.class, true);

    }

}