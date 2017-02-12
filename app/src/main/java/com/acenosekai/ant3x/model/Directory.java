package com.acenosekai.ant3x.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Acenosekai on 2/11/2017.
 * Rock On
 */

public class Directory extends RealmObject {
    @PrimaryKey
    private String directory;
    private Boolean initialized = false;
    private Date lastWatch = new Date();

    public Date getLastWatch() {
        return lastWatch;
    }

    public void setLastWatch(Date lastWatch) {
        this.lastWatch = lastWatch;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Boolean getInitialized() {
        return initialized;
    }

    public void setInitialized(Boolean initialized) {
        this.initialized = initialized;
    }
}
