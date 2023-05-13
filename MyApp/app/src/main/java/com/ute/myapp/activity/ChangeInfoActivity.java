package com.ute.myapp.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.event.OnUploadListener;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.firebasehelper.FirebaseStorageHelper;
import com.ute.myapp.model.User;
import com.ute.myapp.preferencehelper.PreferenceHelper;
import com.ute.myapp.util.MyUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeInfoActivity extends AppCompatActivity {
    private User user;
    Button buttonFileChooser;
    private ShapeableImageView shapeableImageView;
    private TextInputEditText textInputEditTextFirstName;
    private TextInputEditText textInputEditTextLastName;
    private TextInputEditText textInputEditTextDateOfBirth;
    private TextInputEditText textInputEditTextPhone;
    private TextInputEditText textInputEditTextEmail;
    private TextInputLayout textInputLayoutFirstName;
    private TextInputLayout textInputLayoutLastName;
    private TextInputLayout textInputLayoutDateOfBirth;
    private TextInputLayout textInputLayoutPhone;
    private TextInputLayout textInputLayoutEmail;
    Button button;
    private Uri imageUri;
    private FirebaseStorageHelper firebaseStorageHelper;
    private FireStoreHelper fireStoreHelper;
    private PreferenceHelper preferenceHelper;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);
        initializeView();
    }

    private void initializeView() {
        preferenceHelper = PreferenceHelper.getInstance(this);
        firebaseStorageHelper = FirebaseStorageHelper.getInstance();
        fireStoreHelper = FireStoreHelper.getInstance();
        Intent intentGetData = getIntent();
        user = (User) intentGetData.getSerializableExtra(Constant.USER);
        shapeableImageView = findViewById(R.id.image_view_user_avatar);
        userList = new ArrayList<>();
        fireStoreHelper.getAllUser(getAllUserOnSuccessListener);
        File imageFile = new File(user.getImageUrl());
        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            shapeableImageView.setImageBitmap(bitmap);
        }
        textInputEditTextFirstName = findViewById(R.id.text_input_edit_text_first_name);
        textInputEditTextLastName = findViewById(R.id.text_input_edit_text_last_name);
        textInputEditTextDateOfBirth = findViewById(R.id.text_input_edit_text_date_of_birth);
        textInputEditTextPhone = findViewById(R.id.text_input_edit_text_phone);
        textInputEditTextEmail = findViewById(R.id.text_input_edit_email);

        textInputEditTextFirstName.setText(user.getFirstName());
        textInputEditTextLastName.setText(user.getLastName());
        textInputEditTextDateOfBirth.setText(user.getDateOfBirth());
        textInputEditTextPhone.setText(user.getPhone());
        textInputEditTextEmail.setText(user.getEmail());

        textInputLayoutFirstName = findViewById(R.id.text_input_layout_first_name);
        textInputLayoutLastName = findViewById(R.id.text_input_layout_last_name);
        textInputLayoutDateOfBirth = findViewById(R.id.text_input_layout_date_of_birth);
        textInputLayoutPhone = findViewById(R.id.text_input_layout_phone);
        textInputLayoutEmail = findViewById(R.id.text_input_layout_email);

        buttonFileChooser = findViewById(R.id.button_upload_avatar);
        button = findViewById(R.id.button_change);

        textInputEditTextFirstName.setNextFocusForwardId(R.id.text_input_edit_text_last_name);
        textInputEditTextLastName.setNextFocusForwardId(R.id.text_input_edit_text_date_of_birth);
        textInputEditTextDateOfBirth.setNextFocusForwardId(R.id.text_input_edit_text_phone);
        textInputEditTextPhone.setNextFocusForwardId(R.id.text_input_edit_email);
        textInputEditTextEmail.setNextFocusForwardId(R.id.button_upload_avatar);
        buttonFileChooser.setNextFocusForwardId(R.id.button_change);


        buttonFileChooser.setOnClickListener(buttonFileChooserOnClickListener);
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
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    user.setUserId(documentSnapshot.getId());
                    userList.add(user);
                }
            }
        }
    };

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            imageUri = result;
            Picasso.get().load(result).into(shapeableImageView);
        }
    });

    private String getFileNameExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private final OnUploadListener onUploadListener = new OnUploadListener() {
        @Override
        public void onUploadSuccess(String downloadUrl) {
            fireStoreHelper.getUrlAvatar(user.getUserId(), deleteOnSuccessListener);
            updateUser(downloadUrl);
        }

        @Override
        public void onUploadFailure() {
            Toast.makeText(ChangeInfoActivity.this, R.string.upload_avatar_failure, Toast.LENGTH_SHORT).show();
        }
    };

    private final OnSuccessListener<Void> updateUserOnSuccessListener = unused -> {
        Toast.makeText(this, R.string.edit_successful, Toast.LENGTH_SHORT).show();
        preferenceHelper.setLoggedIn(false);
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    };

    private final OnFailureListener updateUserOnFailureListener = e -> Toast.makeText(this, R.string.edit_failure, Toast.LENGTH_SHORT).show();


    private void updateUser(String imageUrl) {
        User updateUser = new User();
        String dateOfBirth = String.valueOf(textInputEditTextDateOfBirth.getText());
        String email = String.valueOf(textInputEditTextEmail.getText());
        String firstName = String.valueOf(textInputEditTextFirstName.getText());

        String lastName = String.valueOf(textInputEditTextLastName.getText());
        String lockedTime = user.getLockedTime();
        String password = MyUtil.hashedPassword(user.getPassword());
        String phone = String.valueOf(textInputEditTextPhone.getText());
        String roleName = user.getRoleName();
        boolean status = user.isStatus();
        String userName = user.getUserName();
        boolean flag = true;
        for (User userItem : userList) {
            if (userItem.getPhone().equals(user.getPhone()) || userItem.getEmail().equals(user.getEmail())) {
                continue;
            }
            if (userItem.getPhone().equals(phone)) {
                textInputLayoutPhone.setError(MyUtil.fieldExist(phone));
                flag = false;
                break;
            } else {
                textInputLayoutPhone.setError(null);
            }
            if (userItem.getEmail().equals(email)) {
                textInputLayoutEmail.setError(MyUtil.fieldExist(email));
                flag = false;
                break;
            } else {
                textInputLayoutEmail.setError(null);
            }
        }
        if (flag) {
            updateUser.setDateOfBirth(dateOfBirth);
            updateUser.setEmail(email);
            updateUser.setFirstName(firstName);
            updateUser.setLastName(lastName);
            updateUser.setLockedTime(lockedTime);
            updateUser.setPassword(password);
            updateUser.setPhone(phone);
            updateUser.setRoleName(roleName);
            updateUser.setStatus(status);
            updateUser.setUserName(userName);
            updateUser.setImageUrl(imageUrl);
            fireStoreHelper.changeInfo(user.getUserId(), updateUser, updateUserOnSuccessListener, updateUserOnFailureListener);
        }
    }

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

    private final OnSuccessListener<DocumentSnapshot> deleteOnSuccessListener = documentSnapshot -> {
        if (documentSnapshot.exists()) {
            String imageUrl = documentSnapshot.getString(Constant.IMAGE_URL);
            if (imageUrl != null) {
                if (!imageUrl.equals(Constant.IMAGE_URL)) {
                    firebaseStorageHelper.deleteImage(imageUrl);
                }
            }
        }
    };

    private final OnSuccessListener<DocumentSnapshot> onSuccessListener = documentSnapshot -> {
        if (documentSnapshot.exists()) {
            String imageUrl = documentSnapshot.getString(Constant.IMAGE_URL);
            updateUser(imageUrl);
        }
    };

    private final View.OnClickListener buttonFileChooserOnClickListener = view -> imagePickerLauncher.launch(Constant.MINE_IMAGE);
    private final View.OnClickListener onClickListener = view -> {
        if (imageUri != null) {
            if (checkEmptyField()) {
                String fileName = System.currentTimeMillis() + "." + getFileNameExtension(imageUri);
                firebaseStorageHelper.uploadImage(imageUri, fileName, onUploadListener);
            }
        } else {
            if (checkEmptyField()) {
                fireStoreHelper.getUrlAvatar(user.getUserId(), onSuccessListener);
            }
        }
    };

}