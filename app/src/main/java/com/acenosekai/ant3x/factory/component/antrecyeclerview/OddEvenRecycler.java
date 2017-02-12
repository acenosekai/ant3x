package com.acenosekai.ant3x.factory.component.antrecyeclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class OddEvenRecycler extends  RecyclerView.ItemDecoration {
    private final int mOddBackground;
    private final int mEvenBackground;

    public OddEvenRecycler(int oddBackground, int evenBackground) {
        mOddBackground = oddBackground;
        mEvenBackground = evenBackground;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        view.setBackgroundColor(position % 2 == 0 ? mEvenBackground : mOddBackground);
    }
}
