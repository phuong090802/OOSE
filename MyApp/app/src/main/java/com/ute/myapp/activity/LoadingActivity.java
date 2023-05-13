package com.ute.myapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.model.User;
import com.ute.myapp.preferencehelper.PreferenceHelper;

public class LoadingActivity extends AppCompatActivity {
    private User user;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        initializeView();
    }

    private void initializeView() {
        preferenceHelper = PreferenceHelper.getInstance(this);
        Intent intentGetData = getIntent();
        user = (User) intentGetData.getSerializableExtra(Constant.USER);
        if (user != null) {
            showAlterDialog();
        }
    }

    private void showAlterDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.save_info)
                .setMessage(R.string.remember_account)
                .setPositiveButton(R.string.yes, positiveButtonOnClickListener)
                .setNegativeButton(R.string.no, negativeButtonOnClickListener)
                .setCancelable(true)
                .setOnCancelListener(onCancelListener);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private final DialogInterface.OnCancelListener onCancelListener = dialogInterface -> {
        preferenceHelper.clearCredential();
        commonOnClickListener();
    };

    private final DialogInterface.OnClickListener positiveButtonOnClickListener = (dialogInterface, i) -> {
        String userName = user.getUserName();
        String password = user.getPassword();
        preferenceHelper.saveCredential(userName, password);
        commonOnClickListener();
    };

    private final DialogInterface.OnClickListener negativeButtonOnClickListener = (dialogInterface, i) -> {
        preferenceHelper.clearCredential();
        commonOnClickListener();
    };

    private void commonOnClickListener() {
        String roleName = user.getRoleName();
        Intent intent = null;
        if (roleName.equalsIgnoreCase(Constant.ADMIN) || roleName.equalsIgnoreCase(Constant.CONTENT_MANAGER)) {
            intent = new Intent(this, ManageActivity.class);
        } else if (roleName.equalsIgnoreCase(Constant.AUTHOR) || roleName.equalsIgnoreCase(Constant.USER)) {
            intent = new Intent(this, AppActivity.class);
        }
        if (intent != null) {
            intent.putExtra(Constant.USER, user);
            startActivity(intent);
            finish();
        }
    }

}