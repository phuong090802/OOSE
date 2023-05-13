package com.ute.myapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ute.myapp.R;
import com.ute.myapp.adapter.ManageGenreAdapter;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.decoration.GenreItemDecorationAdmin;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.Genre;
import com.ute.myapp.util.MyUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenreManageFragment extends Fragment {
    private Context context;
    FloatingActionButton floatingActionButtonAddGenre;
    private FireStoreHelper fireStoreHelper;
    private TextInputEditText textInputEditTextGenreName;
    private List<Genre> genreList;
    private boolean status;
    RecyclerView recyclerView;
    private ManageGenreAdapter manageGenreAdapter;
    GridLayoutManager gridLayoutManager;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre_manage, container, false);
        context = getContext();
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = (String) bundle.get(Constant.LIST_GENRE);
            status = bundle.getBoolean(Constant.GENRE_STATUS);
            if (json != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Genre>>() {
                }.getType();
                genreList = gson.fromJson(json, type);
            }
        }
        fireStoreHelper = FireStoreHelper.getInstance();
        searchView = view.findViewById(R.id.search_view_genre_manage);
        searchView.setOnQueryTextListener(onQueryTextListener);
        floatingActionButtonAddGenre = view.findViewById(R.id.floating_action_button_add_genre);
        floatingActionButtonAddGenre.setOnClickListener(floatingActionButtonAddGenreOnClickListener);

        recyclerView = view.findViewById(R.id.recycler_view_genre);

        gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GenreItemDecorationAdmin(22));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        manageGenreAdapter = new ManageGenreAdapter(context, genreList);
        recyclerView.setAdapter(manageGenreAdapter);
    }

    private final SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {

            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (!TextUtils.isEmpty(newText)) {
                List<Genre> filterDataList = genreList.stream().filter(genre -> genre.getGenreName().contains(newText))
                        .collect(Collectors.toList());
                manageGenreAdapter = new ManageGenreAdapter(context, filterDataList);
                recyclerView.setAdapter(manageGenreAdapter);
                return true;

            } else {
                reloadData();
                return false;
            }
        }
    };


    private final OnSuccessListener<DocumentReference> addGenreOnSuccessListener = documentReference -> Toast.makeText(context, R.string.add_genre_successful, Toast.LENGTH_SHORT).show();

    private final OnFailureListener addGenreOnFailureListener = e -> Toast.makeText(context, R.string.add_genre_fail, Toast.LENGTH_SHORT).show();

    private final OnSuccessListener<QuerySnapshot> getAllGenreOnSuccessListener = queryDocumentSnapshots -> {
        List<Genre> updateData = new ArrayList<>();
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                Genre genre = documentSnapshot.toObject(Genre.class);
                if (genre != null) {
                    genre.setGenreId(documentSnapshot.getId());
                    updateData.add(genre);
                }
            }
            manageGenreAdapter.updateGenreList(updateData);

        }
    };

    private final OnCompleteListener<QuerySnapshot> checkGenreNameOnCompleteListener = task -> {
        if (task.isSuccessful()) {
            QuerySnapshot querySnapshot = task.getResult();
            String genreName = String.valueOf(textInputEditTextGenreName.getText());
            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                Toast.makeText(context, MyUtil.genreNameExist(genreName), Toast.LENGTH_SHORT).show();
            } else {
                textInputEditTextGenreName.setError(null);
                Genre genre = new Genre();
                genre.setGenreName(genreName);
                genre.setStatus(status);
                fireStoreHelper.createGenre(genre, addGenreOnSuccessListener, addGenreOnFailureListener);
                reloadData();
            }
        } else {
            Toast.makeText(context, R.string.an_error_occurred, Toast.LENGTH_SHORT).show();
        }
    };

    private void reloadData() {
        if (status) {
            fireStoreHelper.getAllGenreActive(getAllGenreOnSuccessListener);
        } else {
            fireStoreHelper.getAllGenreInactive(getAllGenreOnSuccessListener);
        }
    }

    private final View.OnClickListener floatingActionButtonAddGenreOnClickListener = view -> {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        builder.setTitle(R.string.add_genre);
        TextInputLayout textInputLayoutGenreName = new TextInputLayout(context);
        textInputEditTextGenreName = new TextInputEditText(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG);
        textInputLayoutGenreName.setLayoutParams(params);
        linearLayout.addView(textInputLayoutGenreName);
        textInputEditTextGenreName.setInputType(InputType.TYPE_CLASS_TEXT);
        textInputEditTextGenreName.setHint(R.string.enter_your_genre);
        textInputLayoutGenreName.addView(textInputEditTextGenreName);

        builder.setView(textInputLayoutGenreName);
        builder.setPositiveButton(R.string.add, (dialog, which) -> {
            if (TextUtils.isEmpty(textInputEditTextGenreName.getText())) {
                Toast.makeText(context, R.string.require, Toast.LENGTH_SHORT).show();
            } else {
                String genreName = String.valueOf(textInputEditTextGenreName.getText());
                fireStoreHelper.checkGenreName(genreName, checkGenreNameOnCompleteListener);
            }
        });

        builder.setNegativeButton(R.string.abort, (dialog, which) -> dialog.dismiss());
        builder.setView(linearLayout);
        AlertDialog dialog = builder.create();
        dialog.show();
    };
}