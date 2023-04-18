package com.ute.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Stack;

public class ManageActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private final Stack<Fragment> fragmentStack = new Stack<>();
    private boolean isInitialize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        initializeView();
    }

    private void initializeView() {
        if (!isInitialize) {
            replaceFragment(new HomeAdminFragment());
            isInitialize = true;
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation_view_manage);
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
    }

    private final NavigationBarView.OnItemSelectedListener onItemSelectedListener = item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_home_manage) {
            replaceFragment(new HomeAdminFragment());
            return true;
        } else if (itemId == R.id.menu_settings_manage) {
            replaceFragment(new Settings1Fragment());
            return true;
        }
        return false;
    };

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_admin, fragment);
        fragmentStack.push(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(!fragmentStack.isEmpty()) {
            fragmentStack.pop();
        }
        else if(fragmentStack.empty()) {
            super.onBackPressed();
            return;
        }
        Fragment fragment = fragmentStack.peek();
        setChecked(fragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void setChecked(Fragment fragment) {
        if (fragment instanceof HomeAdminFragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_home_manage).setChecked(true);
        } else if (fragment instanceof Settings1Fragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_settings_manage).setChecked(true);
        }
    }
}