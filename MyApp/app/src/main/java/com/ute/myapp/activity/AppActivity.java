package com.ute.myapp.activity;

import android.content.Intent;
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
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.event.ComposeOnClickListener;
import com.ute.myapp.event.OnComposeClickListener;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.fragment.AddStoryFragment;
import com.ute.myapp.fragment.BookshelfFragment;
import com.ute.myapp.fragment.ComposeFragment;
import com.ute.myapp.fragment.HomeFragment;
import com.ute.myapp.fragment.SearchFragment;
import com.ute.myapp.fragment.Settings1Fragment;
import com.ute.myapp.fragment.Settings3Fragment;
import com.ute.myapp.model.Genre;
import com.ute.myapp.model.User;
import com.ute.myapp.preferencehelper.PreferenceHelper;
import com.ute.myapp.util.MyUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class AppActivity extends AppCompatActivity implements ComposeOnClickListener, OnComposeClickListener {
    private BottomNavigationView bottomNavigationView;
    private final Stack<Fragment> fragmentStack = new Stack<>();
    private boolean isInitialize;
    private User user;
    PreferenceHelper preferenceHelper;
    private List<Genre> genreList;
    private FireStoreHelper fireStoreHelper;
    private List<Map<String, Object>> mapListStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        initializeView();
    }


    private void initializeView() {
        fireStoreHelper = FireStoreHelper.getInstance();
        loadData();
        preferenceHelper = PreferenceHelper.getInstance(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        if (!isInitialize) {
            isInitialize = true;
            replaceFragment(new HomeFragment());
        }
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(Constant.USER);
        if (preferenceHelper.isLoggedIn()) {
            user = preferenceHelper.getUser();
        }
    }


    private void loadData() {
        genreList = new ArrayList<>();
        mapListStory = new ArrayList<>();
        fireStoreHelper.getAllGenreActive(getAllGenreOnSuccessListener);
        fireStoreHelper.getAllStory(getAllStoryOnSuccessListener);
    }

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

    private final OnSuccessListener<QuerySnapshot> getAllGenreOnSuccessListener = queryDocumentSnapshots -> {
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                Genre genre = documentSnapshot.toObject(Genre.class);
                if (genre != null) {
                    genre.setGenreId(documentSnapshot.getId());
                    genreList.add(genre);
                }
            }
            genreList.sort(Comparator.comparing(Genre::getGenreName));
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (user != null) {
            String roleName = user.getRoleName();
            if (roleName.equalsIgnoreCase(Constant.ADMIN) || roleName.equalsIgnoreCase(Constant.CONTENT_MANAGER)) {
                Intent intent = new Intent(this, ManageActivity.class);
                startActivity(intent);
            }
        }
    }

    private final NavigationBarView.OnItemSelectedListener onItemSelectedListener = item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_home) {
            HomeFragment homeFragment = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.LIST_GENRE, MyUtil.listGenreToJson(genreList));
            homeFragment.setArguments(bundle);
            replaceFragment(homeFragment);
            return true;
        } else if (itemId == R.id.menu_search) {
            replaceFragment(new SearchFragment());
            return true;
        } else if (itemId == R.id.menu_bookshelf) {
            replaceFragment(new BookshelfFragment());
            return true;
        } else if (itemId == R.id.menu_settings) {
            if (user != null) {
                Settings3Fragment settings3Fragment = new Settings3Fragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.USER, user);
                settings3Fragment.setArguments(bundle);
                replaceFragment(settings3Fragment);
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
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentStack.push(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (!fragmentStack.isEmpty()) {
            fragmentStack.pop();
        }
        if (fragmentStack.empty()) {
            super.onBackPressed();
        } else {
            Fragment fragment = fragmentStack.peek();
            setChecked(fragment);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
        }
    }

    private void setChecked(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
        } else if (fragment instanceof SearchFragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_search).setChecked(true);
        } else if (fragment instanceof BookshelfFragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_bookshelf).setChecked(true);
        } else if (fragment instanceof Settings1Fragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_settings).setChecked(true);
        } else if (fragment instanceof Settings3Fragment) {
            bottomNavigationView.getMenu().findItem(R.id.menu_settings).setChecked(true);
        }
    }

    @Override
    public void onComposeOnClick(User user) {
        replaceFragment( new ComposeFragment());
    }

    @Override
    public void onClickCompose() {
        AddStoryFragment addStoryFragment = new AddStoryFragment();
        Bundle bundle = new Bundle();
        String json = MyUtil.listGenreToJson(genreList);
        bundle.putString(Constant.LIST_GENRE, json);
        addStoryFragment.setArguments(bundle);
        replaceFragment(addStoryFragment);
    }

    @Override
    public void onClickYourStory() {

    }
}