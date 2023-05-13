package com.ute.myapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ute.myapp.R;
import com.ute.myapp.adapter.ChapterAdapter;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.decoration.GenreItemDecorationAdmin;
import com.ute.myapp.event.ChapterOnClickListener;
import com.ute.myapp.event.SelectChapterListener;
import com.ute.myapp.model.Chapter;
import com.ute.myapp.util.MyUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StoryWithChapterFragment extends Fragment implements ChapterOnClickListener {
    TextView textViewName;
    RecyclerView recyclerView;
    private List<Chapter> chapterList;
    ChapterAdapter chapterAdapter;
    Map<String, Object> mapStory;
    private Context context;
    private SelectChapterListener selectChapterListener;
    private String storyId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SelectChapterListener) {
            selectChapterListener = (SelectChapterListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_story_with_chapter, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        textViewName = view.findViewById(R.id.text_view_name);
        recyclerView = view.findViewById(R.id.recycler_view_chapter);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = (String) bundle.get(Constant.MAP_STORY);
            if (json != null) {
                chapterList = new ArrayList<>();
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                mapStory = gson.fromJson(json, type);
                Double withChapter = (Double) mapStory.get(Constant.WITH_CHAPTER);
                if (withChapter != null) {
                    Long longValue = withChapter.longValue();
                    mapStory.put(Constant.WITH_CHAPTER, longValue);
                }
                String name = (String) mapStory.get(Constant.STORY_NAME);
                textViewName.setText(name);
                mapStory.forEach((key, value) -> {
                    if (key.startsWith("chapter")) {
                        String content = (String) value;
                        Chapter chapter = new Chapter(MyUtil.standardChapter(key), content);
                        chapterList.add(chapter);
                    }
                });
                storyId = (String) mapStory.get(Constant.STORY_ID);
                chapterList.sort((o1, o2) -> {
                    int chapter1 = Integer.parseInt(o1.getChapter().replaceAll("\\D+", ""));
                    int chapter2 = Integer.parseInt(o2.getChapter().replaceAll("\\D+", ""));
                    return chapter1 - chapter2;
                });
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.addItemDecoration(new GenreItemDecorationAdmin(22));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                chapterAdapter = new ChapterAdapter(context, chapterList, this);
                recyclerView.setAdapter(chapterAdapter);
            }
        }

    }

    @Override
    public void onclickChapter(Chapter chapter) {
        selectChapterListener.onReadChapterListener(chapter, storyId);
    }
}