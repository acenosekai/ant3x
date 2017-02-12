package com.acenosekai.ant3x.factory.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.acenosekai.ant3x.App;
import com.acenosekai.ant3x.MainActivity;
import com.acenosekai.ant3x.factory.component.DefaultItemHolder;
import com.acenosekai.ant3x.fragment.TrackListFragment;
import com.acenosekai.ant3x.model.Song;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class ArtistAdapter extends DefaultSongAdapter {

    public ArtistAdapter(List<Song> songs, MainActivity mainActivity) {
        super(songs,mainActivity);
    }

    @Override
    public List<String> buildDirSong() {
        return null;
    }

    @Override
    public void onBindViewHolder(DefaultItemHolder holder, int position) {
        final Song o = songs.get(position);
        holder.getListText().setText(o.getArtist());
        List<Song> itemSongs = App.getInstance().getR().where(Song.class).equalTo("artist", o.getArtist()).findAllSorted("title");

        String songTotal = itemSongs.size() + " tracks";
        holder.getListDesc().setText(songTotal);
        holder.getListLength().setVisibility(View.GONE);
        holder.getCover().setVisibility(View.GONE);
        holder.getIconDefault().setVisibility(View.GONE);

        holder.getWrap().setOnClickListener(view -> {
            TrackListFragment fragment = new TrackListFragment();
            fragment.setSongList(itemSongs);
            mainActivity.changePage(fragment);
        });
    }
}
