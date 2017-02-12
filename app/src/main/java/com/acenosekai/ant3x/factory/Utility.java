package com.acenosekai.ant3x.factory;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.acenosekai.ant3x.App;
import com.acenosekai.ant3x.model.Album;
import com.acenosekai.ant3x.model.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class Utility {

    public static final String APP_DIR = "com.acenosekai.ant3x";

    public static File mkAppDirIfNotExist(String path) {
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + Utility.APP_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File folder2 = new File(Environment.getExternalStorageDirectory() +
                File.separator + Utility.APP_DIR + File.separator + path);
        if (!folder2.exists()) {
            folder2.mkdirs();
        }

        return folder2;
    }

    public interface NotEmpty {
        void notEmpty(String s);
    }

    public interface Empty {
        void empty();
    }

    public static void emptyHandle(String s, NotEmpty notEmpty) {
        emptyHandle(s, notEmpty, () -> {
        });
    }

    public static void emptyHandle(String s, NotEmpty notEmpty, Empty empty) {
        if (StringUtils.isEmpty(s)) {
            empty.empty();
        } else {
            if (StringUtils.isEmpty(s.trim())) {
                empty.empty();
            } else {
                notEmpty.notEmpty(s.trim());
            }
        }
    }

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = App.getInstance().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = App.getInstance().getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String intToHHmm(int time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(time));
    }

    public static String serializeDir(List<String> songs) {
        return new Gson().toJson(songs);
    }

    public static List<String> buildLisDir(List<Song> songs) {
        return StreamSupport.stream(songs).map(Song::getDirectory).collect(Collectors.toList());
    }


    public static List<String> deserializeDir(String songSerialized) {
        return new Gson().fromJson(songSerialized, new TypeToken<List<String>>() {
        }.getType());
    }

    public interface OnLoadCover {
        void onLoadCover(boolean hasCover);
    }

    public static void loadCoverSmall(String dir, OnLoadCover o) {
        new Worker(() ->
                loadCoverAlbum(dir, hasCover -> {
                    if (hasCover) {
                        try {
                            Realm realm = Realm.getInstance(App.realmConfig());
                            Song song = realm.where(Song.class).equalTo("directory", dir).findFirst();
                            if (App.getInstance().getCoverSmallCache().contains(song.getAlbumKey())) {
                                o.onLoadCover(true);
                            } else {
                                int width = 100;
                                int height = 100;
                                Bitmap bmp = App.getInstance().getCoverCache().getBitmap(song.getAlbumKey()).getBitmap();
                                float f;
                                if (bmp.getWidth() > bmp.getHeight()) {
                                    f = (float) width / (float) bmp.getWidth();
                                } else {
                                    f = (float) height / (float) bmp.getHeight();
                                }

                                width = Math.round((float) bmp.getWidth() * f);
                                height = Math.round((float) bmp.getHeight() * f);
                                Bitmap res = Bitmap.createScaledBitmap(bmp, width, height, false);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                res.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                                byte[] bitmapData = bos.toByteArray();
                                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapData);
                                App.getInstance().getCoverSmallCache().put(song.getAlbumKey(), bs);
                                o.onLoadCover(true);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            o.onLoadCover(false);
                        }
                    } else {
                        o.onLoadCover(false);
                    }
                })

        ).start();

    }

//    public interface InstanceRealm {
//        void run(Realm r);
//    }
//
//    public static void saveRealm(InstanceRealm i) {
//        Realm r = Realm.getInstance(App.realmConfig());
//        i.run(r);
//        r.close();
//    }

    public static void loadCoverAlbum(String dir, OnLoadCover o) {

        new Worker(() -> {
            Realm realm = Realm.getInstance(App.realmConfig());

            realm.executeTransaction(r -> {
                Song song = realm.where(Song.class).equalTo("directory", dir).findFirst();
                String albumKey = song.getAlbumKey();
                Album a = r.where(Album.class).equalTo("albumKey", albumKey).findFirst();
                if (a == null) {
                    a = r.createObject(Album.class, albumKey);
                    MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                    metadataRetriever.setDataSource(song.getDirectory());
                    byte[] byteImage = metadataRetriever.getEmbeddedPicture();
                    if (byteImage != null) {
                        try {
                            ByteArrayInputStream bs = new ByteArrayInputStream(byteImage);
                            App.getInstance().getCoverCache().put(albumKey, bs);
                            bs.close();
                            App.i(song.getAlbum());
                            o.onLoadCover(true);
                            a.setHasCover(true);
                        } catch (IOException e) {
                            o.onLoadCover(false);
                            a.setHasCover(false);
                            e.printStackTrace();
                        }
                    } else {
                        o.onLoadCover(false);
                        a.setHasCover(false);
                    }
                    metadataRetriever.release();
                } else {
                    if (a.getHasCover()) {
                        o.onLoadCover(true);
                    } else {
                        o.onLoadCover(false);
                    }
                }


            });
            realm.close();
        }).start();


    }
}
