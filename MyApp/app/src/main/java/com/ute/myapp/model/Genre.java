package com.ute.myapp.model;

import androidx.annotation.NonNull;

public class Genre {
    private String genreId;
    private String genreName;
    private boolean status;

    public Genre() {
    }

    public Genre(String genreId, String genreName, boolean status) {
        this.genreId = genreId;
        this.genreName = genreName;
        this.status = status;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getGenreName() {
        return genreName;
    }

    @NonNull
    @Override
    public String toString() {
        return "Genre{" +
                "genreId='" + genreId + '\'' +
                ", genreName='" + genreName + '\'' +
                ", status=" + status +
                '}';
    }
}
