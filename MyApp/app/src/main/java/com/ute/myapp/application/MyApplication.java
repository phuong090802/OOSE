package com.ute.myapp.application;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.ute.myapp.activity.AppActivity;
import com.ute.myapp.activity.ManageActivity;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.User;
import com.ute.myapp.preferencehelper.PreferenceHelper;
import com.ute.myapp.util.MyUtil;

import java.time.LocalDateTime;

public class MyApplication extends Application {
    PreferenceHelper preferenceHelper;
    FireStoreHelper fireStoreHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeView();
    }

    private void initializeView() {
        fireStoreHelper = FireStoreHelper.getInstance();
        preferenceHelper = PreferenceHelper.getInstance(getApplicationContext());
        boolean isLogin = preferenceHelper.isLoggedIn();
        User user = preferenceHelper.getUser();
        if (user != null && isLogin) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lockedTime = LocalDateTime.parse(user.getLockedTime());
            if (now.isAfter(lockedTime)) {
                if (!user.isStatus()) {
                    user.setStatus(true);
                    fireStoreHelper.updateStatusUser(user.getUserId());
                }
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
                }
            } else {
                Toast.makeText(this, MyUtil.showLockedTime(lockedTime), Toast.LENGTH_SHORT).show();
                openMainApp();
            }
        } else {
            openMainApp();
        }
    }

    private void openMainApp() {
        Intent intent = new Intent(this, AppActivity.class);
        startActivity(intent);
    }


}
