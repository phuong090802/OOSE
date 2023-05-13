package com.ute.myapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.User;
import com.ute.myapp.util.MyUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ForgotPassword1Activity extends AppCompatActivity {
    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;
    Button button;
    private FireStoreHelper fireStoreHelper;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password1);
        initializeView();
    }

    private void initializeView() {
        fireStoreHelper = FireStoreHelper.getInstance();
        userList = new ArrayList<>();
        fireStoreHelper.getAllUser(getAllUserOnSuccessListener);
        textInputLayout = findViewById(R.id.text_input_layout_username_forgot);
        textInputEditText = findViewById(R.id.text_input_edit_username_forgot);
        button = findViewById(R.id.button_forgot_password_forgot);
        button.setOnClickListener(onClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userList = new ArrayList<>();
        fireStoreHelper.getAllUser(getAllUserOnSuccessListener);
    }

    private final OnSuccessListener<QuerySnapshot> getAllUserOnSuccessListener = queryDocumentSnapshots -> {
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                User userData = documentSnapshot.toObject(User.class);
                if (userData != null) {
                    userData.setUserId(documentSnapshot.getId());
                    userList.add(userData);
                }
            }
        }
    };

    private final View.OnClickListener onClickListener = view -> {
        if (TextUtils.isEmpty(textInputEditText.getText())) {
            textInputLayout.setError(getString(R.string.require));
        } else {
            User user = null;
            String data = String.valueOf(textInputEditText.getText());
            for (User userItem : userList) {
                if (userItem.getUserName().equals(data) || userItem.getEmail().equals(data) || userItem.getPhone().equals(data)) {
                    user = userItem;
                    break;
                }
            }
            if (user == null) {
                textInputLayout.setError(getString(R.string.account_is_not_exist));
            } else {
                Intent intent = new Intent(this, ForgotPassword2Activity.class);
                String imageUrl = user.getImageUrl();
                File tempDir = this.getCacheDir();
                String extension = MyUtil.getExtension(imageUrl);
                String fileName = System.currentTimeMillis() + "." + extension;
                File outputFile = new File(tempDir, fileName);
                User finalUser = user;
                Picasso.get()
                        .load(imageUrl)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                try {
                                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                                    if (extension.equals(Constant.PNG)) {
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                    } else if (extension.contains(Constant.JPG) || extension.contains(Constant.JPEG)) {
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                    }
                                    outputStream.flush();
                                    outputStream.close();

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                finalUser.setImageUrl(outputFile.getAbsolutePath());
                                intent.putExtra(Constant.USER, finalUser);
                                startActivity(intent);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

            }
        }
    };

}