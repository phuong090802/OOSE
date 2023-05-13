package com.ute.myapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;
import com.ute.myapp.R;
import com.ute.myapp.activity.AppActivity;
import com.ute.myapp.activity.ChangeInfoActivity;
import com.ute.myapp.activity.ChangePasswordActivity;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.event.ComposeOnClickListener;
import com.ute.myapp.model.User;
import com.ute.myapp.preferencehelper.PreferenceHelper;

import java.io.File;

public class Settings3Fragment extends Fragment {
    ShapeableImageView imageViewUserSettings;
    TextView textViewNameSettings;
    private User user;
    TextView textViewToLogout;
    private Context context;
    private PreferenceHelper preferenceHelper;
    TextView textViewChangePassword;
    TextView textViewEditInfo;
    TextView textViewCompose;
    private ComposeOnClickListener composeOnClickListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ComposeOnClickListener) {
            composeOnClickListener = (ComposeOnClickListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_settings3, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        preferenceHelper = PreferenceHelper.getInstance(context);
        imageViewUserSettings = view.findViewById(R.id.image_view_user_settings);
        textViewNameSettings = view.findViewById(R.id.text_view_name_settings);
        textViewChangePassword = view.findViewById(R.id.text_view_change_password);
        textViewEditInfo = view.findViewById(R.id.text_view_to_edit_info);
        textViewCompose = view.findViewById(R.id.text_view_compose);
        Bundle bundle = getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable(Constant.USER);
            if (user != null) {
                File imageFile = new File(user.getImageUrl());
                if (imageFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    imageViewUserSettings.setImageBitmap(bitmap);
                }
                String name = user.getLastName() + " " + user.getFirstName();
                textViewNameSettings.setText(name);

            }
        }
        textViewToLogout = view.findViewById(R.id.text_view_to_log_out);
        textViewToLogout.setOnClickListener(textViewToLogoutOnClickListener);
        textViewChangePassword.setOnClickListener(textViewChangePasswordOnClickListener);
        textViewEditInfo.setOnClickListener(textViewEditInfoOnClickListener);
        textViewCompose.setOnClickListener(textViewComposeOnClickListener);

    }

    private final View.OnClickListener textViewToLogoutOnClickListener = view -> {
        preferenceHelper.setLoggedIn(false);
        Intent intent = new Intent(getActivity(), AppActivity.class);
        context.startActivity(intent);
    };

    private final View.OnClickListener textViewChangePasswordOnClickListener = view -> {
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        intent.putExtra(Constant.USER, user);
        context.startActivity(intent);
    };

    private final View.OnClickListener textViewEditInfoOnClickListener = view -> {
        Intent intent = new Intent(getActivity(), ChangeInfoActivity.class);
        intent.putExtra(Constant.USER, user);
        context.startActivity(intent);
    };

    private final View.OnClickListener textViewComposeOnClickListener = view -> composeOnClickListener.onComposeOnClick(user);
}