package com.ute.myapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ute.myapp.R;
import com.ute.myapp.adapter.ManageStoryAdapter;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.decoration.GenreItemDecorationAdmin;
import com.ute.myapp.event.EditStoryListener;
import com.ute.myapp.event.SelectStoryListener;
import com.ute.myapp.event.StoryOnClickListener;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.Genre;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StoryManageFragment extends Fragment implements StoryOnClickListener {
    private List<Map<String, Object>> mapListStory;
    private List<String> genreNameList;
    ManageStoryAdapter manageStoryAdapter;
    RecyclerView recyclerView;
    private Context context;
    private SelectStoryListener selectStoryListener;
    private EditStoryListener editStoryListener;
    private FireStoreHelper fireStoreHelper;
    SearchView searchView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SelectStoryListener) {
            selectStoryListener = (SelectStoryListener) context;
        }
        if (context instanceof EditStoryListener) {
            editStoryListener = (EditStoryListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_story_manage, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        fireStoreHelper = FireStoreHelper.getInstance();
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = (String) bundle.get(Constant.LIST_STORY);
            String jsonGenreList = (String) bundle.get(Constant.LIST_GENRE);
            if (json != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Map<String, Object>>>() {
                }.getType();
                Type typeGenreList = new TypeToken<List<Genre>>() {
                }.getType();
                mapListStory = gson.fromJson(json, type);
                mapListStory.forEach(story -> {
                    if (story.containsKey(Constant.WITH_CHAPTER) && story.get(Constant.WITH_CHAPTER) instanceof Double) {
                        Double doubleValue = (Double) story.get(Constant.WITH_CHAPTER);
                        if (doubleValue != null) {
                            Long longValue = doubleValue.longValue();
                            story.put(Constant.WITH_CHAPTER, longValue);
                        }
                    }
                });
                List<Genre> genreList = gson.fromJson(jsonGenreList, typeGenreList);
                genreNameList = genreList.stream().map(Genre::getGenreName).collect(Collectors.toList());
            }
        }
        searchView = view.findViewById(R.id.search_view_story_manage);
        searchView.setOnQueryTextListener(onQueryTextListener);
        recyclerView = view.findViewById(R.id.recycler_view_story);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GenreItemDecorationAdmin(22));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        manageStoryAdapter = new ManageStoryAdapter(context, mapListStory, genreNameList, this);
        manageStoryAdapter.setOnStoryClickListener(map -> editStoryListener.editStoryListener(map));
        recyclerView.setAdapter(manageStoryAdapter);
    }

    private final SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (!TextUtils.isEmpty(newText)) {
                List<Map<String, Object>> listFilterStory = mapListStory.stream()
                        .filter(story -> {
                            Log.e("newText", newText);
                            String genreName = (String) story.get(Constant.GENRE_NAME);
                            String storyName = (String) story.get(Constant.STORY_NAME);
                            return Objects.requireNonNull(genreName).contains(newText) || Objects.requireNonNull(storyName).contains(newText);
                        }).distinct()
                        .collect(Collectors.toList());
                manageStoryAdapter = new ManageStoryAdapter(context, listFilterStory, genreNameList, StoryManageFragment.this);
                manageStoryAdapter.setOnStoryClickListener(map -> editStoryListener.editStoryListener(map));
                recyclerView.setAdapter(manageStoryAdapter);
                return true;

            } else {
                fireStoreHelper.getAllStory(loadAllStoryOnSuccessListener);
                return false;
            }
        }
    };

    private final OnSuccessListener<QuerySnapshot> loadAllStoryOnSuccessListener = queryDocumentSnapshots -> {
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                Map<String, Object> data = documentSnapshot.getData();
                String storyId = documentSnapshot.getId();
                if (data != null) {
                    Map<String, Object> storyMap = new HashMap<>();

                    String genreName = (String) data.get(Constant.GENRE_NAME);
                    String authorName = (String) data.get(Constant.AUTHOR_NAME);
                    String storyName = (String) data.get(Constant.STORY_NAME);
                    Boolean status = (Boolean) data.get(Constant.STORY_STATUS);
                    Long withChapter = (Long) data.get(Constant.WITH_CHAPTER);
                    Boolean approve = (Boolean) data.get(Constant.APPROVE);
                    String imageUrl = (String) data.get(Constant.IMAGE_URL);
                    String userId = (String) data.get(Constant.USER_ID);

                    storyMap.put(Constant.STORY_ID, storyId);
                    storyMap.put(Constant.GENRE_NAME, genreName);
                    storyMap.put(Constant.AUTHOR_NAME, authorName);
                    storyMap.put(Constant.STORY_NAME, storyName);
                    storyMap.put(Constant.STORY_STATUS, status);
                    storyMap.put(Constant.WITH_CHAPTER, withChapter);
                    storyMap.put(Constant.APPROVE, approve);
                    storyMap.put(Constant.IMAGE_URL, imageUrl);
                    storyMap.put(Constant.USER_ID, userId);


                    if (withChapter != null) {
                        if (withChapter == 0) {
                            String content = (String) data.get(Constant.CONTENT);
                            storyMap.put(Constant.CONTENT, content);
                        } else {
                            data.forEach((key, value) -> {
                                if (key.startsWith("chapter")) {
                                    String content = (String) value;
                                    storyMap.put(key, content);
                                }
                            });
                        }
                    }
                    mapList.add(storyMap);
                }
            }
            manageStoryAdapter.updateStoryList(mapList);
        }
    };


    private final OnSuccessListener<QuerySnapshot> getAllStoryOnSuccessListener = queryDocumentSnapshots -> {
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                Map<String, Object> data = documentSnapshot.getData();
                String storyId = documentSnapshot.getId();
                if (data != null) {
                    Map<String, Object> storyMap = new HashMap<>();

                    String genreName = (String) data.get(Constant.GENRE_NAME);
                    String authorName = (String) data.get(Constant.AUTHOR_NAME);
                    String storyName = (String) data.get(Constant.STORY_NAME);
                    Boolean status = (Boolean) data.get(Constant.STORY_STATUS);
                    Long withChapter = (Long) data.get(Constant.WITH_CHAPTER);
                    Boolean approve = (Boolean) data.get(Constant.APPROVE);
                    String imageUrl = (String) data.get(Constant.IMAGE_URL);
                    String userId = (String) data.get(Constant.USER_ID);

                    storyMap.put(Constant.STORY_ID, storyId);
                    storyMap.put(Constant.GENRE_NAME, genreName);
                    storyMap.put(Constant.AUTHOR_NAME, authorName);
                    storyMap.put(Constant.STORY_NAME, storyName);
                    storyMap.put(Constant.STORY_STATUS, status);
                    storyMap.put(Constant.WITH_CHAPTER, withChapter);
                    storyMap.put(Constant.APPROVE, approve);
                    storyMap.put(Constant.IMAGE_URL, imageUrl);
                    storyMap.put(Constant.USER_ID, userId);


                    if (withChapter != null) {
                        if (withChapter == 0) {
                            String content = (String) data.get(Constant.CONTENT);
                            storyMap.put(Constant.CONTENT, content);
                        } else {
                            data.forEach((key, value) -> {
                                if (key.startsWith("chapter")) {
                                    String content = (String) value;
                                    storyMap.put(key, content);
                                }
                            });
                        }
                    }
                    mapListStory.add(storyMap);
                }
            }
        }
    };


    @Override
    public void onResume() {
        fireStoreHelper.getAllStory(getAllStoryOnSuccessListener);
        super.onResume();

    }

    @Override
    public void onclickStory(Map<String, Object> map) {
        selectStoryListener.onSelectStoryListener(map);
    }

}