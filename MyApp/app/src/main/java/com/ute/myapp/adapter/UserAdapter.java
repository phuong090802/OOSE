package com.ute.myapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

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
import com.ute.myapp.model.User;
import com.ute.myapp.util.MyUtil;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private final List<User> userList;
    private Context context;
    private final FireStoreHelper fireStoreHelper;
    private SwitchMaterial switchMaterial;
    private TextInputEditText textInputEditText;
    private TextInputLayout textInputLayoutLockedTime;
    private Spinner spinner;
    private final String[] roleArrays;

    public UserAdapter(Context context, List<User> userList) {
        this.userList = userList;
        this.context = context;
        fireStoreHelper = FireStoreHelper.getInstance();
        roleArrays = context.getResources().getStringArray(R.array.role_name);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateUserList(List<User> userList) {
        this.userList.clear();
        this.userList.addAll(userList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_custom_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewName.setText(MyUtil.fullName(user.getFirstName(), user.getLastName()));
        holder.textViewUserName.setText(user.getUserName());
        holder.textViewEmail.setText(user.getEmail());
        holder.textViewPhone.setText(user.getPhone());
        holder.itemView.setOnLongClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            builder.setTitle(R.string.edit_user);
            TextView textViewName = new TextView(context);
            TextView textViewUserName = new TextView(context);
            TextView textViewEmail = new TextView(context);
            TextView textViewPhone = new TextView(context);
            spinner = new Spinner(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, roleArrays);
            spinner.setAdapter(adapter);
            setValueSpinner(user.getRoleName());

            switchMaterial = new SwitchMaterial(context);
            textInputLayoutLockedTime = new TextInputLayout(context);
            textInputLayoutLockedTime.setVisibility(View.GONE);
            switchMaterial.setText(R.string.genre_status);
            switchMaterial.setChecked(user.isStatus());
            switchMaterial.setOnCheckedChangeListener(onCheckedChangeListener);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG);
            textInputEditText = new TextInputEditText(context);
            textInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            textInputEditText.setHint(R.string.enter_locked_date);
            spinner.setLayoutParams(params);

            textInputLayoutLockedTime.setLayoutParams(params);
            textViewName.setText(MyUtil.fullName(user.getFirstName(), user.getLastName()));
            textViewUserName.setText(user.getUserName());
            textViewEmail.setText(user.getEmail());
            textViewPhone.setText(user.getPhone());
            params.setMargins(Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG, Constant.MARGIN_DIALOG);
            textViewName.setLayoutParams(params);
            textViewUserName.setLayoutParams(params);
            textViewEmail.setLayoutParams(params);
            textViewPhone.setLayoutParams(params);
            switchMaterial.setLayoutParams(params);

            linearLayout.addView(textViewName);
            linearLayout.addView(textViewUserName);
            linearLayout.addView(textViewEmail);
            linearLayout.addView(textViewPhone);
            linearLayout.addView(spinner);
            linearLayout.addView(switchMaterial);
            linearLayout.addView(textInputLayoutLockedTime);
            textInputLayoutLockedTime.addView(textInputEditText);
            builder.setView(textViewName);
            builder.setView(textViewUserName);
            builder.setView(textViewEmail);
            builder.setView(textViewPhone);
            builder.setView(spinner);
            builder.setView(switchMaterial);
            builder.setView(textInputLayoutLockedTime);
            builder.setPositiveButton(R.string.edit_status_user, (dialog, which) -> {
                if (!switchMaterial.isChecked()) {
                    if (TextUtils.isEmpty(textInputEditText.getText())) {
                        Toast.makeText(context, R.string.require, Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(String.valueOf(textInputEditText.getText())) == 0) {
                        Toast.makeText(context, R.string.require_locked_time, Toast.LENGTH_SHORT).show();
                    } else {
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        LocalDateTime newDateTime = currentDateTime.plus(Integer.parseInt(String.valueOf(textInputEditText.getText())), ChronoUnit.DAYS);
                        String roleName = MyUtil.convertRoleName(spinner.getSelectedItem().toString());
                        User newUser = new User(user.getUserId(), user.getFirstName(), user.getLastName(), user.getUserName(), user.getPassword(), user.getPhone(), user.getDateOfBirth(), user.getEmail(), user.getImageUrl(), roleName, switchMaterial.isChecked(), String.valueOf(newDateTime));
                        updateUser(newUser, user.isStatus());
                    }
                } else {
                    String roleName = MyUtil.convertRoleName(spinner.getSelectedItem().toString());
                    User newUser = new User(user.getUserId(), user.getFirstName(), user.getLastName(), user.getUserName(), user.getPassword(), user.getPhone(), user.getDateOfBirth(), user.getEmail(), user.getImageUrl(), roleName, switchMaterial.isChecked(), user.getLockedTime());
                    updateUser(newUser, user.isStatus());
                }
            });
            builder.setNegativeButton(R.string.abort, (dialog, which) -> dialog.dismiss());
            builder.setView(linearLayout);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        });
    }

    private void updateUser(User user, boolean status) {
        fireStoreHelper.updateStatusUser(user, updateUserOnSuccessListener, updateUserOnFailureListener);
        if(status) {
            fireStoreHelper.getAllUserActive(getAllUserOnSuccessListener);
        } else{
            fireStoreHelper.getAllUserInactive(getAllUserOnSuccessListener);
        }
    }

    private final OnSuccessListener<QuerySnapshot> getAllUserOnSuccessListener = queryDocumentSnapshots -> {
        List<User> updateUserList = new ArrayList<>();
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                User userData = documentSnapshot.toObject(User.class);
                if (userData != null) {
                   if(!userData.getRoleName().equals(Constant.ADMIN)) {
                       userData.setUserId(documentSnapshot.getId());
                       updateUserList.add(userData);
                   }
                }
            }
            updateUserList.sort(Comparator.comparing(User::getUserName));
            updateUserList(updateUserList);
        }
    };

    private final OnSuccessListener<Void> updateUserOnSuccessListener = unused -> Toast.makeText(context, R.string.edit_genre_successful, Toast.LENGTH_SHORT).show();

    private final OnFailureListener updateUserOnFailureListener = e -> Toast.makeText(context, R.string.edit_genre_fail, Toast.LENGTH_SHORT).show();


    private void setValueSpinner(String roleName) {
        if (roleName.equalsIgnoreCase(Constant.AUTHOR)) {
            spinner.setSelection(1);
        } else if (roleName.equalsIgnoreCase(Constant.CONTENT_MANAGER)) {
            spinner.setSelection(2);
        }
    }

    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (compoundButton, b) -> {
        if (b) {
            textInputLayoutLockedTime.setVisibility(View.GONE);
        } else {
            textInputLayoutLockedTime.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewUserName;
        private final TextView textViewEmail;
        private final TextView textViewPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewUserName = itemView.findViewById(R.id.text_view_user_name);
            textViewEmail = itemView.findViewById(R.id.text_view_email);
            textViewPhone = itemView.findViewById(R.id.text_view_phone);
        }
    }
}
