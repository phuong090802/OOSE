package com.ute.myapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.dto.Claim;
import com.ute.myapp.model.User;
import com.ute.myapp.util.MyUtil;

import java.io.File;

public class ForgotPassword2Activity extends AppCompatActivity {
    private User user;
    TextView textViewYourEmail;
    ShapeableImageView shapeableImageView;
    Button buttonNotYou;
    Button buttonLoginWithPassword;
    Button buttonNextForgot;
    TextView textViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password2);
        initializeView();

    }

    private void initializeView() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constant.USER);
        textViewYourEmail = findViewById(R.id.text_view_your_email);
        textViewName = findViewById(R.id.text_view_name);
        textViewName.setText(MyUtil.fullName(user.getFirstName(), user.getLastName()));
        shapeableImageView = findViewById(R.id.image_view_user);
        textViewYourEmail.setText(MyUtil.convertEmail(user.getEmail()));
        File imageFile = new File(user.getImageUrl());
        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            shapeableImageView.setImageBitmap(bitmap);
        }

        buttonNotYou = findViewById(R.id.button_not_you);
        buttonLoginWithPassword = findViewById(R.id.button_login_with_password);
        buttonNextForgot = findViewById(R.id.button_next_forgot);
        buttonLoginWithPassword.setOnClickListener(buttonLoginWithPasswordOnClickListener);
        buttonNotYou.setOnClickListener(buttonNotYouOnClickListener);
        buttonNextForgot.setOnClickListener(buttonNextForgotOnClickListener);
    }

    private final View.OnClickListener buttonLoginWithPasswordOnClickListener = view -> {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    };

    private final View.OnClickListener buttonNotYouOnClickListener = view -> {
        Intent intent = new Intent(this, ForgotPassword1Activity.class);
        startActivity(intent);
    };

    private final View.OnClickListener buttonNextForgotOnClickListener = view -> {
        String OTP = MyUtil.randomOTP();
        long time = System.currentTimeMillis() + 300000;
        Claim claim = new Claim(OTP, time);
        MyUtil.sendMail(user.getEmail(), OTP);
        Intent intent = new Intent(this, ForgotPassword3Activity.class);
        intent.putExtra(Constant.USER, user);
        intent.putExtra(Constant.CLAIM, claim);
        startActivity(intent);
    };

}