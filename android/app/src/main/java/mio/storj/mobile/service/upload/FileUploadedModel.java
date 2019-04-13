package mio.storj.mobile.service.upload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import mio.storj.mobile.common.Convertible;
import mio.storj.mobile.common.Convertible;

public class FileUploadedModel extends Convertible {
    @Expose
    @SerializedName("fileId")
    private String mFileId;
    @Expose
    @SerializedName("fileHandle")
    private long mFileHandle;

    public FileUploadedModel(String fileId, long fileHandle) {
        mFileId = fileId;
        mFileHandle = fileHandle;
    }
}
