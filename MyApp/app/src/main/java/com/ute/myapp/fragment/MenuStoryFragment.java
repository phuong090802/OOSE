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

public class MenuStoryFragment extends Fragment {
    TextView textViewAddStory;
    TextView textViewSearchStory;
    TextView textViewNewStory;
    TextView textViewReportedStory;
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
        View view = inflater.inflate(R.layout.fragment_menu_story, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        textViewAddStory = view.findViewById(R.id.text_view_add_story);
        textViewSearchStory = view.findViewById(R.id.text_view_search_story);
        textViewNewStory = view.findViewById(R.id.text_view_new_story);
        textViewReportedStory = view.findViewById(R.id.text_view_reported_story);
        textViewAddStory.setOnClickListener(textViewAddStoryOnClickListener);
        textViewSearchStory.setOnClickListener(textViewSearchStoryOnClickListener);
        textViewNewStory.setOnClickListener(textViewNewStoryOnClickListener);
        textViewReportedStory.setOnClickListener(textViewReportedStoryOnClickListener);
    }


    private final View.OnClickListener textViewAddStoryOnClickListener = view -> selectMenuListener.onClickAddStory();
    private final View.OnClickListener textViewSearchStoryOnClickListener = view -> selectMenuListener.onClickSearchStory();
    private final View.OnClickListener textViewNewStoryOnClickListener = view -> selectMenuListener.onClickNewStory();
    private final View.OnClickListener textViewReportedStoryOnClickListener = view -> selectMenuListener.onClickReportedStory();
}