package com.acenosekai.ant3x.fragment;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acenosekai.ant3x.App;
import com.acenosekai.ant3x.Constants;
import com.acenosekai.ant3x.R;
import com.acenosekai.ant3x.factory.BaseFragment;
import com.acenosekai.ant3x.factory.Utility;
import com.acenosekai.ant3x.factory.adapter.AlbumAdapter;
import com.acenosekai.ant3x.factory.adapter.ArtistAdapter;
import com.acenosekai.ant3x.factory.adapter.GenreAdapter;
import com.acenosekai.ant3x.factory.adapter.PagerAdapter;
import com.acenosekai.ant3x.factory.adapter.TrackAdapter;
import com.acenosekai.ant3x.factory.sing.Playback;
import com.acenosekai.ant3x.fragment.library.DefaultRecycleFragment;
import com.acenosekai.ant3x.model.Directory;
import com.acenosekai.ant3x.model.General;
import com.acenosekai.ant3x.model.Playlist;
import com.acenosekai.ant3x.model.Song;
import com.eftimoff.viewpagertransformers.CubeOutTransformer;
import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsImageView;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import java8.util.concurrent.ThreadLocalRandom;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class LibraryFragment extends BaseFragment {
    private View pageView;
    private Playback.PlaybackOnLoad playbackOnLoad;
    private Playback.PlaybackOnPlay playbackOnPlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.pageView = inflater.inflate(R.layout.fragment_library, container, false);
        final ViewPager pager = (ViewPager) pageView.findViewById(R.id.library_pager);

        List<Fragment> fl = new ArrayList<>();
        final Fragment trackFragment = new DefaultRecycleFragment();
        final Fragment artistFragment = new DefaultRecycleFragment();
        final Fragment albumFragment = new DefaultRecycleFragment();
        final Fragment genreFragment = new DefaultRecycleFragment();

        fl.add(trackFragment);
        fl.add(artistFragment);
        fl.add(albumFragment);
        fl.add(genreFragment);

        FloatingActionButton fab = (FloatingActionButton) pageView.findViewById(R.id.fab_play);

        for (Fragment f : fl) {
            DefaultRecycleFragment fr = (DefaultRecycleFragment) f;
            fr.onScrollEvent(new RecyclerView.OnScrollListener() {
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
        }

        PagerAdapter mAdapter = new PagerAdapter(getChildFragmentManager(), fl);
        pager.setAdapter(mAdapter);

        pager.setPageTransformer(true, new CubeOutTransformer());

        List<String> mTitleDataList = new ArrayList<>();
        mTitleDataList.add("Track");
        mTitleDataList.add("Artist");
        mTitleDataList.add("Album");
        mTitleDataList.add("Genre");

        MagicIndicator magicIndicator = (MagicIndicator) pageView.findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(mainActivity);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitleDataList == null ? 0 : mTitleDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int i) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(mainActivity, R.color.primary_light));
                colorTransitionPagerTitleView.setSelectedColor(Color.WHITE);
                colorTransitionPagerTitleView.setTextSize(18);
                colorTransitionPagerTitleView.setPadding(30, 5, 30, 5);
                colorTransitionPagerTitleView.setText(mTitleDataList.get(i));
                colorTransitionPagerTitleView.setOnClickListener(view -> pager.setCurrentItem(i));
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_MATCH_EDGE);
                indicator.setColors(ContextCompat.getColor(mainActivity, R.color.primary_dark));
                return indicator;
            }
        });

        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, pager);

        TrackAdapter trackAdapter = new TrackAdapter(new ArrayList<>(), mainActivity);
        ((DefaultRecycleFragment) trackFragment).setAdapter(trackAdapter);

        ArtistAdapter artistAdapter = new ArtistAdapter(new ArrayList<>(), mainActivity);
        ((DefaultRecycleFragment) artistFragment).setAdapter(artistAdapter);

        AlbumAdapter albumAdapter = new AlbumAdapter(new ArrayList<>(), mainActivity);
        ((DefaultRecycleFragment) albumFragment).setAdapter(albumAdapter);

        GenreAdapter genreAdapter = new GenreAdapter(new ArrayList<>(), mainActivity);
        ((DefaultRecycleFragment) genreFragment).setAdapter(genreAdapter);

        RealmResults<Directory> dirList = App.getInstance().getR().where(Directory.class).findAll();

        RealmResults<Song> songList = App.getInstance().getR().where(Song.class).findAllSorted("title");
        App.i("song size => " + songList.size());
        trackAdapter.addAllSong(songList);

        Observable.from(songList.sort("artist")).distinct(Song::getArtist).toList().subscribe(artistAdapter::addAllSong);
        Observable.from(songList.sort("album")).distinct(Song::getAlbum).toList().subscribe(albumAdapter::addAllSong);
        Observable.from(songList.sort("genre")).distinct(Song::getGenre).toList().subscribe(genreAdapter::addAllSong);

        pageView.findViewById(R.id.fab_play).setOnClickListener(view -> {
            List<Song> songs = App.getInstance().getR().where(Song.class).findAllSorted("title");
            int randomNum = ThreadLocalRandom.current().nextInt(1, songs.size() + 1);
            Playback.getInstance().setSongList(songs,randomNum-1);
            mainActivity.commandService(Constants.ACTION.PLAY);
            mainActivity.changePage(new NowPlayingFragment());
        });

        playbackOnLoad = Playback.getInstance().addPlaybackOnLoad(p ->
                mainActivity.runOnUiThread(() ->
                {
                    Realm r = Realm.getInstance(App.realmConfig());
                    Song s = r.where(Song.class).equalTo("directory", p.getNowPlaying()).findFirst();
                    ((TextView) pageView.findViewById(R.id.mini_player_artist)).setText(s.getArtist());
                    ((TextView) pageView.findViewById(R.id.mini_player_title)).setText(s.getTitle());
                    final String albumKey = s.getAlbumKey();
                    Utility.loadCoverSmall(s.getDirectory(), hasCover -> {
                        mainActivity.runOnUiThread(() ->
                        {
                            if (hasCover) {
                                try {
                                    ((ImageView) pageView.findViewById(R.id.mini_player_cover)).setImageBitmap(App.getInstance().getCoverSmallCache().getBitmap(albumKey).getBitmap());
                                } catch (IOException e) {
                                    ((ImageView) pageView.findViewById(R.id.mini_player_cover)).setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.default_cover));
                                    e.printStackTrace();
                                }

                            } else {
                                ((ImageView) pageView.findViewById(R.id.mini_player_cover)).setImageDrawable(ContextCompat.getDrawable(mainActivity, R.drawable.default_cover));
                            }
                        });
                    });

                    if (Playback.getInstance().isPlaying())
                        ((IconicsImageView) pageView.findViewById(R.id.mini_player_play)).setIcon(CommunityMaterial.Icon.cmd_pause);
                    else
                        ((IconicsImageView) pageView.findViewById(R.id.mini_player_play)).setIcon(CommunityMaterial.Icon.cmd_play);
                    r.close();
                })
        );

        playbackOnPlay = Playback.getInstance().addPlaybackOnPlay(p ->
                mainActivity.runOnUiThread(() -> {
                    float progress = (float) p.getPlayer().getBytesPosition() / (float) p.getPlayer().getBytesTotal();
                    float altProgress = 1f - progress;
                    pageView.findViewById(R.id.mini_player_progress).setLayoutParams(new LinearLayout.LayoutParams(
                            ViewPager.LayoutParams.MATCH_PARENT,
                            ViewPager.LayoutParams.MATCH_PARENT, altProgress));
                    pageView.findViewById(R.id.mini_player_progress_alt).setLayoutParams(new LinearLayout.LayoutParams(
                            ViewPager.LayoutParams.MATCH_PARENT,
                            ViewPager.LayoutParams.MATCH_PARENT, progress));
                })
        );

        pageView.findViewById(R.id.mini_player_play).setOnClickListener(view -> {
            if (Playback.getInstance().isPlaying())
                mainActivity.commandService(Constants.ACTION.PAUSE);
            else mainActivity.commandService(Constants.ACTION.PLAY);
        });

        pageView.findViewById(R.id.mini_player_wrap).setOnClickListener(view -> mainActivity.changePage(new NowPlayingFragment()));


        Playback.getInstance().load();

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
        return null;
    }
}
