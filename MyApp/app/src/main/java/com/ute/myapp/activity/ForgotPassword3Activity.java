package com.ute.myapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.dto.Claim;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.User;

public class ForgotPassword3Activity extends AppCompatActivity {
    private User user;
    private Claim claim;
    private TextInputLayout textInputLayoutOTP;
    private TextInputEditText textInputEditTextOTP;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText textInputEditTextPassword;
    private TextInputLayout textInputLayoutConformPassword;
    private TextInputEditText textInputEditTextConformPassword;
    Button button;
    private FireStoreHelper fireStoreHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password3);
        initializeView();
    }

    private void initializeView() {
        fireStoreHelper = FireStoreHelper.getInstance();
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constant.USER);
        claim = (Claim) intent.getSerializableExtra(Constant.CLAIM);


        textInputLayoutOTP = findViewById(R.id.text_input_layout_otp_code);
        textInputEditTextOTP = findViewById(R.id.text_input_edit_text_otp_code);
        textInputLayoutPassword = findViewById(R.id.text_input_layout_password_change);
        textInputEditTextPassword = findViewById(R.id.text_input_edit_text_password_change);
        textInputLayoutConformPassword = findViewById(R.id.text_input_layout_confirm_password_change);
        textInputEditTextConformPassword = findViewById(R.id.text_input_edit_text_confirm_password_change);
        button = findViewById(R.id.button_change_password);
        button.setOnClickListener(onClickListener);
        textInputEditTextOTP.setNextFocusForwardId(R.id.text_input_edit_text_password_change);
        textInputEditTextPassword.setNextFocusForwardId(R.id.text_input_edit_text_confirm_password_change);
        textInputEditTextConformPassword.setNextFocusForwardId(R.id.button_change_password);

    }

    private final OnSuccessListener<Void> updatePasswordOnSuccessListener = unused -> {
        Toast.makeText(this, R.string.change_password_successful, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    };

    private final OnFailureListener updatePasswordOnFailureListener = e -> Toast.makeText(this, R.string.change_password_failure, Toast.LENGTH_SHORT).show();


    private final View.OnClickListener onClickListener = view -> {
        long current = System.currentTimeMillis();
        long time = claim.getTime();
        if (current > time) {
            Toast.makeText(this, R.string.expire_otp, Toast.LENGTH_SHORT).show();
            Intent backIntent = new Intent(this, ForgotPassword2Activity.class);
            startActivity(backIntent);
        } else {
            if (checkField()) {
                String password = String.valueOf(textInputEditTextPassword.getText());
                fireStoreHelper.changePassword(user.getUserId(), password, updatePasswordOnSuccessListener, updatePasswordOnFailureListener);
            }
        }
    };

    private boolean checkField() {
        boolean flag = true;
        if (TextUtils.isEmpty(textInputEditTextOTP.getText())) {
            textInputLayoutOTP.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutOTP.setError(null);
        }
        if (!String.valueOf(textInputEditTextOTP.getText()).equals(claim.getOTP())) {
            textInputLayoutOTP.setError(getString(R.string.error_otp));
            flag = false;
        } else {
            textInputLayoutOTP.setError(null);
        }
        if (TextUtils.isEmpty(textInputEditTextConformPassword.getText())) {
            textInputLayoutConformPassword.setError(getString(R.string.error_otp));
            flag = false;
        } else {
            textInputLayoutConformPassword.setError(null);
        }
        if (!TextUtils.isEmpty(textInputEditTextPassword.getText()) && !TextUtils.isEmpty(textInputEditTextConformPassword.getText()) && !textInputEditTextPassword.getText().toString().equals(textInputEditTextConformPassword.getText().toString())) {
            textInputLayoutPassword.setError(getString(R.string.error_password));
            textInputLayoutConformPassword.setError(getString(R.string.error_password));
            flag = false;
        } else if ((!TextUtils.isEmpty(textInputEditTextPassword.getText()) && !TextUtils.isEmpty(textInputEditTextConformPassword.getText()) && textInputEditTextPassword.getText().toString().equals(textInputEditTextConformPassword.getText().toString()))) {
            textInputLayoutPassword.setError(null);
            textInputLayoutConformPassword.setError(null);
        }
        return flag;
    }
}