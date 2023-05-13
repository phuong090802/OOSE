package com.ute.myapp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.event.OnComposeClickListener;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.User;
import com.ute.myapp.preferencehelper.PreferenceHelper;


public class ComposeFragment extends Fragment {
    private TextView textViewCompose;
    private TextView textViewYourStory;
    private User user;
    private Context context;
    private PreferenceHelper preferenceHelper;
    private FireStoreHelper fireStoreHelper;
    private OnComposeClickListener onComposeClickListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof OnComposeClickListener) {
            onComposeClickListener = (OnComposeClickListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_compose, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        fireStoreHelper = FireStoreHelper.getInstance();
        preferenceHelper = PreferenceHelper.getInstance(context);
        user = preferenceHelper.getUser();
        textViewCompose = view.findViewById(R.id.text_view_begin_compose);
        textViewYourStory = view.findViewById(R.id.text_view_your_story);
        textViewCompose.setOnClickListener(textViewComposeOnClickListener);
        textViewYourStory.setOnClickListener(textViewYourStoryOnClickListener);
    }

    private final View.OnClickListener textViewComposeOnClickListener = view -> {
        if(user.getRoleName().equals(Constant.USER)) {
            fireStoreHelper.updateRoleAuthor(user.getUserId());
        }
        onComposeClickListener.onClickCompose();
    };

    private final View.OnClickListener textViewYourStoryOnClickListener = view -> onComposeClickListener.onClickYourStory();
}