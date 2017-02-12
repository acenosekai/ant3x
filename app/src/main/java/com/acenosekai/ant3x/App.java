package com.acenosekai.ant3x;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.acenosekai.ant3x.factory.SimpleDiskCache;
import com.acenosekai.ant3x.factory.Utility;
import com.acenosekai.ant3x.factory.sing.BassInit;
import com.acenosekai.ant3x.factory.sing.Playback;
import com.acenosekai.ant3x.model.Album;
import com.acenosekai.ant3x.model.General;
import com.acenosekai.ant3x.model.Song;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class App extends Application {

    private static App app;

    public static App getInstance() {
        return app;
    }

    public static void desTroyInstance(){app=null;}

    public static void i(String message) {
        Log.i("ANT3X", message);
    }

    private static RealmConfiguration realmConfiguration;


    public static synchronized RealmConfiguration realmConfig() {
        if (realmConfiguration == null) {
            realmConfiguration = new RealmConfiguration.Builder()
                    .name("com.acenosekai.ant3x.realm")
                    .migration(new Migration())
                    .schemaVersion(3)
                    .build();
        }
        return realmConfiguration;
    }

    private Realm r;

    public Realm getR() {
        return r;
    }

    private SimpleDiskCache coverCache;

    public SimpleDiskCache getCoverCache() {
        return coverCache;
    }

    private SimpleDiskCache coverSmallCache;

    public SimpleDiskCache getCoverSmallCache() {
        return coverSmallCache;
    }

    @Override
    public void onCreate() {
        app = this;
        try {
            this.coverCache = SimpleDiskCache.open(Utility.mkAppDirIfNotExist("cover"), 1, 1024 * 1024 * 20);
        } catch (IOException e) {
            i("cant initialize cover cache");
            e.printStackTrace();
        }

        try {
            this.coverSmallCache = SimpleDiskCache.open(Utility.mkAppDirIfNotExist("cover_small"), 1, 1024 * 1024 * 20);
        } catch (IOException e) {
            i("cant initialize cover small cache");
            e.printStackTrace();
        }

        Realm.init(this);
        r = Realm.getInstance(realmConfig());
        App.i("ver name -> " + BuildConfig.VERSION_NAME);
        General versionName = r.where(General.class).equalTo("key", "VERSION_NAME").findFirst();
        if (versionName == null || !versionName.getValue().equals(BuildConfig.VERSION_NAME)) {
            r.executeTransaction(realm -> {
                // Delete all matches
                App.i("dif version");
                RealmResults<Song> songs = realm.where(Song.class).findAll();
                songs.deleteAllFromRealm();

                RealmResults<Album> albums = realm.where(Album.class).findAll();
                albums.deleteAllFromRealm();
                if (null == versionName) {
                    General general = realm.createObject(General.class, "VERSION_NAME");
                    general.setValue(BuildConfig.VERSION_NAME);
                } else {
                    versionName.setValue(BuildConfig.VERSION_NAME);
                    realm.copyToRealmOrUpdate(versionName);
                }

                General shuffleSetting = realm.where(General.class).equalTo("key", "PLAYBACK_SHUFFLE").findFirst();
                if (shuffleSetting == null) {
                    shuffleSetting = realm.createObject(General.class, "PLAYBACK_SHUFFLE");
                    shuffleSetting.setValue("false");
                }

                General repeatSetting = realm.where(General.class).equalTo("key", "PLAYBACK_REPEAT").findFirst();
                if (repeatSetting == null) {
                    repeatSetting = realm.createObject(General.class, "PLAYBACK_REPEAT");
                    repeatSetting.setValue("OFF");
                }

                General nowplayingSetting = realm.where(General.class).equalTo("key", "PLAYBACK_PLAY_INDEX").findFirst();
                if (nowplayingSetting == null) {
                    nowplayingSetting = realm.createObject(General.class, "PLAYBACK_PLAY_INDEX");
                    nowplayingSetting.setValue("0");
                }

            });
        }

        BassInit.getInstance();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        r.close();
    }

}
