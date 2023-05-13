package com.ute.myapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;

import java.io.File;
import java.util.List;
import java.util.Map;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
    private final List<Map<String, Object>> mapListStory;
    private final Context context;

    public StoryAdapter(Context context, List<Map<String, Object>> mapListStory) {
        this.mapListStory = mapListStory;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_custom_story, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryAdapter.ViewHolder holder, int position) {
        Map<String, Object> mapStory = mapListStory.get(position);
        String genreName = (String) mapStory.get(Constant.GENRE_NAME);
        String storyName = (String) mapStory.get(Constant.STORY_NAME);
        String imageUrl = (String) mapStory.get(Constant.IMAGE_URL);
        holder.textViewStoryName.setText(storyName);
        holder.textViewGenreName.setText(genreName);
        File cacheDir = context.getCacheDir();
        new File(cacheDir, "story-image-picasso-cache");
        Picasso.get().load(imageUrl).into(holder.shapeableImageView);
    }

    @Override
    public int getItemCount() {
        return mapListStory == null ? 0 : mapListStory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView shapeableImageView;
        private final TextView textViewStoryName;
        private final TextView textViewGenreName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shapeableImageView = itemView.findViewById(R.id.shape_able_image_view_item);
            textViewStoryName = itemView.findViewById(R.id.text_view_story_name);
            textViewGenreName = itemView.findViewById(R.id.text_view_genre_name);
        }
    }
}
