package com.ute.myapp.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.event.OnUploadListener;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.firebasehelper.FirebaseStorageHelper;
import com.ute.myapp.model.Genre;
import com.ute.myapp.model.User;
import com.ute.myapp.preferencehelper.PreferenceHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class AddStoryFragment extends Fragment {
    private List<String> genreNameList;
    private List<String> contentChapterList;
    private TextInputEditText textInputEditTextStoryName;
    private TextInputEditText textInputEditTextAuthor;
    private TextInputEditText textInputEditTextContent;
    private TextInputEditText textInputEditTextChapterContent;
    private Spinner spinner;
    private SwitchMaterial switchMaterial;
    private Button buttonNewChapter;
    Button buttonPublish;
    private TextInputLayout textInputLayoutContent;
    private TextInputLayout textInputLayoutStoryName;
    private TextInputLayout textInputLayoutAuthor;
    private Context context;
    PreferenceHelper preferenceHelper;
    private User user;
    private FireStoreHelper fireStoreHelper;
    Button buttonFileChooser;
    private ShapeableImageView shapeableImageView;
    private Uri imageUri;
    private FirebaseStorageHelper firebaseStorageHelper;

    private FragmentActivity fragmentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_add_story, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        fragmentActivity = getActivity();
        fireStoreHelper = FireStoreHelper.getInstance();
        firebaseStorageHelper = FirebaseStorageHelper.getInstance();
        preferenceHelper = PreferenceHelper.getInstance(context);
        user = preferenceHelper.getUser();
        contentChapterList = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            List<Genre> genreList;
            String json = (String) bundle.get(Constant.LIST_GENRE);
            if (json != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Genre>>() {
                }.getType();
                genreList = gson.fromJson(json, type);
                genreNameList = genreList.stream().map(Genre::getGenreName).collect(Collectors.toList());
            }
        }
        textInputEditTextStoryName = view.findViewById(R.id.text_input_edit_text_story_name);
        textInputEditTextAuthor = view.findViewById(R.id.text_input_edit_text_author);
        textInputEditTextContent = view.findViewById(R.id.text_input_edit_text_content);

        buttonPublish = view.findViewById(R.id.button_publish);
        buttonPublish.setOnClickListener(buttonPublishOnClickListener);
        buttonNewChapter = view.findViewById(R.id.button_new_chapter);
        buttonNewChapter.setOnClickListener(buttonNewChapterOnClickListener);
        textInputLayoutContent = view.findViewById(R.id.text_input_layout_content);
        textInputLayoutAuthor = view.findViewById(R.id.text_input_layout_author);
        textInputLayoutStoryName = view.findViewById(R.id.text_input_layout_story_name);
        spinner = view.findViewById(R.id.spinner_genre);
        switchMaterial = view.findViewById(R.id.switch_optional);
        buttonFileChooser = view.findViewById(R.id.button_upload_image);
        buttonFileChooser.setOnClickListener(buttonFileChooserOnClickListener);
        shapeableImageView = view.findViewById(R.id.shape_able_image_view);
        switchMaterial.setOnCheckedChangeListener(onCheckedChangeListener);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, genreNameList);
        spinner.setAdapter(adapter);
    }

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            imageUri = result;
            Picasso.get().load(result).into(shapeableImageView);
        }
    });


    private final View.OnClickListener buttonFileChooserOnClickListener = view -> imagePickerLauncher.launch(Constant.MINE_IMAGE);

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

    private final View.OnClickListener buttonNewChapterOnClickListener = view -> {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        ScrollView linearLayout = new ScrollView(context);
        builder.setTitle(R.string.add_chapter);
        TextInputLayout textInputLayoutChapterContent = new TextInputLayout(context);
        textInputEditTextChapterContent = new TextInputEditText(context);
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
            String content = String.valueOf(textInputEditTextChapterContent.getText());
            if (!TextUtils.isEmpty(content)) {
                contentChapterList.add(content);
                Toast.makeText(context, R.string.add_chapter_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.require_add_chapter, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.abort, (dialog, which) -> dialog.dismiss());
        builder.setView(linearLayout);
        AlertDialog dialog = builder.create();
        dialog.show();
    };


    private final OnSuccessListener<DocumentReference> onSuccessListener = documentReference -> {
        Toast.makeText(context, R.string.create_story_successful, Toast.LENGTH_SHORT).show();
        fragmentActivity.onBackPressed();
    };

    private final OnFailureListener onFailureListener = e -> Toast.makeText(context, R.string.create_story_failure, Toast.LENGTH_SHORT).show();

    private String getFileNameExtension(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private final OnUploadListener onUploadListener = new OnUploadListener() {
        @Override
        public void onUploadSuccess(String downloadUrl) {
            Map<String, Object> storyMap = new HashMap<>();
            String genreName = spinner.getSelectedItem().toString();
            String storyName = String.valueOf(textInputEditTextStoryName.getText());
            String authorName = String.valueOf(textInputEditTextAuthor.getText());
            String content = String.valueOf(textInputEditTextContent.getText());
            storyMap.put(Constant.AUTHOR_NAME, authorName);
            storyMap.put(Constant.USER_ID, user.getUserId());
            storyMap.put(Constant.GENRE_NAME, genreName);
            storyMap.put(Constant.STORY_NAME, storyName);
            storyMap.put(Constant.STORY_STATUS, true);
            storyMap.put(Constant.APPROVE, true);
            storyMap.put(Constant.IMAGE_URL, downloadUrl);

            if (!switchMaterial.isChecked()) {
                storyMap.put(Constant.WITH_CHAPTER, 0);
                storyMap.put(Constant.CONTENT, content);
            } else {
                storyMap.put(Constant.WITH_CHAPTER, contentChapterList.size());
                IntStream.range(0, contentChapterList.size())
                        .forEach(index -> {
                            String chapter = "chapter" + (index + 1);
                            storyMap.put(chapter, contentChapterList.get(index));
                        });

            }
            fireStoreHelper.createStory(storyMap, onSuccessListener, onFailureListener);
        }

        @Override
        public void onUploadFailure() {
            Toast.makeText(context, R.string.upload_image_failure, Toast.LENGTH_SHORT).show();
        }
    };

    private final OnUploadListener AuthorOnUploadListener = new OnUploadListener() {
        @Override
        public void onUploadSuccess(String downloadUrl) {
            Map<String, Object> storyMap = new HashMap<>();
            String genreName = spinner.getSelectedItem().toString();
            String storyName = String.valueOf(textInputEditTextStoryName.getText());
            String authorName = String.valueOf(textInputEditTextAuthor.getText());
            String content = String.valueOf(textInputEditTextContent.getText());
            storyMap.put(Constant.AUTHOR_NAME, authorName);
            storyMap.put(Constant.USER_ID, user.getUserId());
            storyMap.put(Constant.GENRE_NAME, genreName);
            storyMap.put(Constant.STORY_NAME, storyName);
            storyMap.put(Constant.STORY_STATUS, true);
            storyMap.put(Constant.APPROVE, false);
            storyMap.put(Constant.IMAGE_URL, downloadUrl);

            if (!switchMaterial.isChecked()) {
                storyMap.put(Constant.WITH_CHAPTER, 0);
                storyMap.put(Constant.CONTENT, content);
            } else {
                storyMap.put(Constant.WITH_CHAPTER, contentChapterList.size());
                IntStream.range(0, contentChapterList.size())
                        .forEach(index -> {
                            String chapter = "chapter" + (index + 1);
                            storyMap.put(chapter, contentChapterList.get(index));
                        });

            }
            fireStoreHelper.createStory(storyMap, onSuccessListener, onFailureListener);
        }

        @Override
        public void onUploadFailure() {
            Toast.makeText(context, R.string.upload_image_failure, Toast.LENGTH_SHORT).show();
        }
    };


    private final View.OnClickListener buttonPublishOnClickListener = view -> {
        if (user != null) {
            String roleName = user.getRoleName();
            if (roleName.equalsIgnoreCase(Constant.CONTENT_MANAGER) || roleName.equalsIgnoreCase(Constant.ADMIN)) {
                if (checkEmptyField()) {
                    String fileName = System.currentTimeMillis() + "." + getFileNameExtension(imageUri);
                    firebaseStorageHelper.uploadImage(imageUri, fileName, onUploadListener);
                }
            } else {
                if (checkEmptyField()) {
                    String fileName = System.currentTimeMillis() + "." + getFileNameExtension(imageUri);
                    firebaseStorageHelper.uploadImage(imageUri, fileName, AuthorOnUploadListener);
                }
            }
        }
    };

    private boolean checkEmptyField() {
        boolean flag = true;
        if (TextUtils.isEmpty(textInputEditTextStoryName.getText())) {
            textInputLayoutStoryName.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutStoryName.setError(null);
        }
        if (TextUtils.isEmpty(textInputEditTextAuthor.getText())) {
            textInputLayoutAuthor.setError(getString(R.string.require));
            flag = false;
        } else {
            textInputLayoutAuthor.setError(null);
        }
        if (!switchMaterial.isChecked()) {
            if (TextUtils.isEmpty(textInputEditTextContent.getText())) {
                textInputLayoutContent.setError(getString(R.string.require));
                flag = false;
            } else {
                textInputLayoutContent.setError(null);
            }
        }
        if (imageUri == null) {
            Toast.makeText(context, R.string.upload_image_error, Toast.LENGTH_SHORT).show();
            flag = false;
        }
        return flag;
    }


    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (compoundButton, b) -> {
        if (b) {
            textInputLayoutContent.setVisibility(View.GONE);
            buttonNewChapter.setVisibility(View.VISIBLE);
        } else {
            textInputLayoutContent.setVisibility(View.VISIBLE);
            buttonNewChapter.setVisibility(View.GONE);
        }
    };
}