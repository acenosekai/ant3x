package com.acenosekai.ant3x.factory.adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;

import com.acenosekai.ant3x.App;
import com.acenosekai.ant3x.MainActivity;
import com.acenosekai.ant3x.R;
import com.acenosekai.ant3x.factory.Utility;
import com.acenosekai.ant3x.factory.component.DefaultItemHolder;
import com.acenosekai.ant3x.fragment.TrackListFragment;
import com.acenosekai.ant3x.model.Song;

import java.io.IOException;
import java.util.List;

/**
 * Created by Acenosekai on 1/28/2017.
 * Rock On
 */

public class AlbumAdapter extends DefaultSongAdapter {

    public AlbumAdapter(List<Song> songs, MainActivity mainActivity) {
        super(songs,mainActivity);
    }

    @Override
    public List<String> buildDirSong() {
        return null;
    }

    @Override
    public void onBindViewHolder(DefaultItemHolder holder, int position) {
        final Song o = songs.get(position);
        holder.getListText().setText(o.getAlbum());

        List<Song> itemSongs = App.getInstance().getR().where(Song.class).equalTo("album", o.getAlbum()).findAllSorted("title");
        String songTotal = itemSongs.size() + " tracks";
        holder.getListDesc().setText(o.getArtist());
        holder.getListLength().setText(songTotal);
        holder.getCover().setImageBitmap(null);
        final String albumKey = o.getAlbumKey();

            Utility.loadCoverSmall(o.getDirectory(), hasCover ->

                mainActivity.runOnUiThread(() -> { if (hasCover) {
                    try {
                        holder.getCover().setImageBitmap(App.getInstance().getCoverSmallCache().getBitmap(albumKey).getBitmap());
                    } catch (IOException e) {
                        holder.getCover().setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.default_cover_small));
                        e.printStackTrace();
                    }
                } else {
                    holder.getCover().setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.default_cover_small));
                }})

            );

        holder.getWrap().setOnClickListener(view -> {
            TrackListFragment fragment = new TrackListFragment();
            fragment.setSongList(itemSongs);
            mainActivity.changePage(fragment);
        });


        holder.getCover().setVisibility(View.VISIBLE);
        holder.getIconDefault().setVisibility(View.GONE);
    }
}
