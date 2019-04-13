package mio.storj.mobile.domain.uploading;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import mio.storj.mobile.common.Convertible;
import mio.storj.mobile.common.Convertible;

public class UploadingFile extends Convertible {
    @Expose
    @SerializedName("fileHandle")
    private long _fileHandle;
    @Expose
    @SerializedName("progress")
    private double _progress;
    @Expose
    @SerializedName("size")
    private long _size;
    @Expose
    @SerializedName("uploaded")
    private long _uploaded;

    @Expose
    @SerializedName("name")
    private String _name;
    @Expose
    @SerializedName("uri")
    private String _uri;
    @Expose
    @SerializedName("bucketId")
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

        if (fileHandle != 0) {
            isIdSet = true;
        }
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
    public void setFileHandle(long handle) {
        _fileHandle = handle;
        isIdSet = true;
    }
}
