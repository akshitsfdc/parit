package com.softgames.popat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.softgames.popat.R;
import com.softgames.popat.service.FireAuthService;

public class SignUpActivity extends BaseActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirm;
    private FireAuthService fireAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fireAuthService = new FireAuthService();

        mEmailField = findViewById(R.id.fieldEmail);
        mPasswordField = findViewById(R.id.fieldPassword);
        mConfirm = findViewById(R.id.confirmPassword);

        findViewById(R.id.signUpButton).setOnClickListener(v -> {
            signUp();
        });

    }

    private void signUp(){

        if(!validateForm()){
            return;
        }


        String email = mEmailField.getText().toString().trim();
        String pwd = mPasswordField.getText().toString().trim();

        fireAuthService.createAccount(email, pwd)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();

                    if(!authResult.getAdditionalUserInfo().isNewUser()){
                        uiUtils.showLongSnakeBar("This user is already registered.");
                       return;
                    }
                    if(user !=null && !user.isEmailVerified()){
                        sendEmailVerification(user);
                    }else{
                        uiUtils.showLongSnakeBar("Something went wrong, please try again later");
                    }

                })
                .addOnFailureListener(e -> {
                   e.printStackTrace();
                   uiUtils.showLongSnakeBar("Authentication failed, please again try later");
                });

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

        if(password.length() < 6){
            uiUtils.showLongSnakeBar("Password must be 6 digits long");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        String confirm = mConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(confirm)) {
            mConfirm.setError("Required.");
            valid = false;
        } else {
            mConfirm.setError(null);
        }
        if (!TextUtils.isEmpty(confirm) && !TextUtils.isEmpty(password) && !TextUtils.equals(password, confirm)) {
            mConfirm.setError("Not Matching.");
            valid = false;
        } else {

        }

        return valid;
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private void navigateToEmailVerification(boolean isEmailSent){

        routing.appendParams("isEmailSent", isEmailSent);
        routing.navigate(AccountVerificationActivity.class, false);

    }
}