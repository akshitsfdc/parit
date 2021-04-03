package com.softgames.popat.service;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.softgames.popat.R;
import com.softgames.popat.activities.BaseActivity;

public class FireAuthService {

    private FirebaseAuth firebaseAuth;
    private Activity activity;
    public final int RC_SIGN_IN = 9001;

    public FireAuthService() {

        firebaseAuth = FirebaseAuth.getInstance();

        this.activity = activity;
    }
    public FireAuthService(Activity activity) {

        firebaseAuth = FirebaseAuth.getInstance();

        this.activity = activity;
    }

    public String getUserId(){

        return firebaseAuth.getCurrentUser().getUid();
    }

    public FirebaseUser getCurrentUser(){

        return firebaseAuth.getCurrentUser();

    }

    public Task<Void> reloadCurrentUser(){
        return getCurrentUser().reload();
    }
    public Task<AuthResult> emailPwdSignIn(String email, String password){

        return firebaseAuth.signInWithEmailAndPassword(email, password);

    }

    public Task<Void> sendEmailVerification(FirebaseUser user) {

        return user.sendEmailVerification();

    }
    public Task<Void> sendResetEmail(String emailAddress){
         return firebaseAuth.sendPasswordResetEmail(emailAddress);
    }
    public Task<Void> sendEmailVerification() {

        FirebaseUser user = getCurrentUser();
        if(user != null){
            return user.sendEmailVerification();
        }
        return null;
    }

    public Task<AuthResult> createAccount(String email, String password){
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }
    public void openGoogleSignIn(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        activity.startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    public Task<AuthResult> firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        return firebaseAuth.signInWithCredential(credential);

    }

    private void showLoading(){
        ((BaseActivity)activity).showLoading();
    }
    private void hideLoading(){
        ((BaseActivity)activity).hideLoading();
    }

}
