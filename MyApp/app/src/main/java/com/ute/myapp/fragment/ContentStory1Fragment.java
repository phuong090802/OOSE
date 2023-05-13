package com.ute.myapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.firebasehelper.FireStoreHelper;

import java.lang.reflect.Type;
import java.util.Map;


public class ContentStory1Fragment extends Fragment {
    private Map<String, Object> mapStory;
    TextView textViewName;
    private TextView textViewContent;
    Button button;
    private Context context;
    private TextInputEditText textInputEditTextChapterContent;
    private FireStoreHelper fireStoreHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_content_story1, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        fireStoreHelper = FireStoreHelper.getInstance();
        textViewName = view.findViewById(R.id.text_view_name);
        textViewContent = view.findViewById(R.id.text_view_content);
        button = view.findViewById(R.id.button_edit);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = (String) bundle.get(Constant.MAP_STORY);
            if (json != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                mapStory = gson.fromJson(json, type);
                Double withChapter = (Double) mapStory.get(Constant.WITH_CHAPTER);
                if(withChapter != null) {
                    Long longValue = withChapter.longValue();
                    mapStory.put(Constant.WITH_CHAPTER, longValue);
                }
            }
        }
        Long withChapter = (Long) mapStory.get(Constant.WITH_CHAPTER);
        if (withChapter != null) {
            if (withChapter == 0) {
                String name = (String) mapStory.get(Constant.STORY_NAME);
                String content = (String) mapStory.get(Constant.CONTENT);
                textViewName.setText(name);
                textViewContent.setText(content);
            }
        }
        button.setOnClickListener(onClickListener);
    }


    private final ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }
    };

    private final OnSuccessListener<Void> updateStoryOnSuccessListener = unused -> Toast.makeText(context, R.string.edit_successful, Toast.LENGTH_SHORT).show();
    private final OnFailureListener updateStoryOnFailureListener = e -> Toast.makeText(context, R.string.edit_failure, Toast.LENGTH_SHORT).show();


    private final View.OnClickListener onClickListener = view -> {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        ScrollView linearLayout = new ScrollView(context);
        builder.setTitle(R.string.add_chapter);
        TextInputLayout textInputLayoutChapterContent = new TextInputLayout(context);
        textInputEditTextChapterContent = new TextInputEditText(context);
        String content = (String) mapStory.get(Constant.CONTENT);
        textInputEditTextChapterContent.setText(content);
        textInputEditTextChapterContent.setCustomInsertionActionModeCallback(callback);
        textInputEditTextChapterContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        textInputEditTextChapterContent.setMaxLines(Integer.MAX_VALUE);
        textInputEditTextChapterContent.setHorizontallyScrolling(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG);
        textInputLayoutChapterContent.setHint(R.string.chapter_number);
        textInputLayoutChapterContent.addView(textInputEditTextChapterContent);
        linearLayout.addView(textInputLayoutChapterContent);

        builder.setView(textInputLayoutChapterContent);
        builder.setPositiveButton(R.string.edit_content, (dialog, which) -> {
            String newContent = String.valueOf(textInputEditTextChapterContent.getText());
            if (!TextUtils.isEmpty(newContent)) {
                String storyId = (String) mapStory.get(Constant.STORY_ID);
                Map<String, Object> updateMapStory = mapStory;
                updateMapStory.remove(Constant.STORY_ID);
                updateMapStory.put(Constant.CONTENT, newContent);
                textViewContent.setText(newContent);
                fireStoreHelper.updateStory(storyId, updateMapStory, updateStoryOnSuccessListener, updateStoryOnFailureListener);
            } else {
                Toast.makeText(context, R.string.require_add_chapter, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.abort, (dialog, which) -> dialog.dismiss());
        builder.setView(linearLayout);
        AlertDialog dialog = builder.create();
        dialog.show();
    };


}