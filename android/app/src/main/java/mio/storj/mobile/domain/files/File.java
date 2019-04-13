package mio.storj.mobile.domain.files;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// File is a domain model that describes file entity
public class File {
    @Expose
    @SerializedName("bucketId")
    public String bucketId;
    @Expose
    @SerializedName("created")
    public String created;
    @Expose
    @SerializedName("id")
    public String erasure;
    @Expose
    @SerializedName("hmac")
    public String hmac;
    @Expose
    @SerializedName("fileId")
    public String fileId;
    @Expose
    @SerializedName("index")
    public String index;
    @Expose
    @SerializedName("mimeType")
    public String mimeType;
    @Expose
    @SerializedName("name")
    public String name;
    @Expose
    @SerializedName("fileUri")
    public String fileUri;
    @Expose
    @SerializedName("thumbnail")
    public String thumbnail;
    @Expose
    @SerializedName("downloadState")
    public int downloadState;
    @Expose
    @SerializedName("fileHandle")
    public long fileHandle;
    @Expose
    @SerializedName("size")
    public long size;
    @Expose
    @SerializedName("isDecrypted")
    public boolean isDecrypted;
    @Expose
    @SerializedName("isStarred")
    public boolean isStarred;
    @Expose
    @SerializedName("isSynced")
    public boolean isSynced;
    @Expose
    @SerializedName("isFileHandleSet")
    public boolean isFileHandleSet;


    public void setFileHandle(long newFileHandle) {
        fileHandle = newFileHandle;
        isFileHandleSet = true;
    }
}
