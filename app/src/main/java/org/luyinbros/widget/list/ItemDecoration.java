package org.luyinbros.widget.list;

import android.graphics.Canvas;
import android.graphics.Rect;

public class ItemDecoration<ParentView> {

    public void onDrawOver(Canvas c, ParentView parent) {
    }

    public void getItemOffsets(Rect outRect, int itemPosition, ParentView parent) {
        outRect.set(0, 0, 0, 0);
    }

    public void onDraw(Canvas c, ParentView parent) {
    }
}
