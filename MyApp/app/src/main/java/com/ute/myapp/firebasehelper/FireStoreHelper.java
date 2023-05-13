package com.ute.myapp.firebasehelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ute.myapp.constant.Constant;
import com.ute.myapp.model.Chapter;
import com.ute.myapp.model.Genre;
import com.ute.myapp.model.User;
import com.ute.myapp.util.MyUtil;

import java.util.Map;

public class FireStoreHelper {
    private final FirebaseFirestore firebaseFirestore;
    private static FireStoreHelper instance;

    private FireStoreHelper() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public static FireStoreHelper getInstance() {
        if (instance == null) {
            instance = new FireStoreHelper();
        }
        return instance;
    }

    public void login(String username, OnCompleteListener<QuerySnapshot> onSuccessListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.USERS);
        Query query = collectionReference.whereEqualTo(Constant.USERNAME, username);
        query.get().
                addOnCompleteListener(onSuccessListener);
    }

    public void checkGenreName(String genreName, OnCompleteListener<QuerySnapshot> onSuccessListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.GENRES);
        Query query = collectionReference.whereEqualTo(Constant.GENRE_NAME, genreName);
        query.get().addOnCompleteListener(onSuccessListener);
    }

    public void createAccount(User user, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.USERS);
        collectionReference.add(MyUtil.convertUserToMap(user)).addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void createGenre(Genre genre, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.GENRES);
        collectionReference.add(MyUtil.convertGenreToMap(genre)).addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getAllGenreActive(OnSuccessListener<QuerySnapshot> onSuccessListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.GENRES);
        Query query = collectionReference.whereEqualTo(Constant.GENRE_STATUS, true);
        query.get().addOnSuccessListener(onSuccessListener);
    }

    public void getAllGenreInactive(OnSuccessListener<QuerySnapshot> onSuccessListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.GENRES);
        Query query = collectionReference.whereEqualTo(Constant.GENRE_STATUS, false);
        query.get().addOnSuccessListener(onSuccessListener);
    }

    public void updateGenre(Genre genre, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        Genre updateGenre = new Genre();
        updateGenre.setGenreName(genre.getGenreName());
        updateGenre.setStatus(genre.isStatus());
        DocumentReference documentReference = firebaseFirestore.collection(Constant.GENRES).document(genre.getGenreId());
        documentReference.set(MyUtil.convertGenreToMap(updateGenre))
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getAllUserActive(OnSuccessListener<QuerySnapshot> onSuccessListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.USERS);
        Query query = collectionReference.whereEqualTo(Constant.USER_STATUS, true);
        query.get().addOnSuccessListener(onSuccessListener);
    }

    public void getAllUserInactive(OnSuccessListener<QuerySnapshot> onSuccessListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.USERS);
        Query query = collectionReference.whereEqualTo(Constant.USER_STATUS, false);
        query.get().addOnSuccessListener(onSuccessListener);
    }

    public void updateStatusUser(User user, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        DocumentReference documentReference = firebaseFirestore.collection(Constant.USERS).document(user.getUserId());
        documentReference.update(MyUtil.convertUserToMapUpdate(user))
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void updateStatusUser(String userId) {
        DocumentReference documentReference = firebaseFirestore.collection(Constant.USERS).document(userId);
        documentReference.update(Constant.USER_STATUS, true);

    }

    public void createStory(Map<String, Object> story, OnSuccessListener<DocumentReference> onSuccessListener, OnFailureListener onFailureListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.STORIES);
        collectionReference.add(story).addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getAllStory(OnSuccessListener<QuerySnapshot> onSuccessListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.STORIES);
        Query query = collectionReference.whereEqualTo(Constant.APPROVE, true);
        query.get().addOnSuccessListener(onSuccessListener);
    }

    public void updateStory(String storyId, Map<String, Object> map, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        DocumentReference documentReference = firebaseFirestore.collection(Constant.STORIES).document(storyId);
        documentReference.set(map)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void getStoryWithId(String storyId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        DocumentReference documentReference = firebaseFirestore.collection(Constant.STORIES).document(storyId);
        documentReference.get().addOnCompleteListener(onCompleteListener);
    }

    public void updateStory(String storyId, Chapter chapter, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        DocumentReference documentReference = firebaseFirestore.collection(Constant.STORIES).document(storyId);
        String nameChapter = MyUtil.convertChapter(chapter.getChapter());
        String content = chapter.getContent();
        documentReference.update(nameChapter, content).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);

    }

    public void changePassword(String userId, String newPassword, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        DocumentReference documentReference = firebaseFirestore.collection(Constant.USERS).document(userId);
        documentReference.update(Constant.PASSWORD, MyUtil.hashedPassword(newPassword)).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void changeInfo(String userId, User user, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        DocumentReference documentReference = firebaseFirestore.collection(Constant.USERS).document(userId);
        documentReference.set(MyUtil.convertUserToMap(user)).addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void getUrlAvatar(String userId, OnSuccessListener<DocumentSnapshot> onSuccessListener) {
        DocumentReference docRef = firebaseFirestore.collection(Constant.USERS).document(userId);
        docRef.get().addOnSuccessListener(onSuccessListener);
    }

    public void getAllUser(OnSuccessListener<QuerySnapshot> onSuccessListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.USERS);
        collectionReference.get().addOnSuccessListener(onSuccessListener);
    }

    public void editStory(String storyId, Map<String, Object> map, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        DocumentReference documentReference = firebaseFirestore.collection(Constant.STORIES).document(storyId);
        documentReference.update(map)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void deleteStory(String storyId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        DocumentReference documentReference = firebaseFirestore.collection(Constant.STORIES).document(storyId);
        documentReference.delete().addOnSuccessListener(onSuccessListener).addOnFailureListener(onFailureListener);
    }

    public void updateRoleAuthor(String userId) {
        DocumentReference documentReference = firebaseFirestore.collection(Constant.USERS).document(userId);
        documentReference.update(Constant.ROLE_NAME, Constant.AUTHOR);

    }

    public void getAllNewStory(OnSuccessListener<QuerySnapshot> onSuccessListener) {
        CollectionReference collectionReference = firebaseFirestore.collection(Constant.STORIES);
        Query query = collectionReference.whereEqualTo(Constant.APPROVE, false);
        query.get().addOnSuccessListener(onSuccessListener);
    }

    public void updateApproveStory(String storyId) {
        DocumentReference documentReference = firebaseFirestore.collection(Constant.STORIES).document(storyId);
        documentReference.update(Constant.APPROVE, true);

    }
}
