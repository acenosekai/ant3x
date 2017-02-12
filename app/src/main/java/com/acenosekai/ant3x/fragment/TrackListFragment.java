package com.acenosekai.ant3x.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acenosekai.ant3x.App;
import com.acenosekai.ant3x.Constants;
import com.acenosekai.ant3x.R;
import com.acenosekai.ant3x.factory.BaseFragment;
import com.acenosekai.ant3x.factory.Utility;
import com.acenosekai.ant3x.factory.adapter.DefaultSongAdapter;
import com.acenosekai.ant3x.factory.adapter.TrackAdapter;
import com.acenosekai.ant3x.factory.component.AntRecyclerView;
import com.acenosekai.ant3x.factory.sing.Playback;
import com.acenosekai.ant3x.model.Playlist;
import com.acenosekai.ant3x.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import java8.util.concurrent.ThreadLocalRandom;

/**
 * Created by Acenosekai on 1/29/2017.
 * Rock On
 */

public class TrackListFragment extends BaseFragment {
    @Override
    public BaseFragment backFragment() {
        return new LibraryFragment();
    }

    private AntRecyclerView contentList;
    private TrackAdapter adapter;
    private List<Song> songList;

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View pageView = inflater.inflate(R.layout.fragment_track_list, container, false);
        contentList = (AntRecyclerView) pageView.findViewById(R.id.track_list);

        LinearLayoutManager llm = new LinearLayoutManager(mainActivity);
        contentList.setLayoutManager(llm);
        contentList.setHasFixedSize(true);
        adapter = new TrackAdapter(songList, mainActivity);
        contentList.setAdapter(this.adapter);
        FloatingActionButton fab = (FloatingActionButton) pageView.findViewById(R.id.fab_play_track);

        fab.setOnClickListener(view -> {
            Playback.getInstance().setSongList(songList, (ThreadLocalRandom.current().nextInt(1, songList.size() + 1)) - 1);
            mainActivity.commandService(Constants.ACTION.PLAY);
            mainActivity.changePage(new NowPlayingFragment());
        });


        contentList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.isShown()) {
                    fab.hide();
                }

                if (dy < 0 && !fab.isShown()) {
                    fab.show();
                }

            }
        });


        return pageView;
    }
}
