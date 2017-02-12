package com.acenosekai.ant3x.fragment.library;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acenosekai.ant3x.R;
import com.acenosekai.ant3x.factory.BaseFragment;
import com.acenosekai.ant3x.factory.adapter.DefaultSongAdapter;
import com.acenosekai.ant3x.factory.component.AntRecyclerView;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class DefaultRecycleFragment extends BaseFragment {

    private AntRecyclerView contentList;
    private DefaultSongAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentList = (AntRecyclerView) inflater.inflate(R.layout.view_recycle, container, false);
        if(onScrollListener!=null) {
            contentList.addOnScrollListener(onScrollListener);
        }
        LinearLayoutManager llm = new LinearLayoutManager(mainActivity);
        contentList.setLayoutManager(llm);
        contentList.setHasFixedSize(true);
        contentList.setAdapter(this.adapter);
        return contentList;
    }

    private RecyclerView.OnScrollListener onScrollListener;
    public void onScrollEvent(RecyclerView.OnScrollListener onScrollListener){
        this.onScrollListener = onScrollListener;
    }


    public void setAdapter(DefaultSongAdapter adapter) {
        this.adapter = adapter;
        if (contentList != null) {
            contentList.setAdapter(adapter);
        }
    }

    public DefaultSongAdapter getAdapter() {
        return adapter;
    }

    @Override
    public BaseFragment backFragment() {
        return null;
    }
}
