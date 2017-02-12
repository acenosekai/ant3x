package com.acenosekai.ant3x.factory.adapter;

import android.view.View;

import com.acenosekai.ant3x.App;
import com.acenosekai.ant3x.Constants;
import com.acenosekai.ant3x.MainActivity;
import com.acenosekai.ant3x.factory.Utility;
import com.acenosekai.ant3x.factory.component.DefaultItemHolder;
import com.acenosekai.ant3x.factory.sing.Playback;
import com.acenosekai.ant3x.fragment.NowPlayingFragment;
import com.acenosekai.ant3x.model.Playlist;
import com.acenosekai.ant3x.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class TrackAdapter extends DefaultSongAdapter {


    public TrackAdapter(List<Song> songs, MainActivity mainActivity) {
        super(songs, mainActivity);
    }

    @Override
    public List<String> buildDirSong() {
        return StreamSupport.stream(songs).map(Song::getDirectory).collect(Collectors.toList());
    }

    @Override
    public void onBindViewHolder(DefaultItemHolder holder, int position) {
        final Song o = songs.get(position);
        holder.getListText().setText(o.getTitle());
        holder.getListDesc().setText(o.getArtist());
        holder.getListLength().setText(Utility.intToHHmm(o.getDuration()));
        holder.getCover().setVisibility(View.GONE);
        holder.getIconDefault().setVisibility(View.GONE);

        holder.getWrap().setOnClickListener(view -> {
            Playback.getInstance().setSongList(songs,position);
            mainActivity.commandService(Constants.ACTION.PLAY);
            mainActivity.changePage(new NowPlayingFragment());
        });
    }
}
