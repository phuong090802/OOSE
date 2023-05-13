package com.ute.myapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ute.myapp.R;
import com.ute.myapp.adapter.GenreAdapter;
import com.ute.myapp.adapter.StoryAdapter;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.decoration.GenreItemDecoration;
import com.ute.myapp.decoration.GenreItemDecorationAdmin;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.Genre;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewGenre;
    private RecyclerView recyclerViewStory;
    private List<String> genreNameList;
    private FireStoreHelper fireStoreHelper;
    private List<Genre> genreList;
    private boolean isInitialize;
    private GenreAdapter genreAdapter;
    private Context context;
    private List<Map<String, Object>> mapListStory;
    private StoryAdapter storyAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        recyclerViewGenre = view.findViewById(R.id.recycler_view_genre);
        recyclerViewStory = view.findViewById(R.id.recycler_view_story);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewGenre.setLayoutManager(layoutManager);
        recyclerViewGenre.addItemDecoration(new GenreItemDecoration(10));
        recyclerViewGenre.setItemAnimator(new DefaultItemAnimator());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerViewStory.setLayoutManager(gridLayoutManager);
        recyclerViewStory.setItemAnimator(new DefaultItemAnimator());
        recyclerViewStory.addItemDecoration(new GenreItemDecorationAdmin(10));

        fireStoreHelper = FireStoreHelper.getInstance();
        if (!isInitialize) {
            mapListStory = new ArrayList<>();
            genreList = new ArrayList<>();
            genreNameList = new ArrayList<>();
            fireStoreHelper.getAllGenreActive(getAllGenreOnSuccessListener);
            fireStoreHelper.getAllStory(getAllStoryOnSuccessListener);
            isInitialize = true;
        }

    }

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
            setAdapterForRecycleViewStory();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(!genreNameList.isEmpty()) {
            setAdapterForRecycleViewGenre();
        }
        if(!mapListStory.isEmpty()) {
            setAdapterForRecycleViewStory();
        }
    }

    private final OnSuccessListener<QuerySnapshot> getAllGenreOnSuccessListener = queryDocumentSnapshots -> {
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                Genre genre = documentSnapshot.toObject(Genre.class);
                if (genre != null) {
                    genre.setGenreId(documentSnapshot.getId());
                    genreList.add(genre);
                }
            }
            genreList.sort(Comparator.comparing(Genre::getGenreName));
            genreNameList = genreList.stream().map(Genre::getGenreName).collect(Collectors.toList());
            setAdapterForRecycleViewGenre();
        }
    };

    private void setAdapterForRecycleViewStory() {
        storyAdapter = new StoryAdapter(context, mapListStory);
        recyclerViewStory.setAdapter(storyAdapter);

    }

    private void setAdapterForRecycleViewGenre() {
        genreAdapter = new GenreAdapter(context, genreNameList);
        recyclerViewGenre.setAdapter(genreAdapter);
    }

}