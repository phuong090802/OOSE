package com.ute.myapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ute.myapp.AccountManageFragment;
import com.ute.myapp.GenreManageFragment;
import com.ute.myapp.constant.Constant;

public class AdminAdapter extends FragmentStateAdapter {
    public AdminAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new AccountManageFragment() : new GenreManageFragment();
    }

    @Override
    public int getItemCount() {
        return Constant.NUMBERS_TAB_ADMIN;
    }
}
