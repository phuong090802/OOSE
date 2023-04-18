package com.ute.myapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Stack;

public class AppActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final Stack<Fragment> fragmentStack = new Stack<>();
    private boolean isInitialize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        initializeView();
    }

    private void initializeView() {
        if (!isInitialize) {
            replaceFragment(new HomeFragment());
            isInitialize = true;
        }
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
    }

    private final NavigationBarView.OnItemSelectedListener onItemSelectedListener = item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_home) {
            replaceFragment(new HomeFragment());
            return true;
        } else if (itemId == R.id.menu_search) {
            replaceFragment(new SearchFragment());
            return true;
        } else if (itemId == R.id.menu_bookshelf) {
            replaceFragment(new BookshelfFragment());
            return true;
        } else if (itemId == R.id.menu_settings) {
            replaceFragment(new Settings2Fragment());
            return true;
        }
        return false;
    };

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
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
        if (fragment instanceof HomeFragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
        } else if (fragment instanceof SearchFragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_search).setChecked(true);
        } else if (fragment instanceof BookshelfFragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_bookshelf).setChecked(true);
        } else if (fragment instanceof Settings2Fragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_settings).setChecked(true);
        }
    }
}