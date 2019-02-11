package io.storj.mobile.domain.syncqueue;

public class SyncQueueEntry {
    private int _id;
    private String _fileName;
    private String _localPath;
    private int _status;
    private int _errorCode;
    private long _size;
    private int _count;
    private String _creationDate;
    private String _bucketId;
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

    public int getCount() {
        return _count;
    }

    public String getCreationDate() {
        return _creationDate;
    }

    public String getBucketId() {
        return _bucketId;
    }

    public long getFileHandle() {
        return _fileHandle;
    }
}
