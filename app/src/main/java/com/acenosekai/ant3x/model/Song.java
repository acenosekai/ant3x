package com.acenosekai.ant3x.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */
public class Song extends RealmObject {
    @PrimaryKey
    private String directory;
    private String title = "(Unknown)";
    private String artist = "(Unknown)";
    private String albumArtist = "(Unknown)";
    private Integer duration = 0;
    private String genre = "(Unknown)";
    private String album = "(Unknown)";
    private Date lastWatch = new Date();
    private String albumKey = "(Unknown)";
    private Boolean initialized = false;

    public Boolean getInitialized() {
        return initialized;
    }

    public void setInitialized(Boolean initialized) {
        this.initialized = initialized;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Date getLastWatch() {
        return lastWatch;
    }

    public void setLastWatch(Date lastWatch) {
        this.lastWatch = lastWatch;
    }

    public String getAlbumKey() {
        return albumKey;
    }

    public void setAlbumKey(String albumKey) {
        this.albumKey = albumKey;
    }
}