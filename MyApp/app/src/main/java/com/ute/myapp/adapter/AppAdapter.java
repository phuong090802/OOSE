package com.ute.myapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ute.myapp.GenreFragment;
import com.ute.myapp.StoryFragment;
import com.ute.myapp.constant.Constant;


public class AppAdapter extends FragmentStateAdapter {

    public AppAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 1 ? new GenreFragment() : new StoryFragment();
    }

    @Override
    public int getItemCount() {
        return Constant.NUMBERS_TAB_APP;
    }
}
