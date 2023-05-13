package com.ute.myapp.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.event.OnUploadListener;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.firebasehelper.FirebaseStorageHelper;
import com.ute.myapp.model.Genre;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EditStoryFragment extends Fragment {
    private Map<String, Object> mapStory;
    private TextInputLayout textInputLayoutStoryName;
    private TextInputLayout textInputLayoutAuthor;
    private TextInputEditText textInputEditTextStoryName;
    private TextInputEditText textInputEditTextAuthor;
    private Spinner spinner;
    private ShapeableImageView shapeableImageView;
    Button buttonUpdateImage;
    Button buttonEditStory;
    private FireStoreHelper fireStoreHelper;
    private List<String> genreNameList;
    private Context context;
    private String imagePath;
    private Uri imageUri;
    private FirebaseStorageHelper firebaseStorageHelper;
    private FragmentActivity fragmentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentActivity = getActivity();
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_edit_story, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        fireStoreHelper = FireStoreHelper.getInstance();
        firebaseStorageHelper = FirebaseStorageHelper.getInstance();
        shapeableImageView = view.findViewById(R.id.shape_able_image_view);

        Bundle bundle = getArguments();
        if (bundle != null) {
            List<Genre> genreList;
            String jsonGenreList = (String) bundle.get(Constant.LIST_GENRE);
            String json = (String) bundle.get(Constant.MAP_STORY);
            imagePath = (String) bundle.get(Constant.IMAGE_PATH);
            if (json != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();

                Type typeGenre = new TypeToken<List<Genre>>() {
                }.getType();
                genreList = gson.fromJson(jsonGenreList, typeGenre);
                genreNameList = genreList.stream().map(Genre::getGenreName).collect(Collectors.toList());

                mapStory = gson.fromJson(json, type);
                Double withChapter = (Double) mapStory.get(Constant.WITH_CHAPTER);
                if (withChapter != null) {
                    Long longValue = withChapter.longValue();
                    mapStory.put(Constant.WITH_CHAPTER, longValue);
                }

            }
        }
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            shapeableImageView.setImageBitmap(bitmap);
        }
        String storyName = (String) mapStory.get(Constant.STORY_NAME);
        String authorName = (String) mapStory.get(Constant.AUTHOR_NAME);
        String genreName = (String) mapStory.get(Constant.GENRE_NAME);
        textInputLayoutStoryName = view.findViewById(R.id.text_input_layout_story_name);
        textInputLayoutAuthor = view.findViewById(R.id.text_input_layout_author);

        textInputEditTextStoryName = view.findViewById(R.id.text_input_edit_text_story_name);
        textInputEditTextAuthor = view.findViewById(R.id.text_input_edit_text_author);

        spinner = view.findViewById(R.id.spinner_genre_name);

        buttonUpdateImage = view.findViewById(R.id.button_update_image);
        buttonUpdateImage.setOnClickListener(buttonUpdateImageOnClickListener);
        buttonEditStory = view.findViewById(R.id.button_edit_story);
        buttonEditStory.setOnClickListener(onClickListener);

        textInputEditTextStoryName.setText(storyName);
        textInputEditTextAuthor.setText(authorName);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, genreNameList);
        spinner.setAdapter(adapter);
        setValueSpinner(genreName);

    }

    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            imageUri = result;
            Picasso.get().load(result).into(shapeableImageView);
        }
    });


    private final View.OnClickListener buttonUpdateImageOnClickListener = view -> imagePickerLauncher.launch(Constant.MINE_IMAGE);

    private void setValueSpinner(String genreName) {
        IntStream.range(0, genreNameList.size()).forEach(i -> {
            if (genreName.equalsIgnoreCase(genreNameList.get(i))) {
                spinner.setSelection(i);
            }
        });
    }

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
        return flag;
    }

    private String getFileNameExtension(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private final OnUploadListener onUploadListener = new OnUploadListener() {
        @Override
        public void onUploadSuccess(String downloadUrl) {
            String imageUrl = (String) mapStory.get(Constant.IMAGE_URL);
            firebaseStorageHelper.deleteImage(imageUrl);
            updateStory(downloadUrl);
        }

        @Override
        public void onUploadFailure() {
            Toast.makeText(context, R.string.upload_image_story_failure, Toast.LENGTH_SHORT).show();
        }
    };

    private final OnSuccessListener<Void> editStoryOnSuccessListener = unused -> Toast.makeText(context, R.string.edit_successful, Toast.LENGTH_SHORT).show();
    private final OnFailureListener editStoryOnFailureListener = e -> Toast.makeText(context, R.string.edit_failure, Toast.LENGTH_SHORT).show();

    private void updateStory(String downloadUrl) {
        Map<String, Object> storyMap = new HashMap<>();
        String storyId = (String) mapStory.get(Constant.STORY_ID);

        String genreName = spinner.getSelectedItem().toString();
        String authorName = String.valueOf(textInputEditTextAuthor.getText());
        String storyName = String.valueOf(textInputEditTextStoryName.getText());

        storyMap.put(Constant.GENRE_NAME, genreName);
        storyMap.put(Constant.AUTHOR_NAME, authorName);
        storyMap.put(Constant.STORY_NAME, storyName);
        storyMap.put(Constant.IMAGE_URL, downloadUrl);

        fireStoreHelper.editStory(storyId, storyMap, editStoryOnSuccessListener, editStoryOnFailureListener);
        fragmentActivity.onBackPressed();
    }

    private final View.OnClickListener onClickListener = view -> {
        if (checkEmptyField()) {
            if (imageUri != null) {
                String fileName = System.currentTimeMillis() + "." + getFileNameExtension(imageUri);
                firebaseStorageHelper.uploadImage(imageUri, fileName, onUploadListener);
            } else {
                String imageUrl = (String) mapStory.get(Constant.IMAGE_URL);
                updateStory(imageUrl);
            }
        }
    };
}