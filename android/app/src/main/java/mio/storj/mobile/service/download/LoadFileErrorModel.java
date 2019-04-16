package mio.storj.mobile.service.download;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import mio.storj.mobile.common.Convertible;

public class LoadFileErrorModel extends Convertible {
    @Expose
    @SerializedName("fileId")
    private String mFileId;

    @Expose
    @SerializedName("localPath")
    private String mLocalPath;

    @Expose
    @SerializedName("message")
    private String mMessage;

    public LoadFileErrorModel(String fileId, String path, String message) {
        mFileId = fileId;
        mLocalPath = path;
        mMessage = message;
    }
}
