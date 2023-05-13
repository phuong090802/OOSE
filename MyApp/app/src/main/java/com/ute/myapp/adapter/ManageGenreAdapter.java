package com.ute.myapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ute.myapp.R;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.Genre;
import com.ute.myapp.util.MyUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ManageGenreAdapter extends RecyclerView.Adapter<ManageGenreAdapter.ViewHolder> {
    private final List<Genre> genreList;
    private Context context;
    private TextInputEditText textInputEditTextGenreName;
    private SwitchMaterial switchMaterial;
    private final FireStoreHelper fireStoreHelper;
    private Genre genreUpdate;

    public ManageGenreAdapter(Context context, List<Genre> genreList) {
        this.genreList = genreList;
        this.context = context;
        fireStoreHelper = FireStoreHelper.getInstance();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateGenreList(List<Genre> genreList) {
        this.genreList.clear();
        this.genreList.addAll(genreList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ManageGenreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_custom_genre_manage, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ManageGenreAdapter.ViewHolder holder, int position) {
        Genre genre = genreList.get(position);
        holder.textViewGenreName.setText(genre.getGenreName());
        holder.itemView.setOnLongClickListener(view -> {
            genreUpdate = genre;
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            builder.setTitle(R.string.edit_genre);
            TextInputLayout textInputLayoutGenreName = new TextInputLayout(context);
            switchMaterial = new SwitchMaterial(context);
            switchMaterial.setText(R.string.genre_status);
            switchMaterial.setChecked(genreUpdate.isStatus());
            textInputEditTextGenreName = new TextInputEditText(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            textInputEditTextGenreName.setText(genreUpdate.getGenreName());
            params.setMargins(Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG);
            textInputLayoutGenreName.setLayoutParams(params);
            switchMaterial.setLayoutParams(params);
            linearLayout.addView(textInputLayoutGenreName);
            linearLayout.addView(switchMaterial);
            textInputEditTextGenreName.setInputType(InputType.TYPE_CLASS_TEXT);
            textInputEditTextGenreName.setHint(R.string.enter_your_genre);
            textInputLayoutGenreName.addView(textInputEditTextGenreName);
            builder.setView(textInputLayoutGenreName);
            builder.setView(switchMaterial);
            builder.setPositiveButton(R.string.edit_genre, (dialog, which) -> {
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
            return true;
        });
    }

    private final OnSuccessListener<Void> updateGenreOnSuccessListener = unused -> Toast.makeText(context, R.string.edit_genre_successful, Toast.LENGTH_SHORT).show();

    private final OnFailureListener updateGenreOnFailureListener = e -> Toast.makeText(context, R.string.edit_genre_fail, Toast.LENGTH_SHORT).show();


    private final OnCompleteListener<QuerySnapshot> checkGenreNameOnCompleteListener = task -> {
        if (task.isSuccessful()) {
            QuerySnapshot querySnapshot = task.getResult();
            String genreName = String.valueOf(textInputEditTextGenreName.getText());
            Genre genre = new Genre(genreUpdate.getGenreId(), genreName, switchMaterial.isChecked());
            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                if (documentSnapshot.exists()) {
                    Genre genreData = documentSnapshot.toObject(Genre.class);
                    if (genreData != null) {
                        genreData.setGenreId(documentSnapshot.getId());
                        if (genreData.getGenreId().equalsIgnoreCase(genreUpdate.getGenreId())) {
                            updateGenre(genre, genreUpdate.isStatus());
                        } else {
                            Toast.makeText(context, MyUtil.genreNameExist(genreName), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                textInputEditTextGenreName.setError(null);
                updateGenre(genre, genreUpdate.isStatus());
            }
        } else {
            Toast.makeText(context, R.string.an_error_occurred, Toast.LENGTH_SHORT).show();
        }
    };


    private final OnSuccessListener<QuerySnapshot> getAllGenreOnSuccessListener = queryDocumentSnapshots -> {
        List<Genre> updateData = new ArrayList<>();
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                Genre genre = documentSnapshot.toObject(Genre.class);
                updateData.add(genre);
            }
            updateData.sort(Comparator.comparing(Genre::getGenreName));
            updateGenreList(updateData);
        }
    };

    private void updateGenre(Genre genre, boolean status) {
        fireStoreHelper.updateGenre(genre, updateGenreOnSuccessListener, updateGenreOnFailureListener);
        if (status) {
            fireStoreHelper.getAllGenreActive(getAllGenreOnSuccessListener);
        } else {
            fireStoreHelper.getAllGenreInactive(getAllGenreOnSuccessListener);
        }
    }

    @Override
    public int getItemCount() {
        return genreList == null ? 0 : genreList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewGenreName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGenreName = itemView.findViewById(R.id.text_view_genre_info);
        }
    }
}
