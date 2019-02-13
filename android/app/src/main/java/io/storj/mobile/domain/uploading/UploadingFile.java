package io.storj.mobile.domain.uploading;

public class UploadingFile {
    private long _fileHandle;
    private double _progress;
    private long _size;
    private long _uploaded;

    private String _name;
    private String _uri;
    private String _bucketId;

    private boolean isIdSet;

    public UploadingFile(long fileHandle, double progress, long size, long uploaded, String name, String uri, String bucketId) {
        _fileHandle = fileHandle;
        _progress = progress;
        _size = size;
        _uploaded = uploaded;
        _name = name;
        _uri = uri;
        _bucketId = bucketId;
    }

    public long getFileHandle() {
        return _fileHandle;
    }
    public double getProgress() {
        return _progress;
    }
    public long getSize() {
        return _size;
    }
    public long getUploaded() {
        return _uploaded;
    }
    public String getName() {
        return _name;
    }
    public String getUri() {
        return _uri;
    }
    public String getBucketId() {
        return _bucketId;
    }
    public boolean isIdSet() {
        return isIdSet;
    }

    public void setProgress(double progress) {
        _progress = progress;
    }
    public void setUploaded(long uploadedBytes) {
        _uploaded = uploadedBytes;
    }
    public void setSize(long totalBytes) {
        _size = totalBytes;
    }
}
