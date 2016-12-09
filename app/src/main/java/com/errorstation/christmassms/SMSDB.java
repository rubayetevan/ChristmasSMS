package com.errorstation.christmassms;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Rubayet on 09-Dec-16.
 */

public class SMSDB extends RealmObject {

    String id;
    String details,title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
