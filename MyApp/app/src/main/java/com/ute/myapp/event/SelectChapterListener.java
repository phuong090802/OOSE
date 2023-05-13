package com.ute.myapp.event;

import com.ute.myapp.model.Chapter;

public interface SelectChapterListener {
    void onSelectChapterListener(Chapter chapter, String storyId);
    void onReadChapterListener(Chapter chapter, String storyId);
}
