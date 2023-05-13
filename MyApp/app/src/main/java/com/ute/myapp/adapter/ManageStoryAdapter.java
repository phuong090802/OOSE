package com.ute.myapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.event.OnStoryClickListener;
import com.ute.myapp.event.StoryOnClickListener;
import com.ute.myapp.firebasehelper.FireStoreHelper;

import java.util.List;
import java.util.Map;

public class ManageStoryAdapter extends RecyclerView.Adapter<ManageStoryAdapter.ViewHolder> {
    private final List<Map<String, Object>> mapListStory;
    List<String> genreNameList;
    private Context context;
    private final StoryOnClickListener storyOnClickListener;
    private final FireStoreHelper fireStoreHelper;

    private OnStoryClickListener onStoryClickListener;

    public void setOnStoryClickListener(OnStoryClickListener onStoryClickListener) {
        this.onStoryClickListener = onStoryClickListener;
    }

    public ManageStoryAdapter(Context context, List<Map<String, Object>> mapListStory, List<String> genreNameList, StoryOnClickListener storyOnClickListener) {
        this.mapListStory = mapListStory;
        this.genreNameList = genreNameList;
        this.context = context;
        fireStoreHelper = FireStoreHelper.getInstance();
        this.storyOnClickListener = storyOnClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateStoryList(List<Map<String, Object>> mapListStory) {
        this.mapListStory.clear();
        this.mapListStory.addAll(mapListStory);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ManageStoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_custom_story_manage, parent, false);
        return new ViewHolder(view);
    }

    private final OnSuccessListener<Void> deleteStoryOnSuccessListener = unused -> Toast.makeText(context, R.string.delete_successful, Toast.LENGTH_SHORT).show();
    private final OnFailureListener deleteStoryOnFailureListener = e -> Toast.makeText(context, R.string.delete_failure, Toast.LENGTH_SHORT).show();

    @Override
    public void onBindViewHolder(@NonNull ManageStoryAdapter.ViewHolder holder, int position) {
        Map<String, Object> mapStory = mapListStory.get(position);
        String storyId = (String) mapStory.get(Constant.STORY_ID);
        String genreName = (String) mapStory.get(Constant.GENRE_NAME);
        String storyName = (String) mapStory.get(Constant.STORY_NAME);
        String authorName = (String) mapStory.get(Constant.AUTHOR_NAME);
        Long withChapter = (Long) mapStory.get(Constant.WITH_CHAPTER);
        Boolean status = (Boolean) mapStory.get(Constant.STORY_STATUS);
        holder.textViewGenreName.setText(genreName);
        holder.textViewStoryName.setText(storyName);
        holder.textViewAuthorName.setText(authorName);
        holder.buttonEdit.setOnClickListener(view -> {
            if (onStoryClickListener != null) {
                onStoryClickListener.onEditItemClick(mapStory);
            }
        });
        holder.buttonDelete.setOnClickListener(view -> fireStoreHelper.deleteStory(storyId, deleteStoryOnSuccessListener, deleteStoryOnFailureListener));
        if (withChapter != null) {
            String chapter = withChapter != 0 ? Constant.STORY_WITH_CHAPTER : Constant.STORY_WITHOUT_CHAPTER;
            holder.textViewWithChapter.setText(chapter);
        }
        if (status != null) {
            String statusStory = status ? Constant.ACTIVE : Constant.INACTIVE;
            holder.textViewStatus.setText(statusStory);
        }
        holder.itemView.setOnClickListener(view -> storyOnClickListener.onclickStory(mapStory));
    }


    @Override
    public int getItemCount() {
        return mapListStory == null ? 0 : mapListStory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewStoryName;
        private final TextView textViewGenreName;
        private final TextView textViewAuthorName;
        private final TextView textViewWithChapter;
        private final TextView textViewStatus;
        private final Button buttonEdit;
        private final Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStoryName = itemView.findViewById(R.id.text_view_story_name);
            textViewGenreName = itemView.findViewById(R.id.text_view_genre_name);
            textViewAuthorName = itemView.findViewById(R.id.text_view_author_name);
            textViewWithChapter = itemView.findViewById(R.id.text_view_with_chapter);
            textViewStatus = itemView.findViewById(R.id.text_view_status);
            buttonEdit = itemView.findViewById(R.id.button_edit_story);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }
}
