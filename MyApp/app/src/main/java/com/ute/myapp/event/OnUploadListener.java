package com.ute.myapp.event;

public interface OnUploadListener {
    void onUploadSuccess(String downloadUrl);
    void onUploadFailure();
}
