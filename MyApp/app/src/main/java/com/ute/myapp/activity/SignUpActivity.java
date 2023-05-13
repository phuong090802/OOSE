package com.ute.myapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.User;
import com.ute.myapp.util.MyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutFirstName;
    private TextInputLayout textInputLayoutLastName;
    private TextInputLayout textInputLayoutUserName;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConformPassword;
    private TextInputLayout textInputLayoutDateOfBirth;
    private TextInputLayout textInputLayoutPhone;
    private TextInputLayout textInputLayoutEmail;
    private TextInputEditText textInputEditTextFirstName;
    private TextInputEditText textInputEditTextLastName;
    private TextInputEditText textInputEditTextUserName;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConformPassword;
    private TextInputEditText textInputEditTextDateOfBirth;
    private TextInputEditText textInputEditTextPhone;
    private TextInputEditText textInputEditTextEmail;
    Button buttonSignUp;
    private FireStoreHelper fireStoreHelper;
    private boolean register;
    private boolean status;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userList = new ArrayList<>();
        fireStoreHelper.getAllUser(getAllUserOnSuccessListener);
    }

    private void initializeView() {
        userList = new ArrayList<>();
        register = getIntent().getBooleanExtra(Constant.REGISTER, false);
        status = getIntent().getBooleanExtra(Constant.CREATE_ACTIVE_ACCOUNT, true);
        fireStoreHelper = FireStoreHelper.getInstance();
        fireStoreHelper.getAllUser(getAllUserOnSuccessListener);
        textInputEditTextFirstName = findViewById(R.id.text_input_edit_text_first_name);
        textInputEditTextLastName = findViewById(R.id.text_input_edit_text_last_name);
        textInputEditTextUserName = findViewById(R.id.text_input_edit_text_username_sign_in);
        textInputEditTextPassword = findViewById(R.id.text_input_edit_text_password_sign_in);
        textInputEditTextConformPassword = findViewById(R.id.text_input_edit_text_confirm_password_sign_in);
        textInputEditTextDateOfBirth = findViewById(R.id.text_input_edit_text_date_of_birth);
        textInputEditTextPhone = findViewById(R.id.text_input_edit_text_phone);
        textInputEditTextEmail = findViewById(R.id.text_input_edit_email);

        textInputLayoutFirstName = findViewById(R.id.text_input_layout_first_name);
        textInputLayoutLastName = findViewById(R.id.text_input_layout_last_name);
        textInputLayoutUserName = findViewById(R.id.text_input_layout_username_sign_in);
        textInputLayoutPassword = findViewById(R.id.text_input_layout_password_sign_in);
        textInputLayoutConformPassword = findViewById(R.id.text_input_layout_confirm_password_sign_in);
        textInputLayoutDateOfBirth = findViewById(R.id.text_input_layout_date_of_birth);
        textInputLayoutPhone = findViewById(R.id.text_input_layout_phone);
        textInputLayoutEmail = findViewById(R.id.text_input_layout_email);
        textInputEditTextFirstName.setNextFocusForwardId(R.id.text_input_edit_text_last_name);
        textInputEditTextPassword.setNextFocusForwardId(R.id.text_input_edit_text_confirm_password_sign_in);
        textInputEditTextConformPassword.setNextFocusForwardId(R.id.text_input_edit_text_date_of_birth);
        textInputEditTextDateOfBirth.setNextFocusForwardId(R.id.text_input_edit_text_phone);
        textInputEditTextPhone.setNextFocusForwardId(R.id.text_input_edit_email);
        buttonSignUp = findViewById(R.id.button_signup);
        textInputEditTextEmail.setNextFocusForwardId(R.id.button_signup);
        buttonSignUp.setOnClickListener(onClickListener);
    }

    private final OnSuccessListener<QuerySnapshot> getAllUserOnSuccessListener = queryDocumentSnapshots -> {
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    user.setUserId(documentSnapshot.getId());
                    userList.add(user);
                }
            }
        }
    };

    private boolean checkEmptyField() {
        boolean flag = true;
        if (TextUtils.isEmpty(textInputEditTextFirstName.getText())) {
            textInputLayoutFirstName.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutFirstName.setError(null);
        }
        if (TextUtils.isEmpty(textInputEditTextLastName.getText())) {
            textInputLayoutLastName.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutLastName.setError(null);
        }
        if (TextUtils.isEmpty(textInputEditTextUserName.getText())) {
            textInputLayoutUserName.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutUserName.setError(null);
        }
        if (TextUtils.isEmpty(textInputEditTextUserName.getText())) {
            textInputLayoutUserName.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutUserName.setError(null);
        }
        if (TextUtils.isEmpty(textInputEditTextPassword.getText())) {
            textInputLayoutPassword.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutPassword.setError(null);
        }
        if (TextUtils.isEmpty(textInputEditTextConformPassword.getText())) {
            textInputLayoutConformPassword.setError(getString(R.string.require));
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
        if (TextUtils.isEmpty(textInputEditTextDateOfBirth.getText())) {
            textInputEditTextDateOfBirth.setError(null);
            flag = false;
        }
        String pattern = "^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[0-2])/\\d{4}$";
        Pattern datePattern = Pattern.compile(pattern);
        Matcher matcher = datePattern.matcher(textInputEditTextDateOfBirth.getText());
        if (!matcher.matches()) {
            textInputLayoutDateOfBirth.setError(getString(R.string.error_date_of_birth));
            flag = false;
        } else {
            textInputLayoutDateOfBirth.setError(null);
        }
        if (TextUtils.isEmpty(textInputEditTextPhone.getText())) {
            textInputLayoutPhone.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutPhone.setError(null);
        }
        if (!TextUtils.isEmpty(textInputEditTextPhone.getText()) && !TextUtils.isDigitsOnly(textInputEditTextPhone.getText())) {
            textInputLayoutPhone.setError(getString(R.string.error_phone));
            flag = false;
        } else if (!TextUtils.isEmpty(textInputEditTextPhone.getText()) && TextUtils.isDigitsOnly(textInputEditTextPhone.getText())) {
            textInputLayoutPhone.setError(null);
        }
        if (TextUtils.isEmpty(textInputEditTextEmail.getText())) {
            textInputLayoutEmail.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutEmail.setError(null);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(textInputEditTextEmail.getText()).matches()) {
            textInputLayoutEmail.setError(getString(R.string.error_email));
            flag = false;
        } else {
            textInputLayoutEmail.setError(null);
        }
        return flag;
    }

    private final OnSuccessListener<DocumentReference> onSuccessListener = documentReference -> {
        Toast.makeText(this, R.string.create_successful, Toast.LENGTH_SHORT).show();
        if (register) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
        finish();
    };

    private final OnFailureListener onFailureListener = e -> Toast.makeText(this, R.string.create_failure, Toast.LENGTH_SHORT).show();


    private final View.OnClickListener onClickListener = view -> {
        String userName = String.valueOf(textInputEditTextUserName.getText());
        String phone = String.valueOf(textInputEditTextPhone.getText());
        String email = String.valueOf(textInputEditTextEmail.getText());
        boolean flag = true;
        if (checkEmptyField()) {
            for (User user : userList) {
                if (user.getUserName().equals(userName)) {
                    textInputLayoutUserName.setError(MyUtil.fieldExist(userName));
                    flag = false;
                    break;
                } else {
                    textInputLayoutUserName.setError(null);
                }
                if (user.getPhone().equals(phone)) {
                    textInputLayoutPhone.setError(MyUtil.fieldExist(phone));
                    flag = false;
                    break;
                } else {
                    textInputLayoutPhone.setError(null);
                }
                if (user.getEmail().equals(email)) {
                    textInputLayoutEmail.setError(MyUtil.fieldExist(email));
                    flag = false;
                    break;
                } else {
                    textInputLayoutEmail.setError(null);
                }
            }
            if (flag) {
                String firstName = String.valueOf(textInputEditTextFirstName.getText());
                String lastName = String.valueOf(textInputEditTextLastName.getText());
                String password = String.valueOf(textInputEditTextPassword.getText());
                String hashedPassword = MyUtil.hashedPassword(password);
                String dateOfBirth = String.valueOf(textInputEditTextDateOfBirth.getText());
                User user = new User();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setUserName(userName);
                user.setPassword(hashedPassword);
                user.setPhone(phone);
                user.setDateOfBirth(dateOfBirth);
                user.setEmail(email);
                user.setImageUrl(Constant.DEFAULT_AVATAR_URL);
                user.setRoleName(Constant.DEFAULT_ROLE_NAME);
                user.setStatus(status);
                user.setLockedTime(Constant.DEFAULT_LOCKED_DATE);
                fireStoreHelper.createAccount(user, onSuccessListener, onFailureListener);
            }
        }
    };
}