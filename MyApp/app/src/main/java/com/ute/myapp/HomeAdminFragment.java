package com.ute.myapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ute.myapp.adapter.AdminAdapter;


public class HomeAdminFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_admin, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        AdminAdapter adminAdapter = new AdminAdapter(getChildFragmentManager(), getLifecycle());
        ViewPager2 viewPager2 = view.findViewById(R.id.view_page_2_home_admin);
        viewPager2.setAdapter(adminAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_home_admin);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.manage_account);
                    break;
                case 1:
                    tab.setText(R.string.manage_genre);
                    break;
            }
        }).attach();
    }
}