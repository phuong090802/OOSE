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

import java.lang.reflect.Type;
import java.util.Map;

public class StoryFragment extends Fragment {
    TextView textViewName;
    TextView textViewContent;
    private Map<String, Object> mapStory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        textViewName = view.findViewById(R.id.text_view_name);
        textViewContent = view.findViewById(R.id.text_view_content);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = (String) bundle.get(Constant.MAP_STORY);
            if (json != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                mapStory = gson.fromJson(json, type);
                Double withChapter = (Double) mapStory.get(Constant.WITH_CHAPTER);
                if (withChapter != null) {
                    Long longValue = withChapter.longValue();
                    mapStory.put(Constant.WITH_CHAPTER, longValue);
                }
            }
        }
        Long withChapter = (Long) mapStory.get(Constant.WITH_CHAPTER);
        if (withChapter != null) {
            if (withChapter == 0) {
                String name = (String) mapStory.get(Constant.STORY_NAME);
                String content = (String) mapStory.get(Constant.CONTENT);
                textViewName.setText(name);
                textViewContent.setText(content);
            }
        }
    }
}