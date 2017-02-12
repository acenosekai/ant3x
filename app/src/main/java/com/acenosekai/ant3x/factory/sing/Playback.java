package com.acenosekai.ant3x.factory.sing;

import android.widget.Toast;

import com.acenosekai.ant3x.App;
import com.acenosekai.ant3x.factory.Utility;
import com.acenosekai.ant3x.factory.Worker;
import com.acenosekai.ant3x.model.General;
import com.acenosekai.ant3x.model.Playlist;
import com.acenosekai.ant3x.model.Song;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

/**
 * Created by Acenosekai on 1/8/2017.
 * Rock On
 */

public class Playback {

    private int nowPlayingIdx;
    private String nowPlaying;
    private boolean shuffle = false;
    private boolean playing;
    private Playlist playlist;
    private List<String> songList;
    private Repeat repeat = Repeat.OFF;
    private App app;
    private Player player;
    private long position = 0;

    private static Playback instance;

    private Playback() {
    }

    public static synchronized Playback getInstance() {
        if (instance == null) {
            Playback playback = new Playback();
            playback.app = App.getInstance();
            Realm r = Realm.getInstance(App.realmConfig());
            General shuffleSetting = r.where(General.class).equalTo("key", "PLAYBACK_SHUFFLE").findFirst();
            playback.shuffle = Boolean.valueOf(shuffleSetting.getValue());
            General repeatSetting = r.where(General.class).equalTo("key", "PLAYBACK_REPEAT").findFirst();
            playback.repeat = Repeat.valueOf(repeatSetting.getValue());
            r.executeTransaction(realm -> {
                playback.playlist = realm.where(Playlist.class).equalTo("name", "___queue").findFirst();
                if (playback.playlist == null) {
                    playback.playlist = realm.createObject(Playlist.class, "___queue");
                    if (StringUtils.isEmpty(playback.playlist.getOrderedList())) {
                        List<String> strings = Utility.buildLisDir(realm.where(Song.class).findAll());
                        playback.playlist.setOrderedList(Utility.serializeDir(strings));
                        List<String> songsShuffle = new ArrayList<>();
                        songsShuffle.addAll(strings);
                        Collections.shuffle(songsShuffle);
                        playback.playlist.setShuffleList(Utility.serializeDir(strings));
                    }


                }
                if (playback.shuffle) {
                    playback.songList = Utility.deserializeDir(playback.playlist.getShuffleList());
                } else {
                    playback.songList = Utility.deserializeDir(playback.playlist.getOrderedList());
                }
                General nowplayingSetting = realm.where(General.class).equalTo("key", "PLAYBACK_PLAY_INDEX").findFirst();
                if (nowplayingSetting == null) {
                    nowplayingSetting = realm.createObject(General.class, "PLAYBACK_PLAY_INDEX");
                    nowplayingSetting.setValue("0");
                }
                playback.nowPlayingIdx = Integer.parseInt(nowplayingSetting.getValue());
                playback.nowPlaying = playback.songList.get(playback.nowPlayingIdx);
            });
            r.close();
            instance = playback;
        }
        return instance;
    }


    public Player getPlayer() {
        return player;
    }


    public void setSongList(List<Song> orderedSongList, int position) {
        Realm r = Realm.getInstance(App.realmConfig());
        List<String> strings = Utility.buildLisDir(orderedSongList);
        List<String> songsShuffle = new ArrayList<>();
        songsShuffle.addAll(strings);
        Collections.shuffle(songsShuffle);
        int index = position;

        if (this.shuffle) {
            songList = songsShuffle;
            index = songsShuffle.indexOf(strings.get(position));
        } else {
            songList = strings;
        }

        r.executeTransaction(realm -> {
            Playlist playlist = realm.where(Playlist.class).equalTo("name", "___queue").findFirst();
            if (playlist == null) {
                playlist = realm.createObject(Playlist.class, "___queue");
            }
            playlist.setOrderedList(Utility.serializeDir(strings));
            playlist.setShuffleList(Utility.serializeDir(strings));

            General nowplayingSetting = realm.where(General.class).equalTo("key", "PLAYBACK_SHUFFLE").findFirst();
            nowplayingSetting.setValue(String.valueOf(this.shuffle));
        });

        setNowPlayingIdx(index);
        setPosition(0);
        r.close();
    }

    public interface PlaybackOnLoad {
        void onLoad(Playback p);
    }

    public interface PlaybackOnPlay {
        void onPlay(Playback p);
    }

    private List<PlaybackOnLoad> playbackOnLoads = new ArrayList<>();
    private List<PlaybackOnPlay> playbackOnPlays = new ArrayList<>();

    public PlaybackOnPlay addPlaybackOnPlay(PlaybackOnPlay playbackOnPlay) {
        this.playbackOnPlays.add(playbackOnPlay);
        return playbackOnPlay;
    }

    public void removePlaybackOnPlay(PlaybackOnPlay playbackOnPlay) {
        this.playbackOnPlays.remove(playbackOnPlay);
    }

