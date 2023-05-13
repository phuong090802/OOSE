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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ute.myapp.R;
import com.ute.myapp.adapter.ChapterAdapter;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.decoration.GenreItemDecorationAdmin;
import com.ute.myapp.event.ChapterOnClickListener;
import com.ute.myapp.event.SelectChapterListener;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.Chapter;
import com.ute.myapp.util.MyUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ChapterManageFragment extends Fragment implements ChapterOnClickListener {
    TextView textViewName;
    private List<Chapter> chapterList;
    RecyclerView recyclerView;
    ChapterAdapter chapterAdapter;
    GridLayoutManager gridLayoutManager;
    private Context context;
    private SelectChapterListener selectChapterListener;
    private String storyId;
    FloatingActionButton floatingActionButton;
    private Map<String, Object> mapStory;
    private TextInputEditText textInputEditTextChapterContent;
    private FireStoreHelper fireStoreHelper;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SelectChapterListener) {
            selectChapterListener = (SelectChapterListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_chapter_manage, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        fireStoreHelper = FireStoreHelper.getInstance();
        textViewName = view.findViewById(R.id.text_view_name);
        recyclerView = view.findViewById(R.id.recycler_view_chapter);
        floatingActionButton = view.findViewById(R.id.floating_action_button_add_chapter);
        floatingActionButton.setOnClickListener(onClickListener);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = (String) bundle.get(Constant.MAP_STORY);
            if (json != null) {
                chapterList = new ArrayList<>();
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                mapStory = gson.fromJson(json, type);
                Double withChapter = (Double) mapStory.get(Constant.WITH_CHAPTER);
                if (withChapter != null) {
                    Long longValue = withChapter.longValue();
                    mapStory.put(Constant.WITH_CHAPTER, longValue);
                }
                String name = (String) mapStory.get(Constant.STORY_NAME);
                textViewName.setText(name);
                mapStory.forEach((key, value) -> {
                    if (key.startsWith("chapter")) {
                        String content = (String) value;
                        Chapter chapter = new Chapter(MyUtil.standardChapter(key), content);
                        chapterList.add(chapter);
                    }
                });
                storyId = (String) mapStory.get(Constant.STORY_ID);
                chapterList.sort((o1, o2) -> {
                    int chapter1 = Integer.parseInt(o1.getChapter().replaceAll("\\D+", ""));
                    int chapter2 = Integer.parseInt(o2.getChapter().replaceAll("\\D+", ""));
                    return chapter1 - chapter2;
                });
                gridLayoutManager = new GridLayoutManager(context, 1);
                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.addItemDecoration(new GenreItemDecorationAdmin(22));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                chapterAdapter = new ChapterAdapter(context, chapterList, this);
                recyclerView.setAdapter(chapterAdapter);
            }
        }

    }

    private final OnSuccessListener<Void> updateStoryOnSuccessListener = unused -> Toast.makeText(context, R.string.edit_successful, Toast.LENGTH_SHORT).show();
    private final OnFailureListener updateStoryOnFailureListener = e -> Toast.makeText(context, R.string.edit_failure, Toast.LENGTH_SHORT).show();

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

    private final OnCompleteListener<DocumentSnapshot> onCompleteListener = task -> {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                Map<String, Object> map = document.getData();
                if (map != null) {
                    List<Chapter> chapters = new ArrayList<>();
                    map.forEach((key, value) -> {
                        if (key.startsWith("chapter")) {
                            String content = (String) value;
                            Chapter chapter = new Chapter(MyUtil.standardChapter(key), content);
                            chapters.add(chapter);
                        }

                    });
                        map.put(Constant.STORY_ID, document.getId());
                        chapters.sort((o1, o2) -> {
                            int chapter1 = Integer.parseInt(o1.getChapter().replaceAll("\\D+", ""));
                            int chapter2 = Integer.parseInt(o2.getChapter().replaceAll("\\D+", ""));
                            return chapter1 - chapter2;
                        });
                        mapStory = map;
                        chapterAdapter.updateGenreList(chapters);
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        fireStoreHelper.getStoryWithId(storyId, onCompleteListener);
    }

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
        builder.setPositiveButton(R.string.add_content_chapter, (dialog, which) -> {
            String newContent = String.valueOf(textInputEditTextChapterContent.getText());
            if (!TextUtils.isEmpty(newContent)) {
                String storyId = (String) mapStory.get(Constant.STORY_ID);
                Map<String, Object> updateMapStory = mapStory;
                updateMapStory.remove(Constant.STORY_ID);
                Long withChapter = (Long) mapStory.get(Constant.WITH_CHAPTER);
                if (withChapter != null) {
                    Long newWithChapter = withChapter + 1;
                    String key = "chapter" + newWithChapter;
                    updateMapStory.put(key, newContent);
                    updateMapStory.put(Constant.WITH_CHAPTER, newWithChapter);
                    fireStoreHelper.updateStory(storyId, updateMapStory, updateStoryOnSuccessListener, updateStoryOnFailureListener);
                    fireStoreHelper.getStoryWithId(storyId, onCompleteListener);
                }

            } else {
                Toast.makeText(context, R.string.require_add_chapter, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.abort, (dialog, which) -> dialog.dismiss());
        builder.setView(linearLayout);
        AlertDialog dialog = builder.create();
        dialog.show();
    };

    @Override
    public void onclickChapter(Chapter chapter) {
        selectChapterListener.onSelectChapterListener(chapter, storyId);
    }
}