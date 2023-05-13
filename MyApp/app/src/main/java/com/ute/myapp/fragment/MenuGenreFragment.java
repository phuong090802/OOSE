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

public class MenuGenreFragment extends Fragment {

    TextView textViewActiveGenre;
    TextView textViewInactiveGenre;
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
        View view = inflater.inflate(R.layout.fragment_menu_genre, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        textViewActiveGenre = view.findViewById(R.id.text_view_active_genre);
        textViewInactiveGenre = view.findViewById(R.id.text_view_inactive_genre);
        textViewActiveGenre.setOnClickListener(textViewActiveGenreOnClickListener);
        textViewInactiveGenre.setOnClickListener(textViewInactiveGenreOnClickListener);
    }

    private final View.OnClickListener textViewActiveGenreOnClickListener = view -> selectMenuListener.onClickActiveGenre();

    private final View.OnClickListener textViewInactiveGenreOnClickListener = view -> selectMenuListener.onClickInactiveGenre();

}