    public PlaybackOnLoad addPlaybackOnLoad(PlaybackOnLoad playbackOnLoad) {
        this.playbackOnLoads.add(playbackOnLoad);
        return playbackOnLoad;
    }

    public void removePlaybackOnLoad(PlaybackOnLoad playbackOnLoad) {
        this.playbackOnLoads.remove(playbackOnLoad);
    }

    public void load() {
        App.i("load");
        if (!playbackOnLoads.isEmpty()) {
            final Playback plb = this;
            for (PlaybackOnLoad p : playbackOnLoads) {
                new Worker(() -> p.onLoad(plb)).start();
            }
        }
    }

    public void onPlay() {
        if (!playbackOnPlays.isEmpty()) {
            final Playback plb = this;
            for (PlaybackOnPlay p : playbackOnPlays) {
                new Worker(() -> p.onPlay(plb)).start();
            }
        }
    }


    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> playingBep;

    public void stop() {
        this.player.close();
        if (playingBep != null) {
            playingBep.cancel(true);
        }
        this.playing = false;
        load();
    }

    public void play(long position) {
        if (isPlaying()) {
            this.player.setBytesPosition(position);
        } else {
            this.position = position;
        }
    }

    public void play() {
        if (this.player != null) {
            this.player.close();
        }
        if (playingBep != null) {
            playingBep.cancel(false);
        }
        this.player = new Player(nowPlaying);
        this.player.play(position);
        this.playing = true;
        playingBep = scheduler.scheduleAtFixedRate(() -> {
            onPlay();
            if (player.getSecondsPosition() == player.getSecondsTotal()) {
                App.i("read repeat " + repeat.name());
                switch (repeat) {
                    case OFF:
                        if (songList.size() - 1 == nowPlayingIdx) {
                            stop();
                        } else {
                            next();
                        }
                        break;
                    case ALL:
                        next();
                    case ONE:
                        this.position = 0;
                        play();
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        load();
    }

    public void pause() {
        position = this.player.getBytesPosition();
        this.stop();
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public boolean isPlaying() {
        return playing;
    }


    public void setNowPlayingIdx(int ni) {
        Realm r = Realm.getInstance(App.realmConfig());
        r.executeTransaction(realm -> {
            General nowplayingSetting = realm.where(General.class).equalTo("key", "PLAYBACK_PLAY_INDEX").findFirst();
            nowplayingSetting.setValue(String.valueOf(ni));
        });
        r.close();
        this.nowPlaying = songList.get(ni);
        this.nowPlayingIdx = ni;
    }

    public void next() {
        App.i("next");
        int next = nowPlayingIdx + 1;
        try {
            songList.get(next);
        } catch (Exception e) {
            next = 0;
        }
        setNowPlayingIdx(next);
        this.position = 0;
        load();
        if (isPlaying()) {
            play();
        }
    }


    public void prev() {
        if (this.player != null && this.player.getSecondsPosition() > 3 && isPlaying()) {
            play(0);
        } else {
            if (nowPlayingIdx - 1 < 0) {
                setNowPlayingIdx(songList.size() - 1);
            } else {
                setNowPlayingIdx(nowPlayingIdx - 1);
            }
            this.position = 0;
            if (!isPlaying()) {
                load();
            } else {
                play();
            }
        }
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
        Realm r = Realm.getInstance(App.realmConfig());
        r.executeTransaction(realm -> {
            playlist = realm.where(Playlist.class).equalTo("name", "___queue").findFirst();
            if (this.shuffle) {
                Collections.shuffle(songList);
                playlist.setShuffleList(Utility.serializeDir(songList));
            } else {
                songList = Utility.deserializeDir(playlist.getOrderedList());
            }
            App.i("shuffle" + this.shuffle);
            nowPlayingIdx = songList.indexOf(nowPlaying);
            General nowplayingSetting = realm.where(General.class).equalTo("key", "PLAYBACK_SHUFFLE").findFirst();
            nowplayingSetting.setValue(String.valueOf(this.shuffle));
        });
        Toast.makeText(App.getInstance(), "Shuffle " + this.shuffle, Toast.LENGTH_SHORT).show();
        r.close();
        load();
    }

    public void shuffle() {
        if (this.shuffle) {
            setShuffle(false);
        } else {
            setShuffle(true);
        }
    }


    public Repeat getRepeat() {
        return repeat;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public enum Repeat {
        OFF,
        ONE,
        ALL
    }

    public void repeat() {
        switch (repeat) {
            case ONE:
                repeat = Repeat.OFF;
                break;
            case ALL:
                repeat = Repeat.ONE;
                break;
            case OFF:
                repeat = Repeat.ALL;
                break;
        }
        Realm r = Realm.getInstance(App.realmConfig());
        r.executeTransaction(realm -> {
            General nowplayingSetting = realm.where(General.class).equalTo("key", "PLAYBACK_REPEAT").findFirst();
            nowplayingSetting.setValue(repeat.name());
        });
        Toast.makeText(App.getInstance(), "Repeat " + repeat.name(), Toast.LENGTH_SHORT).show();
        r.close();
        load();
    }

    public String getNowPlaying() {
        return nowPlaying;
    }

}
