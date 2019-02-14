package io.storj.mobile.storjlibmodule.services.callbacks;

import io.storj.libstorj.File;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.domain.IDatabase;
import io.storj.mobile.service.download.DownloadStateEnum;
import io.storj.mobile.storjlibmodule.services.eventemitters.UploadEventEmitter;
import io.storj.mobile.service.utils.ThumbnailProcessor;
import io.storj.mobile.storjlibmodule.utils.Uploader;

public class WorkerUploaderCallback extends BaseUploaderCallback {
    protected Uploader.Callback mEventEmitter;
    protected boolean mIsSync;

    public WorkerUploaderCallback(IDatabase db, Uploader.Callback eventEmitter, boolean isSync) {
        super(db);
        mEventEmitter = eventEmitter;
        mIsSync = isSync;

        if(eventEmitter == null) {
            mEventEmitter = new UploadEventEmitter();
        }
    }

    @Override
    public void onStart(long fileHandle, String bucketId, String fileName, String localPath) {
        super.onStart(fileHandle, bucketId, fileName, localPath);
        mEventEmitter.onStart(fileHandle, bucketId, fileName, localPath);
    }

    @Override
    public boolean onProgress(String localPath, double progress, long uploadedBytes, long totalBytes) {
        boolean result = super.onProgress(localPath, progress, uploadedBytes, totalBytes);

        if(result) {
            mEventEmitter.onProgress(localPath, progress, uploadedBytes, totalBytes);
        }

        return result;
    }

    @Override
    public void onComplete(String localPath, File file) {
        super.onComplete(localPath, file);

        ThumbnailProcessor tProc = new ThumbnailProcessor();
        String thumbnail = null;

        if(file.getMimeType().contains("image/")) {
            thumbnail = tProc.getThumbnail(localPath);
        }

        io.storj.mobile.domain.files.File fileModel = new io.storj.mobile.domain.files.File(
                file.getBucketId(),
                file.getCreated(),
                file.getErasure(),
                file.getHMAC(),
                file.getId(),
                file.getIndex(),
                file.getMimeType(),
                file.getName(),
                localPath,
                thumbnail,
                DownloadStateEnum.DOWNLOADED.getValue(),
                0,
                file.getSize(),
                file.isDecrypted(),
                false,
                mIsSync);

        Response response = mStore.files().insert(fileModel);
        mEventEmitter.onComplete(localPath, file);
    }

    @Override
    public void onError(String localPath, int code, String message) {
        super.onError(localPath, code, message);
        mEventEmitter.onError(localPath, code, message);
    }
}