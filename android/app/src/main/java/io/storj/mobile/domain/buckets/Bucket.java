package io.storj.mobile.domain.buckets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Bucket is a domain model that describes bucket entity
public class Bucket {
    @Expose
    @SerializedName("id")
    private String _id;
    @Expose
    @SerializedName("name")
    private String _name;
    @Expose
    @SerializedName("created")
    private String _created;
    @Expose
    @SerializedName("hash")
    private long _hash;
    @Expose
    @SerializedName("isDecrypted")
    private boolean _isDecrypted;
    @Expose
    @SerializedName("isStarred")
    private boolean _isStarred;

    // Bucket - is a constructor for bucket entity
    public Bucket() {}

    // Bucket - is a constructor for bucket entity
    public Bucket(String id, String name, String created, long hash, boolean isDecrypted, boolean isStarred) {
        _id = id;
        _name = name;
        _created = created;
        _hash = hash;
        _isDecrypted = isDecrypted;
        _isStarred = isStarred;
    }

    public String getId() {
        return _id;
    }
    public String getName() {
        return _name;
    }
    public String getCreated() {
        return _created;
    }
    public long getHashcode() {
        return _hash;
    }
    public boolean isDecrypted() {
        return _isDecrypted;
    }
    public boolean isStarred() {
        return _isStarred;
    }

    public void setStarred(boolean isStarred) {
        _isStarred = isStarred;
    }
}
