package com.acenosekai.ant3x.model;

import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Acenosekai on 1/8/2017.
 * Rock On
 */

public class Playlist extends RealmObject {
    @PrimaryKey
    private String name;
    private String orderedList;
    private String shuffleList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderedList() {
        return orderedList;
    }

    public void setOrderedList(String orderedList) {
        this.orderedList = orderedList;
    }

    public String getShuffleList() {
        return shuffleList;
    }

    public void setShuffleList(String shuffleList) {
        this.shuffleList = shuffleList;
    }
}


