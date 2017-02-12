package com.acenosekai.ant3x.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.acenosekai.ant3x.App;
import com.acenosekai.ant3x.Constants;
import com.acenosekai.ant3x.R;
import com.acenosekai.ant3x.factory.BaseFragment;
import com.acenosekai.ant3x.factory.Utility;
import com.acenosekai.ant3x.factory.sing.Playback;
import com.acenosekai.ant3x.model.Song;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import io.realm.Realm;

/**
 * Created by Acenosekai on 1/8/2017.
 * Rock On
 */

public class NowPlayingFragment extends BaseFragment {
    private View pageView;
    private Playback.PlaybackOnLoad playbackOnLoad;
    private Playback.PlaybackOnPlay playbackOnPlay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        pageView = inflater.inflate(R.layout.fragment_now_playing, container, false);
        Playback.getInstance();
        this.playbackOnLoad = Playback.getInstance().addPlaybackOnLoad(p -> {
                    mainActivity.runOnUiThread(() -> {

                        Realm r = Realm.getInstance(App.realmConfig());
                        Song s = r.where(Song.class).equalTo("directory", p.getNowPlaying()).findFirst();

                        ((TextView) pageView.findViewById(R.id.playback_album)).setText(s.getAlbum());
                        ((TextView) pageView.findViewById(R.id.playback_artist)).setText(s.getArtist());
                        ((TextView) pageView.findViewById(R.id.playback_title)).setText(s.getTitle());
                        ((TextView) pageView.findViewById(R.id.playback_seeker_total)).setText(Utility.intToHHmm(s.getDuration()));
                        IconicsImageView iconPlay = (IconicsImageView) pageView.findViewById(R.id.playback_play);
                        if (p.isPlaying()) {
                            iconPlay.setIcon(CommunityMaterial.Icon.cmd_pause_circle_outline);
                        } else {
                            iconPlay.setIcon(CommunityMaterial.Icon.cmd_play_circle_outline);
                        }

                        IconicsImageView iconShuffle = (IconicsImageView) pageView.findViewById(R.id.playback_shuffle);
                        if (p.isShuffle()) {
                            iconShuffle.setColor(ContextCompat.getColor(mainActivity, R.color.primary));
                        } else {
                            iconShuffle.setColor(ContextCompat.getColor(mainActivity, R.color.accent));
                        }

                        IconicsImageView iconRepeat = (IconicsImageView) pageView.findViewById(R.id.playback_repeat);

                        switch (p.getRepeat()) {
                            case OFF:
                                iconRepeat.setIcon(CommunityMaterial.Icon.cmd_repeat);
                                iconRepeat.setColor(ContextCompat.getColor(mainActivity, R.color.primary));
                                break;
                            case ALL:
                                iconRepeat.setIcon(CommunityMaterial.Icon.cmd_repeat);
                                iconRepeat.setColor(ContextCompat.getColor(mainActivity, R.color.accent));
                                break;
                            case ONE:
                                iconRepeat.setIcon(CommunityMaterial.Icon.cmd_repeat_once);
                                iconRepeat.setColor(ContextCompat.getColor(mainActivity, R.color.accent));
                                break;
                        }

                        ImageView cover = (ImageView) pageView.findViewById(R.id.playback_cover);
                        final String albumKey = s.getAlbumKey();
                        Utility.loadCoverAlbum(s.getDirectory(), hasCover -> {
                            mainActivity.runOnUiThread(() -> {
                                if (hasCover) {
                                    try {
                                        cover.setImageBitmap(App.getInstance().getCoverCache().getBitmap(albumKey).getBitmap());
                                    } catch (IOException e) {
                                        cover.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.default_cover));
                                    }
                                } else {
                                    cover.setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.default_cover));
                                }
                            });
                        });

                        r.close();
                    });


                }
        );

        AppCompatSeekBar seekBar = (AppCompatSeekBar) pageView.findViewById(R.id.playback_seeker);

        playbackOnPlay = Playback.getInstance().addPlaybackOnPlay(p ->
                mainActivity.runOnUiThread(() -> {
                    seekBar.setProgress((int) p.getPlayer().getBytesPosition());
                    if (seekBar.getMax() != (int) p.getPlayer().getBytesTotal()) {
                        seekBar.setMax((int) p.getPlayer().getBytesTotal());
                    }
                })
        );

        Playback.getInstance().load();

        pageView.findViewById(R.id.playback_next).setOnClickListener(v -> mainActivity.commandService(Constants.ACTION.NEXT));
        pageView.findViewById(R.id.playback_prev).setOnClickListener(v -> mainActivity.commandService(Constants.ACTION.PREV));
        pageView.findViewById(R.id.playback_shuffle).setOnClickListener(v -> Playback.getInstance().shuffle());
        pageView.findViewById(R.id.playback_repeat).setOnClickListener(v -> Playback.getInstance().repeat());
        pageView.findViewById(R.id.playback_play).setOnClickListener(v -> {
            if (Playback.getInstance().isPlaying()) mainActivity.commandService(Constants.ACTION.PAUSE);
            else mainActivity.commandService(Constants.ACTION.PLAY);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float di = (float) Playback.getInstance().getPlayer().getSecondsTotal() * ((float)i/(float) Playback.getInstance().getPlayer().getBytesTotal());
                ((TextView) pageView.findViewById(R.id.playback_seeker_count)).setText(Utility.intToHHmm(Math.round(di) * 1000));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Playback.getInstance().play(Long.valueOf(seekBar.getProgress()));
            }
        });
//
        final ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);


        final ScheduledFuture<?>[] seekbarHandler = new ScheduledFuture<?>[1];

        return pageView;
    }



    @Override
    public void onDestroyView() {
        Playback.getInstance().removePlaybackOnLoad(this.playbackOnLoad);
        Playback.getInstance().removePlaybackOnPlay(this.playbackOnPlay);
        super.onDestroyView();
    }

    @Override
    public BaseFragment backFragment() {
        return new LibraryFragment();
    }
}
