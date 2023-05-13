package com.ute.myapp.firebasehelper;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.event.OnUploadListener;

public class FirebaseStorageHelper {
    private final FirebaseStorage firebaseStorage;
    private static FirebaseStorageHelper instance;

    private FirebaseStorageHelper() {
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    public static FirebaseStorageHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseStorageHelper();
        }
        return instance;
    }

    public void uploadImage(Uri imageUri, String imageName, final OnUploadListener listener) {
        StorageReference storageRef = firebaseStorage.getReference(Constant.USERS);
        StorageReference imageRef = storageRef.child(imageName);
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (listener != null) {
                listener.onUploadSuccess(uri.toString());
            }
        })).addOnFailureListener(e -> {
            if (listener != null) {
                listener.onUploadFailure();
            }
        });
    }

    public void deleteImage(String filePath) {
        StorageReference storageRef = firebaseStorage.getReference(Constant.USERS);
        StorageReference fileRef = storageRef.child(filePath);
        fileRef.delete();
    }

}
