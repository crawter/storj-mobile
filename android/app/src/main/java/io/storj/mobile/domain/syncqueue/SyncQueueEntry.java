package io.storj.mobile.domain.syncqueue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SyncQueueEntry {
    @Expose
    @SerializedName("id")
    private int _id;
    @Expose
    @SerializedName("fileName")
    private String _fileName;
    @Expose
    @SerializedName("localPath")
    private String _localPath;
    @Expose
    @SerializedName("status")
    private int _status;
    @Expose
    @SerializedName("errorCode")
    private int _errorCode;
    @Expose
    @SerializedName("size")
    private long _size;
    @Expose
    @SerializedName("count")
    private int _count;
    @Expose
    @SerializedName("creationDate")
    private String _creationDate;
    @Expose
    @SerializedName("bucketId")
    private String _bucketId;
    @Expose
    @SerializedName("fileHandle")
    private long _fileHandle;

    public SyncQueueEntry(int id,
                          String fileName,
                          String localPath,
                          int status,
                          int errorCode,
                          long size,
                          int count,
                          String creationDate,
                          String bucketId,
                          long fileHandle) {
        _id = id;
        _fileName = fileName;
        _localPath = localPath;
        _status = status;
        _errorCode = errorCode;
        _size = size;
        _count = count;
        _creationDate = creationDate;
        _bucketId = bucketId;
        _fileHandle = fileHandle;
    }

    public int getId() {
        return _id;
    }
    public String getFileName() {
        return _fileName;
    }
    public String getLocalPath() {
        return _localPath;
    }
    public int getStatus() {
        return _status;
    }
    public int getErrorCode() {
        return _errorCode;
    }
    public long getSize() {
        return _size;
    }
    public int getCount() { return _count; }
    public String getCreationDate() {
        return _creationDate;
    }
    public String getBucketId() {
        return _bucketId;
    }
    public long getFileHandle() {
        return _fileHandle;
    }

    public void setName(String name) {
        _fileName = name;
    }
    public void setStatus(int status) {
        _status = status;
    }
}
