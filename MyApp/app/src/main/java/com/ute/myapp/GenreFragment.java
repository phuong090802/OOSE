package com.ute.myapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ute.myapp.adapter.GenreAdapter;
import com.ute.myapp.database.Database;
import com.ute.myapp.decoration.GenreItemDecoration;
import com.ute.myapp.model.Genre;

public class GenreFragment extends Fragment {
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);
        context = getContext();
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_genres);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GenreItemDecoration(22));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        GenreAdapter genreAdapter = new GenreAdapter(context, Database.genreList());
        recyclerView.setAdapter(genreAdapter);
    }
}