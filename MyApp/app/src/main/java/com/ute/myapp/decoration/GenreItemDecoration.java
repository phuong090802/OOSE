package com.ute.myapp.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ute.myapp.constant.Constant;

public class GenreItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public GenreItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        outRect.bottom = space;
        outRect.top = (position == 0 || position == 1) ? space : 0;
        outRect.left = (position % Constant.COLUMNS_GENRE != 0) ? space : 0;
        outRect.right = (position % Constant.COLUMNS_GENRE == 0) ? space : 0;
    }
}
