package mio.storj.mobile.service.download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import mio.storj.mobile.common.Convertible;
import mio.storj.mobile.common.Convertible;

public class LoadFileModel extends Convertible {
    @Expose
    @SerializedName("fileId")
    private String mFileId;
    @Expose
    @SerializedName("fileHandle")
    private long mFileHandle;
    @Expose
    @SerializedName("progress")
    private double mProgress;
    @Expose
    @SerializedName("localPath")
    private String mLocalPath;
    @Expose
    @SerializedName("thumbnail")
    private String mThumbnail;

    public LoadFileModel(String fileId, long fHandle, double progress) {
        mFileId = fileId;
        mFileHandle = fHandle;
        mProgress = progress;
    }

    public LoadFileModel(String fileId, String path, String thumbnail) {
        mFileId = fileId;
        mLocalPath = path;
        mThumbnail = thumbnail;
    }

    public LoadFileModel(String fileId, String path) {
        mFileId = fileId;
        mLocalPath = path;
    }
}
