package com.acenosekai.ant3x;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by Acenosekai on 1/7/2017.
 * Rock On
 */

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            oldVersion++;
        }
        if (oldVersion == 1) {
            if (schema.get("Album") == null) {
                schema.create("Album")
                        .addField("albumKey", String.class, FieldAttribute.PRIMARY_KEY)
                        .addField("hasCover", Boolean.class);
            }

            oldVersion++;
        }

        if (oldVersion == 2) {
            if (schema.get("Directory") == null) {
                schema.create("Directory")
                        .addField("directory", String.class, FieldAttribute.PRIMARY_KEY)
                        .addField("initialized", Boolean.class)
                        .addField("lastWatch", Date.class);
            }

            oldVersion++;
        }
    }
}
