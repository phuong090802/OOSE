package com.ute.myapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ute.myapp.adapter.AppAdapter;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        AppAdapter appAdapter = new AppAdapter(getChildFragmentManager(), getLifecycle());

        ViewPager2 viewPager2 = view.findViewById(R.id.view_page_2_home);
        viewPager2.setAdapter(appAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout_home);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.update);
                    break;
                case 1:
                    tab.setText(R.string.genre);
                    break;
                case 2:
                    tab.setText(R.string.enough_content);
                    break;
                case 3:
                    tab.setText(R.string.composed);
                    break;
                case 4:
                    tab.setText(R.string.your);
                    break;
            }
        }).attach();
    }

}