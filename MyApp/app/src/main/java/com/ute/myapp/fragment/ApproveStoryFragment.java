package com.ute.myapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ute.myapp.R;
import com.ute.myapp.adapter.ManageNewStoryAdapter;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.decoration.GenreItemDecorationAdmin;
import com.ute.myapp.event.NewStoryOnClickListener;
import com.ute.myapp.event.SelectNewStoryListener;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


public class ApproveStoryFragment extends Fragment implements NewStoryOnClickListener {
    private List<Map<String, Object>> mapListNewStory;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private Context context;
    private ManageNewStoryAdapter manageNewStoryAdapter;
    private SelectNewStoryListener selectNewStoryListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof SelectNewStoryListener) {
            selectNewStoryListener = (SelectNewStoryListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_approve_story, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = (String) bundle.get(Constant.LIST_STORY);
            if (json != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Map<String, Object>>>() {
                }.getType();
                mapListNewStory = gson.fromJson(json, type);
                mapListNewStory.forEach(story -> {
                    if (story.containsKey(Constant.WITH_CHAPTER) && story.get(Constant.WITH_CHAPTER) instanceof Double) {
                        Double doubleValue = (Double) story.get(Constant.WITH_CHAPTER);
                        if (doubleValue != null) {
                            Long longValue = doubleValue.longValue();
                            story.put(Constant.WITH_CHAPTER, longValue);
                        }
                    }
                });
            }
        }
        searchView = view.findViewById(R.id.search_view_approve_story);
        recyclerView = view.findViewById(R.id.recycler_view_approve_story);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GenreItemDecorationAdmin(22));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        manageNewStoryAdapter = new ManageNewStoryAdapter(context, mapListNewStory, this);
        recyclerView.setAdapter(manageNewStoryAdapter);
    }

    @Override
    public void onclickStory(Map<String, Object> map) {
        selectNewStoryListener.onSelectNewStoryListener(map);
    }
}