package com.acenosekai.ant3x.factory;

import android.media.MediaMetadataRetriever;
import android.os.Handler;

import com.acenosekai.ant3x.App;
import com.acenosekai.ant3x.model.Song;

import java.io.ByteArrayInputStream;
import java.io.File;

import io.realm.Realm;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class MediaReader {
    private File file;

    public MediaReader(File file) {
        this.file = file;
    }

    public void initialize(){
        new Worker(() -> {
            Realm realm = Realm.getInstance(App.realmConfig());
            realm.executeTransaction(r -> {
                Song song = r.where(Song.class).equalTo("directory", file.getPath()).findFirst();
                if(!song.getInitialized()){
                    try {
                        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
                        metadataRetriever.setDataSource(file.getPath());
                        try {
                            Utility.emptyHandle(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                                    , song::setTitle
                                    , () -> song.setTitle(file.getName()));

                        } catch (Exception e) {
                            song.setTitle(file.getName());
                            App.i(e.getMessage());
                        }

                        try {
                            Utility.emptyHandle(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST), song::setArtist);
                        } catch (Exception e) {
                            App.i(e.getMessage());
                        }

                        try {
                            Utility.emptyHandle(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM), song::setAlbum);
                        } catch (Exception e) {
                            App.i(e.getMessage());
                        }

                        try {
                            Utility.emptyHandle(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST), song::setAlbumArtist);
                        } catch (Exception e) {
                            App.i(e.getMessage());
                        }

                        try {
                            Utility.emptyHandle(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE), song::setGenre);
                        } catch (Exception e) {
                            App.i(e.getMessage());
                        }

                        try {
                            Utility.emptyHandle(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION), s1 -> song.setDuration(Integer.parseInt(s1)));
                        } catch (Exception e) {
                            App.i(e.getMessage());
                        }

                        String albumKey = song.getAlbum() + "-" + song.getAlbumArtist();
                        song.setAlbumKey(albumKey);
                        metadataRetriever.release();
                    } catch (Exception e) {
                        song.setTitle(file.getName());
                        App.i(e.getMessage());
                    }

                }

                if (song.getDuration() <= 6000) {
                    song.deleteFromRealm();
                }

            });

            realm.close();

        }).start();
    }
}
