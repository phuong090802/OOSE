package com.ute.myapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ute.myapp.R;
import com.ute.myapp.event.SelectMenuListener;


public class AdminFragment extends Fragment {

    TextView textViewAccountManagement;
    TextView textViewGenreManagement;
    TextView textViewStoryManagement;
    TextView textViewCommentManagement;
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
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        textViewAccountManagement = view.findViewById(R.id.text_view_management_account_admin);
        textViewGenreManagement = view.findViewById(R.id.text_view_management_genre_admin);
        textViewStoryManagement = view.findViewById(R.id.text_view_management_store_admin);
        textViewCommentManagement = view.findViewById(R.id.text_view_management_comment_admin);

        textViewAccountManagement.setOnClickListener(textViewAccountManagementOnClickListener);
        textViewGenreManagement.setOnClickListener(textViewGenreManagementOnClickListener);
        textViewStoryManagement.setOnClickListener(textViewStoryManagementOnClickListener);
        textViewCommentManagement.setOnClickListener(textViewCommentManagementOnClickListener);
    }

    private final View.OnClickListener textViewAccountManagementOnClickListener = view -> selectMenuListener.onClickUser();

    private final View.OnClickListener textViewGenreManagementOnClickListener = view -> selectMenuListener.onClickGenre();
    private final View.OnClickListener textViewStoryManagementOnClickListener = view -> selectMenuListener.onClickStory();

    private final View.OnClickListener textViewCommentManagementOnClickListener = view -> {

    };

}