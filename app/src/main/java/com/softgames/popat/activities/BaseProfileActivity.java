package com.softgames.popat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.softgames.popat.R;
import com.softgames.popat.models.PopatPlayer;
import com.softgames.popat.service.FireAuthService;
import com.softgames.popat.service.StorageService;
import com.softgames.popat.utils.ImageResizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class BaseProfileActivity extends BaseActivity {

    private TextView nameEditText, pleaseWait, skipTxt;
    private ImageView profilePicture;
    private byte[] profilePicBytes;
    private ImageResizer imageResizer;
    private Button updateButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_profile);

        nameEditText = findViewById(R.id.user_name);
        profilePicture = findViewById(R.id.profile_image);
        updateButton = findViewById(R.id.setContinue);
        progressBar = findViewById(R.id.progressBar);
        pleaseWait = findViewById(R.id.pleaseWait);
        skipTxt = findViewById(R.id.skipTxt);

        imageResizer = new ImageResizer(this);

        profilePicture.setOnClickListener(v -> {
            chooseImage();
        });
        updateButton.setOnClickListener(v -> {
            updateProfile();
        });
        skipTxt.setOnClickListener(v -> {
            routing.navigateAndClear(Home.class);
        });
        preLoadData();
    }

    private void preLoadData(){
        FireAuthService authService = new FireAuthService();
        if(authService == null){
            uiUtils.showLongSnakeBar("No user logged in");
            return;
        }
        showLoading();
      //  profileImageProgress.setVisibility(View.VISIBLE);
        fireStoreService.getData(getString(R.string.user_collection), authService.getUserId())
                .addOnSuccessListener(documentSnapshot -> {
                    hideLoading();
                  //  profileImageProgress.setVisibility(View.GONE);
                    PopatPlayer popatPlayer = documentSnapshot.toObject(PopatPlayer.class);
                    if(popatPlayer != null){
                        showRemoteImage(popatPlayer.getProfilePic(), profilePicture);
                        nameEditText.setText(popatPlayer.getName());
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                   // profileImageProgress.setVisibility(View.GONE);
                    Log.d(TAG, "preLoadData: user data could not be loaded.");
                });
    }

    private boolean validateForm(){
        boolean valid = true;

        if(profilePicBytes == null){
            valid = false;
            uiUtils.showLongSnakeBar("Please select a profile picture");
        }
        String name = nameEditText.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            valid = false;
            nameEditText.setError("Required.");
        }

        return valid;
    }

    private void chooseImage(){

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Profile Picture")
                .setActivityMenuIconColor(Color.parseColor("#ffffff"))
                .setGuidelinesColor(Color.parseColor("#2B90FF"))
//                .setMinCropResultSize(1000,1000)
                .setAspectRatio(4,4)
                .setFixAspectRatio(true)
                .start(this);
    }

    private void updateProfile(){

        if(!validateForm()){
            return;
        }

        String name = nameEditText.getText().toString().trim();

        Log.d(TAG, "updateProfile: picturePath 1 : ");
        StorageService storageService = new StorageService();

        FireAuthService authService = new FireAuthService();

        FirebaseUser firebaseUser = authService.getCurrentUser();

        if(firebaseUser == null){
            uiUtils.showLongSnakeBar("No user logged in");
            return;
        }

        updateButton.setEnabled(false);
        skipTxt.setEnabled(false);
        String picturePath = getString(R.string.profile_pic_path)+firebaseUser.getUid();

        pleaseWait.setVisibility(View.VISIBLE);

        storageService.uploadFile(picturePath,profilePicBytes)
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressBar.setProgress((int)progress);
                })
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        uiUtils.showLongSnakeBar("Something went wrong, please try again later");
                    }
                    // Continue with the task to get the download URL
                    return storageService.getFileReference(picturePath).getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    updateButton.setEnabled(true);
                    skipTxt.setEnabled(true);
                    pleaseWait.setVisibility(View.GONE);
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();

                        Map<String, Object> map = new HashMap<>();

                        map.put("profilePic", downloadUri != null ? downloadUri.toString() : null);
                        map.put("name", name);
                        updateUserData(map, firebaseUser.getUid());

                    } else {
                        // Handle failures
                        // ...
                    }
                })

                .addOnFailureListener(e -> {
                    updateButton.setEnabled(true);
                    skipTxt.setEnabled(true);
                    pleaseWait.setVisibility(View.GONE);
                    e.printStackTrace();
                });

//        .continueWithTask(task -> {
//            if (!task.isSuccessful()) {
//                throw task.getException();
//            }
//            // Continue with the task to get the download URL
//            return storageService.getFileReference(picturePath).getDownloadUrl();
//        })
    }

    private void updateUserData(Map<String, Object> map, String userId){
        showLoading();
        fireStoreService.updateData(getString(R.string.user_collection), userId, map)
            .addOnSuccessListener(aVoid -> {
                hideLoading();
                routing.navigateAndClear(Home.class);
            })
            .addOnFailureListener(e -> {
                hideLoading();
                uiUtils.showLongSnakeBar("Profile could not be updated, please try again");
            });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri uri = result.getUri();
                profilePicBytes = imageResizer.compressImage(uri);
                showRemoteImage(uri.toString(), profilePicture);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d(TAG, "onActivityResult: "+CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE);
                uiUtils.showLongSnakeBar("Could not get picture from your camera source, please select from gallery");
            }else {

                uiUtils.showLongSnakeBar("Could not get picture from your camera source, please select from gallery");
            }
        }
    }


}