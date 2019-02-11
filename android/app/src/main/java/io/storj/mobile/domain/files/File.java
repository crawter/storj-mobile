package io.storj.mobile.domain.files;

// File is a domain model that describes file entity
public class File {
    private String _bucketId;
    private String _created;
    private String _erasure;
    private String _hmac;
    private String _fileId;
    private String _index;
    private String _mimeType;
    private String _name;
    private String _fileUri;
    private String _thumbnail;
    private int _downloadState;
    private long _fileHandle;
    private long _size;
    private boolean _isDecrypted;
    private boolean _isStarred;
    private boolean _isSynced;
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

    public void setStarred(boolean isStarred) {
        _isStarred = isStarred;
    }
}
