package mio.storj.mobile.service.upload;

import java.util.concurrent.CountDownLatch;

import io.storj.libstorj.File;
import io.storj.libstorj.UploadFileCallback;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.domain.IDatabase;
import mio.storj.mobile.domain.uploading.UploadingFile;
import mio.storj.mobile.service.IEventEmitter;
import mio.storj.mobile.service.download.DownloadStateEnum;
import mio.storj.mobile.service.storj.StorjService;
import mio.storj.mobile.service.utils.ThumbnailProcessor;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.domain.IDatabase;
import mio.storj.mobile.domain.uploading.UploadingFile;
import mio.storj.mobile.service.IEventEmitter;
import mio.storj.mobile.service.download.DownloadStateEnum;
import mio.storj.mobile.service.storj.StorjService;
import mio.storj.mobile.service.utils.ThumbnailProcessor;

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

                mio.storj.mobile.domain.files.File fileModel = new mio.storj.mobile.domain.files.File();
                fileModel.bucketId = file.getBucketId();
                fileModel.created = file.getCreated();
                fileModel.erasure = file.getErasure();
                fileModel.hmac = file.getHMAC();
                fileModel.fileId = file.getId();
                fileModel.index = file.getIndex();
                fileModel.mimeType = file.getMimeType();
                fileModel.name = file.getName();
                fileModel.fileUri = localPath;
                fileModel.thumbnail = thumbnail;
                fileModel.downloadState = DownloadStateEnum.DOWNLOADED.getValue();
                fileModel.fileHandle = 0;
                fileModel.size = file.getSize();
                fileModel.isDecrypted = file.isDecrypted();
                fileModel.isStarred = false;
                fileModel.isSynced = false;

                Response response = mStore.files().insert(fileModel);
                if (!response.isSuccess()) {
                    // TODO: notify somehow
                }

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
