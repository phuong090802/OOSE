package com.ute.myapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ute.myapp.R;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    private final List<String> stringList;
    private final Context context;

    public GenreAdapter(Context context, List<String> stringList) {
        this.stringList = stringList;
        this.context = context;
    }

    @NonNull
    @Override
    public GenreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_custom_genre, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.ViewHolder holder, int position) {
        String genreName = stringList.get(position);
        holder.textViewGenreItem.setText(genreName);
    }

    @Override
    public int getItemCount() {
        return stringList == null ? 0 : stringList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewGenreItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGenreItem = itemView.findViewById(R.id.text_view_genre_item);
        }
    }
}
