package com.acenosekai.ant3x.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class General extends RealmObject {
    @PrimaryKey
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

