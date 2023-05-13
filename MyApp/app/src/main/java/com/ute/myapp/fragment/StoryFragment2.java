package com.ute.myapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.model.Chapter;

import java.lang.reflect.Type;


public class StoryFragment2 extends Fragment {
    TextView textViewName;
    TextView textViewContent;
    Chapter chapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story2, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        textViewName = view.findViewById(R.id.text_view_name);
        textViewContent = view.findViewById(R.id.text_view_content);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = (String) bundle.get(Constant.CHAPTER);
            if (json != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<Chapter>() {
                }.getType();
                chapter = gson.fromJson(json, type);
                textViewName.setText(chapter.getChapter());
                textViewContent.setText(chapter.getContent());
            }
        }
    }
}