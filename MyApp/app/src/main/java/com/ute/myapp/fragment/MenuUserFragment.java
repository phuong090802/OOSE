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
import com.ute.myapp.event.SelectMenuListener;

public class MenuUserFragment extends Fragment {
    TextView textViewActiveUser;
    TextView textViewInactiveUser;
    private SelectMenuListener selectMenuListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SelectMenuListener) {
            selectMenuListener = (SelectMenuListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_user, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        textViewActiveUser = view.findViewById(R.id.text_view_active_user);
        textViewInactiveUser = view.findViewById(R.id.text_view_inactive_user);
        textViewActiveUser.setOnClickListener(textViewActiveUserOnClickListener);
        textViewInactiveUser.setOnClickListener(textViewInactiveUserOnClickListener);
    }

    private final View.OnClickListener textViewActiveUserOnClickListener = view -> selectMenuListener.onClickActiveUser();

    private final View.OnClickListener textViewInactiveUserOnClickListener = view -> selectMenuListener.onClickInactiveUser();
}