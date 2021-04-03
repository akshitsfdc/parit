package com.softgames.popat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.softgames.popat.R;
import com.softgames.popat.models.PopatPlayer;
import com.softgames.popat.service.FireAuthService;

public class AccountVerificationActivity extends BaseActivity {

    FireAuthService fireAuthService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verification);

        fireAuthService = new FireAuthService(this);

        findViewById(R.id.verifiedButton).setOnClickListener(v -> {
            afterUserVerified();
        });
        findViewById(R.id.resendButton).setOnClickListener(v -> {
            sendVerificationEmail();
        });

        boolean emailSent = (boolean)routing.getParam("isEmailSent");

        if(!emailSent){
            uiUtils.showLongSnakeBar("Could not send you verification email, please retry.");
        }
    }

    private void afterUserVerified(){

        fireAuthService.reloadCurrentUser()
                .addOnSuccessListener(aVoid -> {

                    FirebaseUser user = fireAuthService.getCurrentUser();

                    if(user.isEmailVerified()){
                        createUser();
                    }else {
                        uiUtils.showLongSnakeBar("Please verify your email first, check your inbox!");
                    }

                })
                .addOnFailureListener(e -> {
                    uiUtils.showLongSnakeBar("Could not verify your account at this time, please check your email address.");
                });
    }

    private void createUser(){

        FirebaseUser firebaseUser = fireAuthService.getCurrentUser();
        PopatPlayer popatPlayer = getUserObj(firebaseUser);
        showLoading();
        fireStoreService.setData(getString(R.string.user_collection), popatPlayer.getUserId(), popatPlayer)
            .addOnSuccessListener(aVoid -> {
                hideLoading();
                routing.navigateAndClear(BaseProfileActivity.class);
            })
            .addOnFailureListener(e -> {
                uiUtils.showLongSnakeBar("Something went wrong, please try again later.");
                hideLoading();
            });
    }
    private PopatPlayer getUserObj(FirebaseUser firebaseUser){

        PopatPlayer popatPlayer = new PopatPlayer();
        popatPlayer.setName("");
        popatPlayer.setUserId(fireAuthService.getUserId());
        popatPlayer.setCoins(0);
        popatPlayer.setDummyPopats(0);
        popatPlayer.setGames(0);
        popatPlayer.setEmail(fireAuthService.getCurrentUser().getEmail());
        popatPlayer.setLevel1Popats(0);
        popatPlayer.setLevel2Popats(0);
        popatPlayer.setProfilePic("");

        return popatPlayer;
    }
    private void sendVerificationEmail(){



        FirebaseUser user = fireAuthService.getCurrentUser();

        showLoading();


        fireAuthService.sendEmailVerification(user)
                .addOnSuccessListener(aVoid -> {
                    hideLoading();
                    uiUtils.showLongSnakeBar("Verification email sent to "+user.getEmail());
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    uiUtils.showLongSnakeBar("Failed to send you verification email at this moment, please try again later.");
                });
    }
}