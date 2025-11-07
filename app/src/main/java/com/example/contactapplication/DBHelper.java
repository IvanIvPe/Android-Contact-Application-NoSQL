package com.example.contactapplication;

import android.content.Context;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import java.util.List;

public class DBHelper {

    private final Realm realm;

    public DBHelper(Context context) {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("contacts.realm")
                .schemaVersion(1)
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        this.realm = Realm.getDefaultInstance();
    }

    public Realm getRealm() {
        return realm;
    }

    public void addContact(Contact contact) {
        realm.executeTransaction(realm -> {
            if (realm.where(Contact.class).equalTo("id", contact.getId()).findFirst() == null) {
                realm.insert(contact);
            }
        });
    }

    public void updateContact(Contact contact) {
        realm.executeTransaction(realm -> {
            Contact existingContact = realm.where(Contact.class).equalTo("id", contact.getId()).findFirst();
            if (existingContact != null) {
                existingContact.setName(contact.getName());
                existingContact.setPhone(contact.getPhone());
                existingContact.setProfilePictureUri(contact.getProfilePictureUri());
            }
        });
    }

    public void updateContactPicture(int contactId, String pictureUri) {
        realm.executeTransaction(realm -> {
            Contact contact = realm.where(Contact.class).equalTo("id", contactId).findFirst();
            if (contact != null) {
                contact.setProfilePictureUri(pictureUri);
            }
        });
    }

    public void deleteContact(int id) {
        realm.executeTransaction(realm -> {
            Contact contact = realm.where(Contact.class).equalTo("id", id).findFirst();
            if (contact != null) {
                contact.deleteFromRealm();
            }
        });
    }

    public List<Contact> getAllContacts() {
        RealmResults<Contact> results = realm.where(Contact.class).findAll();
        return realm.copyFromRealm(results);
    }

    public Contact getContactById(int id) {
        return realm.where(Contact.class).equalTo("id", id).findFirst();
    }

    public void close() {
        realm.close();
    }
}
