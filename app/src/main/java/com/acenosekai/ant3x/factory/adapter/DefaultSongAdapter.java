package com.acenosekai.ant3x.factory.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acenosekai.ant3x.App;
import com.acenosekai.ant3x.MainActivity;
import com.acenosekai.ant3x.R;
import com.acenosekai.ant3x.factory.component.DefaultItemHolder;
import com.acenosekai.ant3x.model.Song;

import java.util.List;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public abstract class DefaultSongAdapter extends RecyclerView.Adapter<DefaultItemHolder> {
    protected MainActivity mainActivity;
    protected List<Song> songs;


    public DefaultSongAdapter(List<Song> songs, MainActivity mainActivity) {
        this.songs = songs;
        this.mainActivity = mainActivity;
    }

    public void addSong(Song s){
        songs.add(s);
        this.notifyDataSetChanged();
    }

    public void addAllSong(List<Song> s){
        songs.addAll(s);
        this.notifyDataSetChanged();
    }

    @Override
    public DefaultItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_default, parent, false);
        return new DefaultItemHolder(v);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public abstract List<String> buildDirSong();

}
