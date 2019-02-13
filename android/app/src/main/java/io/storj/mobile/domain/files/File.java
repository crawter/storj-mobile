package io.storj.mobile.domain.files;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// File is a domain model that describes file entity
public class File {
    @Expose
    @SerializedName("bucketId")
    private String _bucketId;
    @Expose
    @SerializedName("created")
    private String _created;
    @Expose
    @SerializedName("id")
    private String _erasure;
    @Expose
    @SerializedName("hmac")
    private String _hmac;
    @Expose
    @SerializedName("fileId")
    private String _fileId;
    @Expose
    @SerializedName("index")
    private String _index;
    @Expose
    @SerializedName("mimeType")
    private String _mimeType;
    @Expose
    @SerializedName("name")
    private String _name;
    @Expose
    @SerializedName("fileUri")
    private String _fileUri;
    @Expose
    @SerializedName("thumbnail")
    private String _thumbnail;
    @Expose
    @SerializedName("downloadState")
    private int _downloadState;
    @Expose
    @SerializedName("fileHandle")
    private long _fileHandle;
    @Expose
    @SerializedName("size")
    private long _size;
    @Expose
    @SerializedName("isDecrypted")
    private boolean _isDecrypted;
    @Expose
    @SerializedName("isStarred")
    private boolean _isStarred;
    @Expose
    @SerializedName("isSynced")
    private boolean _isSynced;
    @Expose
    @SerializedName("isFileHandleSet")
    private boolean _isFileHandleSet;


    // File - is a constructor for file entity
    public File(String bucketId,
                String created,
                String erasure,
                String hmac,
                String fileId,
                String index,
                String mimeType,
                String name,
                String fileUri,
                String thumbnail,
                int downloadState,
                long fileHandle,
                long size,
                boolean isDecrypted,
                boolean isStarred,
                boolean isSynced) {
        _bucketId = bucketId;
        _created = created;
        _erasure = erasure;
        _hmac = hmac;
        _fileId = fileId;
        _index = index;
        _mimeType = mimeType;
        _name = name;
        _fileUri = fileUri;
        _thumbnail = thumbnail;
        _downloadState = downloadState;
        _fileHandle = fileHandle;
        _size = size;
        _isDecrypted = isDecrypted;
        _isStarred = isStarred;
        _isSynced = isSynced;
    }

    public String getBucketId () {
        return _bucketId;
    }
    public String getCreated () {
        return _created;
    }
    public String getErasure () {
        return _erasure;
    }
    public String getHmac () {
        return _hmac;
    }
    public String getFileId () {
        return _fileId;
    }
    public String getIndex () {
        return _index;
    }
    public String getMimeType () {
        return _mimeType;
    }
    public String getName () {
        return _name;
    }
    public String getFileUri () {
        return _fileUri;
    }
    public String getThumbnail () {
        return _thumbnail;
    }
    public int getDownloadState() {
        return _downloadState;
    }
    public long getFileHandle() {
        return _fileHandle;
    }
    public long getSize() {
        return _size;
    }
    public boolean isDecrypted() {
        return _isDecrypted;
    }
    public boolean isStarred() {
        return _isStarred;
    }
    public boolean isSynced() {
        return _isSynced;
    }
    public boolean isFileHandleSet() { return _isFileHandleSet; }

    public void setStarred(boolean isStarred) {
        _isStarred = isStarred;
    }
    public void setDownloadState(int downState) {
        _downloadState = downState;
    }
    public void setFileHandle(long fileHandle) {
        _fileHandle = fileHandle;
        _isFileHandleSet = true;
    }
    public void setUri(String localPath) {
        _fileUri = localPath;
    }
    public void setThumbnail(String thumb) { _thumbnail = thumb; }
}
