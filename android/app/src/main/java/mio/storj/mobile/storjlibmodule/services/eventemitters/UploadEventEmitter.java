package mio.storj.mobile.storjlibmodule.services.eventemitters;

import android.content.ContentValues;
import android.content.Context;

import io.storj.libstorj.File;
import mio.storj.mobile.dataprovider.uploading.UploadingFileContract;
import mio.storj.mobile.storjlibmodule.utils.Uploader;
import mio.storj.mobile.storjlibmodule.utils.Uploader;

public class UploadEventEmitter extends BaseEventEmitter implements Uploader.Callback {
    protected long mFileHandle;

    public final static String EVENT_FILE_UPLOAD_START = "EVENT_FILE_UPLOAD_START";
    public final static String EVENT_FILE_UPLOADED_PROGRESS = "EVENT_FILE_UPLOADED_PROGRESS";
    public final static String EVENT_FILE_UPLOADED_SUCCESSFULLY = "EVENT_FILE_UPLOADED_SUCCESSFULLY";
    public final static String EVENT_FILE_UPLOAD_ERROR = "EVENT_FILE_UPLOAD_ERROR";

    public final static String ERROR_MESSAGE = "message";
    public final static String ERROR_CODE = "code";

    public UploadEventEmitter() {
        super(null);
    }

    public UploadEventEmitter(Context context) {
        super(context);
    }

    @Override
    public void onStart(long fileHandle, String bucketId, String fileName, String localPath) {
        ContentValues map = new ContentValues();
        map.put("fileHandle", fileHandle);

        mFileHandle = fileHandle;

        Emit(EVENT_FILE_UPLOAD_START, map);
    }

    @Override
    public boolean onProgress(String localPath, double progress, long uploadedBytes, long totalBytes) {
        ContentValues map = new ContentValues();
        map.put("fileHandle", mFileHandle);
        map.put(UploadingFileContract._PROGRESS, progress);
        map.put(UploadingFileContract._UPLOADED, uploadedBytes);

        Emit(EVENT_FILE_UPLOADED_PROGRESS, map);
        return true;
    }

    @Override
    public void onComplete(String localPath, File file) {
        ContentValues map = new ContentValues();
        map.put("fileHandle", mFileHandle);
        map.put("fileId", file.getId());

        Emit(EVENT_FILE_UPLOADED_SUCCESSFULLY, map);
    }

    @Override
    public void onError(String localPath, int code, String message) {
        ContentValues map = new ContentValues();

        map.put(ERROR_MESSAGE, message);
        map.put(ERROR_CODE, code);
        map.put("fileHandle", mFileHandle);

        Emit(EVENT_FILE_UPLOAD_ERROR, map);
    }
}