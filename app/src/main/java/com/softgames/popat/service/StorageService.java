package com.softgames.popat.service;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.InputStream;

public class StorageService {

    private FirebaseStorage storage;

    public StorageService() {
        storage = FirebaseStorage.getInstance();
    }

    public UploadTask uploadFile(String filePath, byte[] bytes){

        StorageReference storageReference = storage.getReference().child(filePath);

        return storageReference.putBytes(bytes);
    }
    public StorageReference getFileReference(String filePath){
        return storage.getReference().child(filePath);
    }
    public UploadTask uploadFile(String filePath, InputStream stream){

        StorageReference storageReference = storage.getReference().child(filePath);

        return storageReference.putStream(stream);
    }
    public UploadTask uploadFile(String filePath, Uri uri){

        StorageReference mountainImagesRef = storage.getReference().child(filePath);

        return mountainImagesRef.putFile(uri);
    }
}
