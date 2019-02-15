package io.storj.mobile.service.upload;

import java.util.concurrent.CountDownLatch;

import io.storj.libstorj.File;
import io.storj.libstorj.UploadFileCallback;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.domain.IDatabase;
import io.storj.mobile.domain.uploading.UploadingFile;
import io.storj.mobile.service.IEventEmitter;
import io.storj.mobile.service.download.DownloadStateEnum;
import io.storj.mobile.service.storj.StorjService;
import io.storj.mobile.service.utils.ThumbnailProcessor;

public class UploadingService {
    private final IDatabase mStore;
    private final StorjService mStorj;
    private final IEventEmitter mEventEmitter;

    public final static String EVENT_FILE_UPLOAD_START = "EVENT_FILE_UPLOAD_START";
    public final static String EVENT_FILE_UPLOADED_PROGRESS = "EVENT_FILE_UPLOADED_PROGRESS";
    public final static String EVENT_FILE_UPLOADED_SUCCESSFULLY = "EVENT_FILE_UPLOADED_SUCCESSFULLY";
    public final static String EVENT_FILE_UPLOAD_ERROR = "EVENT_FILE_UPLOAD_ERROR";

    public UploadingService(IDatabase db, StorjService storj, IEventEmitter eventEmitter) {
        mStore = db;
        mStorj = storj;
        mEventEmitter = eventEmitter;
    }

    public void upload(final String bucketId, final String fileName, final String localPath) throws InterruptedException, Exception {

        final CountDownLatch uploadLatch = new CountDownLatch(1);

        final UploadingFile mUploadingFile = new UploadingFile(0, 0, 0, 0, fileName, localPath, bucketId);

        final long fileHandle = mStorj.uploadFile(bucketId, fileName, localPath, new UploadFileCallback() {
            @Override
            public void onProgress(String filePath, double progress, long uploadedBytes, long totalBytes) {
                synchronized (mUploadingFile) {
                    if(!mUploadingFile.isIdSet()) {
                        return;
                    }
                }

                // progress resolver

                mUploadingFile.setProgress(progress);
                mUploadingFile.setUploaded(uploadedBytes);

                Response updateFileResponse = mStore.uploadingFiles().update(mUploadingFile);
                if (updateFileResponse.isSuccess()) {
                    mEventEmitter.sendEvent(EVENT_FILE_UPLOADED_PROGRESS, mUploadingFile.toJson());
                }
            }

            @Override
            public void onComplete(String filePath, File file) {
                mStore.uploadingFiles().delete(mUploadingFile.getFileHandle());

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
                        false);

                Response response = mStore.files().insert(fileModel);

                mEventEmitter.sendEvent(EVENT_FILE_UPLOADED_SUCCESSFULLY,
                        new FileUploadedModel(file.getId(), mUploadingFile.getFileHandle()).toJson());
            }

            @Override
            public void onError(String filePath, int code, String message) {
                mStore.uploadingFiles().delete(mUploadingFile.getFileHandle());
                mEventEmitter.sendEvent(
                        EVENT_FILE_UPLOAD_ERROR,
                        new SingleResponse<>(
                            new FileUploadedModel("", mUploadingFile.getFileHandle()),
                        false,
                        message,
                        code).toJson()
                );
            }
        });

        mUploadingFile.setFileHandle(fileHandle);
        mStore.uploadingFiles().insert(mUploadingFile);
        mEventEmitter.sendEvent(EVENT_FILE_UPLOAD_START, mUploadingFile.toJson());

        uploadLatch.await();
    }
}
