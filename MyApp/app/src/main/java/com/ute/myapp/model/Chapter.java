package com.ute.myapp.model;

import androidx.annotation.NonNull;

public class Chapter {
    private final String chapter;
    private String content;

    public Chapter(String chapter, String content) {
        this.chapter = chapter;
        this.content = content;
    }

    public String getChapter() {
        return chapter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NonNull
    @Override
    public String toString() {
        return "Chapter{" +
                "chapter='" + chapter + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
