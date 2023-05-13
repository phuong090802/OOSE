package com.ute.myapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ute.myapp.R;
import com.ute.myapp.activity.SignInActivity;
import com.ute.myapp.activity.SignUpActivity;
import com.ute.myapp.constant.Constant;

public class Settings1Fragment extends Fragment {
    private Context context;
    TextView textViewLogin;
    TextView textViewRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_settings1, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        textViewLogin = view.findViewById(R.id.text_view_to_login);
        textViewRegister = view.findViewById(R.id.text_view_to_register);
        textViewRegister.setOnClickListener(textViewRegisterOnClickListener);
        textViewLogin.setOnClickListener(textViewLoginOnClickListener);
    }

    private final View.OnClickListener textViewLoginOnClickListener = view -> {
        Intent intent = new Intent(context, SignInActivity.class);
        startActivity(intent);
    };

    private final View.OnClickListener textViewRegisterOnClickListener = view -> {
        Intent intent = new Intent(context, SignUpActivity.class);
        intent.putExtra(Constant.REGISTER, true);
        startActivity(intent);
    };
}