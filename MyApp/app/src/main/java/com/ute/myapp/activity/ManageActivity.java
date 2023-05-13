package com.ute.myapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.event.EditStoryListener;
import com.ute.myapp.event.SelectChapterListener;
import com.ute.myapp.event.SelectMenuListener;
import com.ute.myapp.event.SelectNewStoryListener;
import com.ute.myapp.event.SelectStoryListener;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.fragment.AddStoryFragment;
import com.ute.myapp.fragment.AdminFragment;
import com.ute.myapp.fragment.ApproveStoryFragment;
import com.ute.myapp.fragment.ChapterManageFragment;
import com.ute.myapp.fragment.ContentManagerFragment;
import com.ute.myapp.fragment.ContentStory1Fragment;
import com.ute.myapp.fragment.ContentStory2Fragment;
import com.ute.myapp.fragment.EditStoryFragment;
import com.ute.myapp.fragment.GenreManageFragment;
import com.ute.myapp.fragment.MenuGenreFragment;
import com.ute.myapp.fragment.MenuStoryFragment;
import com.ute.myapp.fragment.MenuUserFragment;
import com.ute.myapp.fragment.Settings1Fragment;
import com.ute.myapp.fragment.Settings2Fragment;
import com.ute.myapp.fragment.StoryFragment;
import com.ute.myapp.fragment.StoryFragment2;
import com.ute.myapp.fragment.StoryManageFragment;
import com.ute.myapp.fragment.StoryWithChapterFragment;
import com.ute.myapp.fragment.UserManageFragment;
import com.ute.myapp.model.Chapter;
import com.ute.myapp.model.Genre;
import com.ute.myapp.model.User;
import com.ute.myapp.preferencehelper.PreferenceHelper;
import com.ute.myapp.util.MyUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ManageActivity extends AppCompatActivity implements SelectMenuListener, SelectStoryListener, SelectChapterListener, EditStoryListener, SelectNewStoryListener {
    private BottomNavigationView bottomNavigationView;
    private final Stack<Fragment> fragmentStack = new Stack<>();
    private boolean isInitialize;
    private User user;
    PreferenceHelper preferenceHelper;
    private List<Genre> genreListActive;
    private List<Genre> genreListInactive;
    private List<User> userListActive;
    private List<User> userListInactive;
    private List<Map<String, Object>> mapListStory;
    private List<Map<String, Object>> mapListNewStory;
    private FireStoreHelper fireStoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        initializeView();
    }

    private void initializeView() {
        preferenceHelper = PreferenceHelper.getInstance(this);
        fireStoreHelper = FireStoreHelper.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation_view_manage);
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constant.USER);
        if (preferenceHelper.isLoggedIn()) {
            user = preferenceHelper.getUser();
        }
        if (!isInitialize) {
            if (user != null) {
                loadUIForRole();
            }
            isInitialize = true;
        }
        loadData();
    }

    private void loadUIForRole() {
        if (user != null) {
            String roleName = user.getRoleName();
            if (roleName.equalsIgnoreCase(Constant.ADMIN)) {
                replaceFragment(new AdminFragment());
            } else if (roleName.equalsIgnoreCase(Constant.CONTENT_MANAGER)) {
                replaceFragment(new ContentManagerFragment());
            }
        }
    }

    private void loadData() {
        genreListActive = new ArrayList<>();
        genreListInactive = new ArrayList<>();
        userListActive = new ArrayList<>();
        userListInactive = new ArrayList<>();
        mapListStory = new ArrayList<>();
        mapListNewStory = new ArrayList<>();
        fireStoreHelper.getAllGenreActive(getAllActiveGenreOnSuccessListener);
        fireStoreHelper.getAllGenreInactive(getAllInactiveGenreOnSuccessListener);
        fireStoreHelper.getAllUserActive(getAllActiveUserOnSuccessListener);
        fireStoreHelper.getAllStory(getAllStoryOnSuccessListener);
        fireStoreHelper.getAllNewStory(getAllNewStoryOnSuccessListener);
    }

    private final NavigationBarView.OnItemSelectedListener onItemSelectedListener = item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_home_manage) {
            loadUIForRole();
            return true;
        } else if (itemId == R.id.menu_settings_manage) {
            if (user != null) {
                Settings2Fragment settings2Fragment = new Settings2Fragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.USER, user);
                settings2Fragment.setArguments(bundle);
                replaceFragment(settings2Fragment);
            } else {
                replaceFragment(new Settings1Fragment());
            }
            return true;
        }
        return false;
    };

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_admin, fragment);
        fragmentStack.push(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (!fragmentStack.isEmpty()) {
            fragmentStack.pop();
        }
        if (!fragmentStack.empty()) {
            loadData();
            Fragment fragment = fragmentStack.peek();
            setChecked(fragment);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout_admin, fragment);
            fragmentTransaction.commit();
        } else {
            finishAffinity();
        }
    }

    private void setChecked(Fragment fragment) {
        if (fragment instanceof AdminFragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_home_manage).setChecked(true);
        } else if (fragment instanceof ContentManagerFragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_home_manage).setChecked(true);
        } else if (fragment instanceof Settings1Fragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_settings_manage).setChecked(true);
        } else if (fragment instanceof Settings2Fragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_settings_manage).setChecked(true);
        }
    }

    private final OnSuccessListener<QuerySnapshot> getAllActiveGenreOnSuccessListener = queryDocumentSnapshots -> {
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                Genre genre = documentSnapshot.toObject(Genre.class);
                if (genre != null) {
                    genre.setGenreId(documentSnapshot.getId());
                    genreListActive.add(genre);
                }
            }
            genreListActive.sort(Comparator.comparing(Genre::getGenreName));
        }
    };

    private final OnSuccessListener<QuerySnapshot> getAllInactiveGenreOnSuccessListener = queryDocumentSnapshots -> {
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                Genre genre = documentSnapshot.toObject(Genre.class);
                if (genre != null) {
                    genre.setGenreId(documentSnapshot.getId());
                    genreListInactive.add(genre);
                }
            }
            genreListInactive.sort(Comparator.comparing(Genre::getGenreName));
        }
    };


    private final OnSuccessListener<QuerySnapshot> getAllActiveUserOnSuccessListener = queryDocumentSnapshots -> {
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                User userData = documentSnapshot.toObject(User.class);
                if (userData != null) {
                    userData.setUserId(documentSnapshot.getId());
                    userListActive.add(userData);
                }
            }
            userListActive.sort(Comparator.comparing(User::getUserName));
        }
    };

    private final OnSuccessListener<QuerySnapshot> getAllStoryOnSuccessListener = queryDocumentSnapshots -> {
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                Map<String, Object> data = documentSnapshot.getData();
                String storyId = documentSnapshot.getId();
                if (data != null) {
                    Map<String, Object> storyMap = new HashMap<>();

                    String genreName = (String) data.get(Constant.GENRE_NAME);
                    String authorName = (String) data.get(Constant.AUTHOR_NAME);
                    String storyName = (String) data.get(Constant.STORY_NAME);
                    Boolean status = (Boolean) data.get(Constant.STORY_STATUS);
                    Long withChapter = (Long) data.get(Constant.WITH_CHAPTER);
                    Boolean approve = (Boolean) data.get(Constant.APPROVE);
                    String imageUrl = (String) data.get(Constant.IMAGE_URL);
                    String userId = (String) data.get(Constant.USER_ID);

                    storyMap.put(Constant.STORY_ID, storyId);
                    storyMap.put(Constant.GENRE_NAME, genreName);
                    storyMap.put(Constant.AUTHOR_NAME, authorName);
                    storyMap.put(Constant.STORY_NAME, storyName);
                    storyMap.put(Constant.STORY_STATUS, status);
                    storyMap.put(Constant.WITH_CHAPTER, withChapter);
                    storyMap.put(Constant.APPROVE, approve);
                    storyMap.put(Constant.IMAGE_URL, imageUrl);
                    storyMap.put(Constant.USER_ID, userId);


                    if (withChapter != null) {
                        if (withChapter == 0) {
                            String content = (String) data.get(Constant.CONTENT);
                            storyMap.put(Constant.CONTENT, content);
                        } else {
                            data.forEach((key, value) -> {
                                if (key.startsWith("chapter")) {
                                    String content = (String) value;
                                    storyMap.put(key, content);
                                }
                            });
                        }
                    }
                    mapListStory.add(storyMap);
                }
            }
        }
    };


    private final OnSuccessListener<QuerySnapshot> getAllNewStoryOnSuccessListener = queryDocumentSnapshots -> {
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                Map<String, Object> data = documentSnapshot.getData();
                String storyId = documentSnapshot.getId();
                if (data != null) {
                    Map<String, Object> storyMap = new HashMap<>();

                    String genreName = (String) data.get(Constant.GENRE_NAME);
                    String authorName = (String) data.get(Constant.AUTHOR_NAME);
                    String storyName = (String) data.get(Constant.STORY_NAME);
                    Boolean status = (Boolean) data.get(Constant.STORY_STATUS);
                    Long withChapter = (Long) data.get(Constant.WITH_CHAPTER);
                    Boolean approve = (Boolean) data.get(Constant.APPROVE);
                    String imageUrl = (String) data.get(Constant.IMAGE_URL);
                    String userId = (String) data.get(Constant.USER_ID);

                    storyMap.put(Constant.STORY_ID, storyId);
                    storyMap.put(Constant.GENRE_NAME, genreName);
                    storyMap.put(Constant.AUTHOR_NAME, authorName);
                    storyMap.put(Constant.STORY_NAME, storyName);
                    storyMap.put(Constant.STORY_STATUS, status);
                    storyMap.put(Constant.WITH_CHAPTER, withChapter);
                    storyMap.put(Constant.APPROVE, approve);
                    storyMap.put(Constant.IMAGE_URL, imageUrl);
                    storyMap.put(Constant.USER_ID, userId);


                    if (withChapter != null) {
                        if (withChapter == 0) {
                            String content = (String) data.get(Constant.CONTENT);
                            storyMap.put(Constant.CONTENT, content);
                        } else {
                            data.forEach((key, value) -> {
                                if (key.startsWith("chapter")) {
                                    String content = (String) value;
                                    storyMap.put(key, content);
                                }
                            });
                        }
                    }
                    mapListNewStory.add(storyMap);
                }
            }
        }
    };


    @Override
    public void onClickGenre() {
        replaceFragment(new MenuGenreFragment());
    }

    @Override
    public void onClickActiveGenre() {
        GenreManageFragment genreManageFragment = new GenreManageFragment();
        Bundle bundle = new Bundle();
        String json = MyUtil.listGenreToJson(genreListActive);
        bundle.putString(Constant.LIST_GENRE, json);
        bundle.putBoolean(Constant.GENRE_STATUS, true);
        genreManageFragment.setArguments(bundle);
        replaceFragment(genreManageFragment);
    }

    @Override
    public void onClickInactiveGenre() {
        GenreManageFragment genreManageFragment = new GenreManageFragment();
        Bundle bundle = new Bundle();
        String json = MyUtil.listGenreToJson(genreListInactive);
        bundle.putString(Constant.LIST_GENRE, json);
        bundle.putBoolean(Constant.GENRE_STATUS, false);
        genreManageFragment.setArguments(bundle);
        replaceFragment(genreManageFragment);
    }

    @Override
    public void onClickUser() {
        replaceFragment(new MenuUserFragment());
    }

    @Override
    public void onClickActiveUser() {
        UserManageFragment userManageFragment = new UserManageFragment();
        Bundle bundle = new Bundle();
        String json = MyUtil.listUserToJson(MyUtil.filterListUser(userListActive));
        bundle.putString(Constant.LIST_USER, json);
        bundle.putBoolean(Constant.USER_STATUS, true);
        userManageFragment.setArguments(bundle);
        replaceFragment(userManageFragment);
    }

    @Override
    public void onClickInactiveUser() {
        UserManageFragment userManageFragment = new UserManageFragment();
        Bundle bundle = new Bundle();
        String json = MyUtil.listUserToJson(MyUtil.filterListUser(userListInactive));
        bundle.putString(Constant.LIST_USER, json);
        bundle.putBoolean(Constant.USER_STATUS, false);
        userManageFragment.setArguments(bundle);
        replaceFragment(userManageFragment);
    }

    @Override
    public void onClickStory() {
        replaceFragment(new MenuStoryFragment());
    }

    @Override
    public void onClickAddStory() {
        AddStoryFragment addStoryFragment = new AddStoryFragment();
        Bundle bundle = new Bundle();
        String json = MyUtil.listGenreToJson(genreListActive);
        bundle.putString(Constant.LIST_GENRE, json);
        addStoryFragment.setArguments(bundle);
        replaceFragment(addStoryFragment);
    }

    @Override
    public void onClickSearchStory() {
        StoryManageFragment storyManageFragment = new StoryManageFragment();
        Bundle bundle = new Bundle();
        String json = MyUtil.listStoryToJson(mapListStory);
        String jsonGenreList = MyUtil.listGenreToJson(genreListActive);
        bundle.putString(Constant.LIST_STORY, json);
        bundle.putString(Constant.LIST_GENRE, jsonGenreList);
        storyManageFragment.setArguments(bundle);
        replaceFragment(storyManageFragment);
    }

    @Override
    public void onClickNewStory() {
        ApproveStoryFragment approveStoryFragment = new ApproveStoryFragment();
        Bundle bundle = new Bundle();
        String json = MyUtil.listStoryToJson(mapListNewStory);
        bundle.putString(Constant.LIST_STORY, json);
        approveStoryFragment.setArguments(bundle);
        replaceFragment(approveStoryFragment);
    }

    @Override
    public void onClickReportedStory() {

    }

    @Override
    public void onSelectStoryListener(Map<String, Object> map) {
        Long withChapter = (Long) map.get(Constant.WITH_CHAPTER);
        if (withChapter != null) {
            if (withChapter == 0) {
                ContentStory1Fragment contentStory1Fragment = new ContentStory1Fragment();
                Bundle bundle = new Bundle();
                String json = MyUtil.mapStoryToJson(map);
                bundle.putString(Constant.MAP_STORY, json);
                contentStory1Fragment.setArguments(bundle);
                replaceFragment(contentStory1Fragment);
            } else {
                ChapterManageFragment chapterManageFragment = new ChapterManageFragment();
                Bundle bundle = new Bundle();
                String json = MyUtil.mapStoryToJson(map);
                bundle.putString(Constant.MAP_STORY, json);
                chapterManageFragment.setArguments(bundle);
                replaceFragment(chapterManageFragment);
            }
        }
    }

    @Override
    public void onSelectChapterListener(Chapter chapter, String storyId) {
        ContentStory2Fragment contentStory2Fragment = new ContentStory2Fragment();
        Bundle bundle = new Bundle();
        String json = MyUtil.chapterToJson(chapter);
        bundle.putString(Constant.CHAPTER, json);
        bundle.putString(Constant.STORY_ID, storyId);
        contentStory2Fragment.setArguments(bundle);
        replaceFragment(contentStory2Fragment);
    }

    @Override
    public void onReadChapterListener(Chapter chapter, String storyId) {
        StoryFragment2 storyFragment2 = new StoryFragment2();
        Bundle bundle = new Bundle();
        String json = MyUtil.chapterToJson(chapter);
        bundle.putString(Constant.CHAPTER, json);
        bundle.putString(Constant.STORY_ID, storyId);
        storyFragment2.setArguments(bundle);
        replaceFragment(storyFragment2);
    }

    @Override
    public void editStoryListener(Map<String, Object> map) {
        String imageUrl = (String) map.get(Constant.IMAGE_URL);
        File tempDir = this.getCacheDir();
        String extension = MyUtil.getExtension(imageUrl);
        String fileName = System.currentTimeMillis() + "." + extension;
        File outputFile = new File(tempDir, fileName);
        Picasso.get()
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try {
                            FileOutputStream outputStream = new FileOutputStream(outputFile);
                            if (extension.equals(Constant.PNG)) {
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            } else if (extension.contains(Constant.JPG) || extension.contains(Constant.JPEG)) {
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            }
                            outputStream.flush();
                            outputStream.close();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        EditStoryFragment editStoryFragment = new EditStoryFragment();
                        Bundle bundle = new Bundle();
                        String jsonGenreName = MyUtil.listGenreToJson(genreListActive);
                        bundle.putString(Constant.LIST_GENRE, jsonGenreName);
                        String json = MyUtil.mapStoryToJson(map);
                        String imagePath = outputFile.getAbsolutePath();
                        bundle.putString(Constant.MAP_STORY, json);
                        bundle.putString(Constant.IMAGE_PATH, imagePath);
                        editStoryFragment.setArguments(bundle);
                        replaceFragment(editStoryFragment);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }


    @Override
    public void onSelectNewStoryListener(Map<String, Object> map) {
        Long withChapter = (Long) map.get(Constant.WITH_CHAPTER);
        if (withChapter != null) {
            if (withChapter == 0) {
                StoryFragment storyFragment = new StoryFragment();
                Bundle bundle = new Bundle();
                String json = MyUtil.mapStoryToJson(map);
                bundle.putString(Constant.MAP_STORY, json);
                storyFragment.setArguments(bundle);
                replaceFragment(storyFragment);
            } else {
                StoryWithChapterFragment storyWithChapterFragment = new StoryWithChapterFragment();
                Bundle bundle = new Bundle();
                String json = MyUtil.mapStoryToJson(map);
                bundle.putString(Constant.MAP_STORY, json);
                storyWithChapterFragment.setArguments(bundle);
                replaceFragment(storyWithChapterFragment);
            }
        }
    }

}