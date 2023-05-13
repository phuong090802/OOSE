package com.ute.myapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.User;
import com.ute.myapp.preferencehelper.PreferenceHelper;
import com.ute.myapp.util.MyUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class SignInActivity extends AppCompatActivity {
    private TextInputEditText textInputEditTextUserName;
    private TextInputEditText textInputEditTextPassword;
    Button buttonLogin;
    private FireStoreHelper fireStoreHelper;
    private TextView textViewLoginFail;
    private PreferenceHelper preferenceHelper;
    User user;
    private ProgressBar progressBar;
    Button buttonCreateAccount;
    Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initializeView();
    }

    private void initializeView() {
        fireStoreHelper = FireStoreHelper.getInstance();
        preferenceHelper = PreferenceHelper.getInstance(this);
        textViewLoginFail = findViewById(R.id.text_view_login_fail);
        progressBar = findViewById(R.id.progress_bar_login);
        textInputEditTextUserName = findViewById(R.id.text_input_edit_text_username);
        textInputEditTextPassword = findViewById(R.id.text_input_edit_text_password);
        buttonCreateAccount = findViewById(R.id.button_create_new_account);
        buttonSignUp = findViewById(R.id.button_forgot_password);
        buttonSignUp.setOnClickListener(buttonSignUpOnClickListener);
        buttonCreateAccount.setOnClickListener(buttonCreateAccountOnClickListener);
        String userName = preferenceHelper.getUserName();
        String password = preferenceHelper.getPassword();

        if (userName != null && password != null) {
            textInputEditTextUserName.setText(userName);
            textInputEditTextPassword.setText(password);
        }
        buttonLogin = findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(buttonLoginOnClickListener);
    }

    private final View.OnClickListener buttonCreateAccountOnClickListener = view -> {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    };

    private final OnCompleteListener<QuerySnapshot> loginOnCompleteListener = task -> {
        if (task.isSuccessful()) {
            String password = String.valueOf(textInputEditTextPassword.getText());
            QuerySnapshot querySnapshot = task.getResult();
            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                if (documentSnapshot.exists()) {
                    User userData = documentSnapshot.toObject(User.class);

                    if (userData != null) {
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime lockedTime = LocalDateTime.parse(userData.getLockedTime());
                        if (now.isAfter(lockedTime)) {
                            String firstName = userData.getFirstName();
                            String lastName = userData.getLastName();
                            String userName = userData.getUserName();
                            String hashedPassword = userData.getPassword();
                            String phone = userData.getPhone();
                            String dateOfBirth = userData.getDateOfBirth();
                            String email = userData.getEmail();
                            String imageUrl = userData.getImageUrl();
                            boolean status = userData.isStatus();
                            if (MyUtil.isMatch(password, hashedPassword)) {
                                File tempDir = this.getCacheDir();
                                String extension = MyUtil.getExtension(imageUrl);
                                String fileName = System.currentTimeMillis() + "." + extension;
                                File outputFile = new File(tempDir, fileName);
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
                                                String roleName = userData.getRoleName();

                                                user = new User();
                                                user.setUserId(documentSnapshot.getId());
                                                user.setFirstName(firstName);
                                                user.setLastName(lastName);
                                                user.setUserName(userName);
                                                user.setPassword(password);
                                                user.setPhone(phone);
                                                user.setDateOfBirth(dateOfBirth);
                                                user.setEmail(email);
                                                user.setImageUrl(outputFile.getAbsolutePath());
                                                user.setRoleName(roleName);
                                                user.setStatus(status);
                                                user.setLockedTime(String.valueOf(lockedTime));
                                                if (!status) {
                                                    fireStoreHelper.updateStatusUser(documentSnapshot.getId());
                                                    user.setStatus(true);
                                                }
                                                preferenceHelper.saveUser(user);
                                                preferenceHelper.setLoggedIn(true);
                                                Intent intent = new Intent(SignInActivity.this, LoadingActivity.class);
                                                intent.putExtra(Constant.USER, user);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                            }

                                            @Override
                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                            }
                                        });

                            } else {
                                showLoginFail();
                            }
                        } else {
                            textViewLoginFail.setVisibility(View.VISIBLE);
                            textViewLoginFail.setText(MyUtil.showLockedTime(lockedTime));
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }

            } else {
                showLoginFail();
            }
        } else {
            Toast.makeText(this, R.string.an_error_occurred, Toast.LENGTH_SHORT).show();
        }
    };

    private void showLoginFail() {
        textViewLoginFail.setVisibility(View.VISIBLE);
        textViewLoginFail.setText(R.string.login_fail);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private final View.OnClickListener buttonLoginOnClickListener = view -> {
        progressBar.setVisibility(View.VISIBLE);
        String username = String.valueOf(textInputEditTextUserName.getText());
        fireStoreHelper.login(username, loginOnCompleteListener);
    };

    private final View.OnClickListener buttonSignUpOnClickListener = view -> {
        Intent intent = new Intent(this, ForgotPassword1Activity.class);
        startActivity(intent);
    };

}