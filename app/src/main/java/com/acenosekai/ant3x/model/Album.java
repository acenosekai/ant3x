package com.acenosekai.ant3x.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Acenosekai on 1/28/2017.
 * Rock On
 */

public class Album  extends RealmObject {
    @PrimaryKey
    private String albumKey;
    private Boolean hasCover;

    public String getAlbumKey() {
        return albumKey;
    }

    public void setAlbumKey(String albumKey) {
        this.albumKey = albumKey;
    }

    public Boolean getHasCover() {
        return hasCover;
    }

    public void setHasCover(Boolean hasCover) {
        this.hasCover = hasCover;
    }
}
