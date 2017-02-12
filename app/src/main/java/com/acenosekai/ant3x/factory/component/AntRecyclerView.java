package com.acenosekai.ant3x.factory.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.acenosekai.ant3x.R;
import com.acenosekai.ant3x.factory.component.antrecyeclerview.OddEvenRecycler;


/**
 * Created by Acenosekai on 12/4/2016.
 * Rock On
 */

public class AntRecyclerView extends RecyclerView {
    public AntRecyclerView(Context context) {
        super(context);
        this.addItemDecoration(new OddEvenRecycler(ContextCompat.getColor(context, R.color.darker), ContextCompat.getColor(context, R.color.transparent)));
    }

    public AntRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.addItemDecoration(new OddEvenRecycler(ContextCompat.getColor(context, R.color.darker), ContextCompat.getColor(context, R.color.transparent)));

    }

    public AntRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.addItemDecoration(new OddEvenRecycler(ContextCompat.getColor(context, R.color.darker), ContextCompat.getColor(context, R.color.transparent)));
    }
}
