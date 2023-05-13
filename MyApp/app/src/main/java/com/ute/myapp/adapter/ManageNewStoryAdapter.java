package com.ute.myapp.adapter;

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
import com.ute.myapp.event.NewStoryOnClickListener;
import com.ute.myapp.firebasehelper.FireStoreHelper;

import java.util.List;
import java.util.Map;

public class ManageNewStoryAdapter extends RecyclerView.Adapter<ManageNewStoryAdapter.ViewHolder> {
    private final List<Map<String, Object>> mapListStory;
    private Context context;
    private final NewStoryOnClickListener newStoryOnClickListener;
    private final FireStoreHelper fireStoreHelper;


    public ManageNewStoryAdapter(Context context, List<Map<String, Object>> mapListStory, NewStoryOnClickListener newStoryOnClickListener) {
        this.mapListStory = mapListStory;
        this.context = context;
        this.newStoryOnClickListener = newStoryOnClickListener;
        fireStoreHelper = FireStoreHelper.getInstance();
    }


    @NonNull
    @Override
    public ManageNewStoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_custom_new_story_manage, parent, false);
        return new ViewHolder(view);
    }

    private final OnSuccessListener<Void> deleteStoryOnSuccessListener = unused -> Toast.makeText(context, R.string.delete_successful, Toast.LENGTH_SHORT).show();
    private final OnFailureListener deleteStoryOnFailureListener = e -> Toast.makeText(context, R.string.delete_failure, Toast.LENGTH_SHORT).show();


    @Override
    public void onBindViewHolder(@NonNull ManageNewStoryAdapter.ViewHolder holder, int position) {
        Map<String, Object> mapStory = mapListStory.get(position);
        String storyId = (String) mapStory.get(Constant.STORY_ID);
        String genreName = (String) mapStory.get(Constant.GENRE_NAME);
        String storyName = (String) mapStory.get(Constant.STORY_NAME);
        String authorName = (String) mapStory.get(Constant.AUTHOR_NAME);
        Long withChapter = (Long) mapStory.get(Constant.WITH_CHAPTER);
        holder.textViewGenreName.setText(genreName);
        holder.textViewStoryName.setText(storyName);
        holder.textViewAuthorName.setText(authorName);
        holder.buttonDelete.setOnClickListener(view -> fireStoreHelper.deleteStory(storyId, deleteStoryOnSuccessListener, deleteStoryOnFailureListener));
        holder.buttonApprove.setOnClickListener(view -> fireStoreHelper.updateApproveStory(storyId));
        if (withChapter != null) {
            String chapter = withChapter != 0 ? Constant.STORY_WITH_CHAPTER : Constant.STORY_WITHOUT_CHAPTER;
            holder.textViewWithChapter.setText(chapter);
        }
        holder.itemView.setOnClickListener(view -> newStoryOnClickListener.onclickStory(mapStory));

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
        private final Button buttonApprove;
        private final Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStoryName = itemView.findViewById(R.id.text_view_story_name);
            textViewGenreName = itemView.findViewById(R.id.text_view_genre_name);
            textViewAuthorName = itemView.findViewById(R.id.text_view_author_name);
            textViewWithChapter = itemView.findViewById(R.id.text_view_with_chapter);
            buttonApprove = itemView.findViewById(R.id.button_approve_story);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }
}
