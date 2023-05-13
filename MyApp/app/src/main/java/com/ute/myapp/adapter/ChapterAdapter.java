package com.ute.myapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ute.myapp.R;
import com.ute.myapp.event.ChapterOnClickListener;
import com.ute.myapp.model.Chapter;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {
    private final List<Chapter> chapterList;
    private final Context context;
    private final ChapterOnClickListener chapterOnClickListener;

    public ChapterAdapter(Context context, List<Chapter> chapterList, ChapterOnClickListener chapterOnClickListener) {
        this.chapterList = chapterList;
        this.context = context;
        this.chapterOnClickListener = chapterOnClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateGenreList(List<Chapter> chapterList){
        this.chapterList.clear();
        this.chapterList.addAll(chapterList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChapterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_custom_chapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterAdapter.ViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.textViewChapter.setText(chapter.getChapter());
        holder.itemView.setOnClickListener(view -> chapterOnClickListener.onclickChapter(chapter));
    }

    @Override
    public int getItemCount() {
        return chapterList == null ? 0 : chapterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewChapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewChapter = itemView.findViewById(R.id.text_view_chapter);
        }
    }
}
