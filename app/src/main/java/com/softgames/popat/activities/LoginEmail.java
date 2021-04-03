package com.softgames.popat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.softgames.popat.R;
import com.softgames.popat.fragments.ForgetPasswordFragment;
import com.softgames.popat.fragments.Loading;
import com.softgames.popat.models.PopatPlayer;
import com.softgames.popat.service.FireAuthService;

import java.util.Objects;

public class LoginEmail extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginEmail";

    private EditText mEmailField;
    private EditText mPasswordField;

    private TextView forgotPwd;
    private FireAuthService fireAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        fireAuthService = new FireAuthService(this);



        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);

        forgotPwd = findViewById(R.id.forgotPwd);

        // Buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);

        findViewById(R.id.googleSignInButton).setOnClickListener(this);


        findViewById(R.id.newUserButton).setOnClickListener(this);

        forgotPwd.setOnClickListener(this);

    }

    private void goToSignUp(){
        routing.navigate(SignUpActivity.class, false
        );
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();

    }
    // [END on_start_check_user]

    public void showForgetPwd(){

        try {
            FragmentManager fragmentManager = getSupportFragmentManager();

            Fragment fragment = fragmentManager.findFragmentByTag("forget_pwd");

            if(fragment == null){
                ForgetPasswordFragment loading = new ForgetPasswordFragment();
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, loading,"forget_pwd")
                        .commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        //context.findViewById(R.id.loadingIndicator).setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == fireAuthService.RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                loginWithGoogle(task.getResult(ApiException.class));
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }
    // [END onactivityresult]

    private void loginWithGoogle(GoogleSignInAccount acct){

        showLoading();

        fireAuthService.firebaseAuthWithGoogle(acct)

                .addOnSuccessListener(authResult -> {
                    updateUI(authResult.getUser());
                    hideLoading();
                })
                .addOnFailureListener(e -> {
                    updateUI(null);
                    hideLoading();
                });
    }

    private void signIn(String email, String password) {


        if (!validateForm()) {
            return;
        }

        if(!localFileUtils.isNetworkConnected()){
            uiUtils.showShortSnakeBar("You are not connected to the internet");
            return;
        }

        showLoading();

        fireAuthService.emailPwdSignIn(email, password)
                .addOnSuccessListener(authResult -> {

                    hideLoading();
                    FirebaseUser user = authResult.getUser();


                    if(user !=null && !user.isEmailVerified()){
                        sendEmailVerification(user);
                    }else{
                        updateUI(user);
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    uiUtils.showShortSnakeBar("Email or password is invalid!");
                    updateUI(null);
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }
        if(!TextUtils.isEmpty(email) && !isValidEmail(email)){
            valid = false;
            mEmailField.setError("Enter a valid email.");
        }else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            createUser(user);
        } else {
            uiUtils.showShortSnakeBar(getString(R.string.generic_error_msg));
        }
    }

    private void createUser(FirebaseUser firebaseUser){


        PopatPlayer popatPlayer = getUserObj(firebaseUser);
        showLoading();

        fireStoreService.setData(getString(R.string.user_collection), popatPlayer.getUserId(), popatPlayer)
                .addOnSuccessListener(aVoid -> {
                    hideLoading();
                    routing.navigateAndClear(Home.class);
                })
                .addOnFailureListener(e -> {
                    uiUtils.showLongSnakeBar("Something went wrong, please try again later.");
                    hideLoading();
                });
    }
    private PopatPlayer getUserObj(FirebaseUser firebaseUser){

        PopatPlayer popatPlayer = new PopatPlayer();
        popatPlayer.setName(firebaseUser.getDisplayName());
        popatPlayer.setUserId(fireAuthService.getUserId());
        popatPlayer.setCoins(0);
        popatPlayer.setDummyPopats(0);
        popatPlayer.setGames(0);
        popatPlayer.setEmail(firebaseUser.getEmail());
        popatPlayer.setLevel1Popats(0);
        popatPlayer.setLevel2Popats(0);
        popatPlayer.setProfilePic(Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString());

        return popatPlayer;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailSignInButton) {
            signIn(mEmailField.getText().toString().trim(), mPasswordField.getText().toString().trim());

        } else if (i == R.id.googleSignInButton) {
            fireAuthService.openGoogleSignIn();
        } else if(i == R.id.newUserButton){
            goToSignUp();
        }else if(i == R.id.forgotPwd){
            showForgetPwd();
        }

    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    private void sendEmailVerification(FirebaseUser user) {


        showLoading();


        fireAuthService.sendEmailVerification(user)
                .addOnSuccessListener(aVoid -> {
                    hideLoading();
                    navigateToEmailVerification(true);
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    navigateToEmailVerification(false);
                });


    }

    private void navigateToEmailVerification(boolean isEmailSent){

        routing.appendParams("isEmailSent", isEmailSent);
        routing.navigate(AccountVerificationActivity.class, false);

    }
}
