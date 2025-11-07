package com.example.contactapplication;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ContactApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // inicijalizovanje Realm-a
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("contacts.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
