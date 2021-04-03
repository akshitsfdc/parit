package com.softgames.popat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.softgames.popat.R;
import com.softgames.popat.models.GameRequest;
import com.softgames.popat.service.FireStoreService;
import com.softgames.popat.service.SenderService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Home extends BaseActivity {

    private Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initObjects();
    }

    private void initObjects(){
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(v -> {
            routing.navigate(MultiPlayerGameLobbyActivity.class, false);
        });
    }

    private void loadUser(){

        
    }
}