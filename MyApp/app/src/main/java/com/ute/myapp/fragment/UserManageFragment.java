package com.ute.myapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ute.myapp.R;
import com.ute.myapp.activity.SignUpActivity;
import com.ute.myapp.adapter.UserAdapter;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.decoration.GenreItemDecorationAdmin;
import com.ute.myapp.firebasehelper.FireStoreHelper;
import com.ute.myapp.model.User;
import com.ute.myapp.util.MyUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class UserManageFragment extends Fragment {
    private boolean status;
    private List<User> userList;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    GridLayoutManager gridLayoutManager;
    private Context context;
    FloatingActionButton floatingActionButtonAddUser;
    private FireStoreHelper fireStoreHelper;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.fragment_user_manage, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        fireStoreHelper = FireStoreHelper.getInstance();
        Bundle bundle = getArguments();
        if (bundle != null) {
            String json = (String) bundle.get(Constant.LIST_USER);
            status = bundle.getBoolean(Constant.USER_STATUS);
            if (json != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<User>>() {
                }.getType();
                userList = gson.fromJson(json, type);
            }
        }
        floatingActionButtonAddUser = view.findViewById(R.id.floating_action_button_add_user);
        floatingActionButtonAddUser.setOnClickListener(floatingActionButtonAddUserOnClickListener);
        searchView = view.findViewById(R.id.search_view_user_manage);
        searchView.setOnQueryTextListener(onQueryTextListener);

        recyclerView = view.findViewById(R.id.recycler_view_user);
        gridLayoutManager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GenreItemDecorationAdmin(22));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        userAdapter = new UserAdapter(context, userList);
        recyclerView.setAdapter(userAdapter);
    }

    private final SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (!TextUtils.isEmpty(newText)) {
                List<User> listFilterUser = userList.stream().filter(user ->
                        user.getUserName().contains(newText) ||
                                user.getEmail().contains(newText) ||
                                user.getPhone().contains(newText)
                ).distinct().collect(Collectors.toList());
                userAdapter = new UserAdapter(context, listFilterUser);
                recyclerView.setAdapter(userAdapter);
                return true;

            } else {
                reloadData();
                return false;
            }
        }
    };

    private void reloadData() {
        if (status) {
            fireStoreHelper.getAllUserActive(getAllUserOnSuccessListener);
        } else {
            fireStoreHelper.getAllUserInactive(getAllUserOnSuccessListener);
        }
    }

    private final OnSuccessListener<QuerySnapshot> getAllUserOnSuccessListener = queryDocumentSnapshots -> {
        List<User> updateData = new ArrayList<>();
        if (!queryDocumentSnapshots.isEmpty()) {
            List<DocumentSnapshot> documentSnapshotList = queryDocumentSnapshots.getDocuments();
            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    user.setUserId(documentSnapshot.getId());
                    updateData.add(user);
                }
            }
            userAdapter.updateUserList(MyUtil.filterListUser(updateData));
        }
    };

    private final View.OnClickListener floatingActionButtonAddUserOnClickListener = view -> {
        Intent intent = new Intent(context, SignUpActivity.class);
        intent.putExtra(Constant.REGISTER, false);
        intent.putExtra(Constant.CREATE_ACTIVE_ACCOUNT, status);
        startActivity(intent);
    };

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }
}