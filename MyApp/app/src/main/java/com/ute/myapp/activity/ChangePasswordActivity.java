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
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.User;
import com.ute.myapp.preferencehelper.PreferenceHelper;

public class ChangePasswordActivity extends AppCompatActivity {
    private PreferenceHelper preferenceHelper;
    private User user;
    private TextInputLayout textInputLayoutOldPassword;
    private TextInputLayout textInputLayoutPasswordChange;
    private TextInputLayout textInputLayoutConformPasswordChange;
    private TextInputEditText textInputEditTextOldPassword;
    private TextInputEditText textInputEditTextPasswordChange;
    private TextInputEditText textInputEditTextConformPasswordChange;
    Button button;
    private FireStoreHelper fireStoreHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initializeView();
    }

    private void initializeView() {
        fireStoreHelper = FireStoreHelper.getInstance();
        preferenceHelper = PreferenceHelper.getInstance(this);
        Intent intentGetData = getIntent();
        user = (User) intentGetData.getSerializableExtra(Constant.USER);


        textInputLayoutOldPassword = findViewById(R.id.text_input_layout_old_password);
        textInputLayoutPasswordChange = findViewById(R.id.text_input_layout_password_change);
        textInputLayoutConformPasswordChange = findViewById(R.id.text_input_layout_confirm_password_change);

        textInputEditTextOldPassword = findViewById(R.id.text_input_edit_text_old_password);
        textInputEditTextPasswordChange = findViewById(R.id.text_input_edit_text_password_change);
        textInputEditTextConformPasswordChange = findViewById(R.id.text_input_edit_text_confirm_password_change);
        button = findViewById(R.id.button_change_password);


        textInputEditTextOldPassword.setNextFocusForwardId(R.id.text_input_edit_text_password_change);
        textInputEditTextPasswordChange.setNextFocusForwardId(R.id.text_input_edit_text_confirm_password_change);
        textInputEditTextConformPasswordChange.setNextFocusForwardId(R.id.button_change_password);
        button.setOnClickListener(onClickListener);


    }

    private boolean checkEmptyField() {
        boolean flag = true;
        if (TextUtils.isEmpty(textInputEditTextOldPassword.getText())) {
            textInputLayoutOldPassword.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutOldPassword.setError(null);
        }

        if (TextUtils.isEmpty(textInputEditTextPasswordChange.getText())) {
            textInputLayoutPasswordChange.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutPasswordChange.setError(null);
        }

        if (TextUtils.isEmpty(textInputEditTextConformPasswordChange.getText())) {
            textInputLayoutConformPasswordChange.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutConformPasswordChange.setError(null);
        }

        if (!TextUtils.isEmpty(textInputEditTextPasswordChange.getText()) && !TextUtils.isEmpty(textInputEditTextConformPasswordChange.getText()) && !textInputEditTextPasswordChange.getText().toString().equals(textInputEditTextConformPasswordChange.getText().toString())) {
            textInputLayoutPasswordChange.setError(getString(R.string.error_password));
            textInputLayoutConformPasswordChange.setError(getString(R.string.error_password));
            flag = false;
        } else if ((!TextUtils.isEmpty(textInputEditTextPasswordChange.getText()) && !TextUtils.isEmpty(textInputEditTextConformPasswordChange.getText()) && textInputEditTextPasswordChange.getText().toString().equals(textInputEditTextConformPasswordChange.getText().toString()))) {
            textInputLayoutPasswordChange.setError(null);
            textInputLayoutConformPasswordChange.setError(null);
        }
        return flag;
    }

    private final OnSuccessListener<Void> updatePasswordOnSuccessListener = unused -> {
        Toast.makeText(this, R.string.change_password_successful, Toast.LENGTH_SHORT).show();
        preferenceHelper.setLoggedIn(false);
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    };

    private final OnFailureListener updatePasswordOnFailureListener = e -> Toast.makeText(this, R.string.change_password_failure, Toast.LENGTH_SHORT).show();

    private final View.OnClickListener onClickListener = view -> {
        if (checkEmptyField()) {
            String oldPassword = String.valueOf(textInputEditTextOldPassword.getText());
            if (!user.getPassword().equals(oldPassword)) {
                textInputLayoutOldPassword.setError(getString(R.string.old_password_error));
            } else {
                String newPassword = String.valueOf(textInputEditTextPasswordChange.getText());
                fireStoreHelper.changePassword(user.getUserId(), newPassword, updatePasswordOnSuccessListener, updatePasswordOnFailureListener);
            }
        }
    };
}