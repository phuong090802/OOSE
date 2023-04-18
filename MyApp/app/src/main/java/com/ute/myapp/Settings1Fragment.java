package com.ute.myapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Settings1Fragment extends Fragment {
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings1, container, false);
        context = getContext();
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        TextView textViewLogin = view.findViewById(R.id.text_view_to_login);
        textViewLogin.setOnClickListener(onClickListener);
    }

    private final View.OnClickListener onClickListener = view -> {
        Intent intent = new Intent(context, SignInActivity.class);
        startActivity(intent);
    };
}