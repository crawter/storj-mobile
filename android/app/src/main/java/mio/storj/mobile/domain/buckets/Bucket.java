package mio.storj.mobile.domain.buckets;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Bucket is a domain model that describes bucket entity
public class Bucket {
    @Expose
    @SerializedName("id")
    public String id;
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("created")
    public String created;
    @Expose
    @SerializedName("hash")
    public long hash;
    @Expose
    @SerializedName("isDecrypted")
    public boolean isDecrypted;
    @Expose
    @SerializedName("isStarred")
    public boolean isStarred;
}
