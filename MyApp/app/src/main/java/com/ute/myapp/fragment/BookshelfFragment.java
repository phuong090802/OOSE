package com.ute.myapp.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ute.myapp.R;

public class BookshelfFragment extends Fragment {
    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        context = getContext();
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
//        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_bookshelf);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
//        recyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.addItemDecoration(new GenreItemDecoration(22));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        GenreAdapter genreAdapter = new GenreAdapter(context, Database.genreList());
//        recyclerView.setAdapter(genreAdapter);
    }
}