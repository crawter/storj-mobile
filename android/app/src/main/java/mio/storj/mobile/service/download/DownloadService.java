package mio.storj.mobile.service.download;

import java.util.concurrent.CountDownLatch;

import io.storj.libstorj.DownloadFileCallback;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.domain.IDatabase;
import mio.storj.mobile.domain.files.File;
import mio.storj.mobile.service.IEventEmitter;
import mio.storj.mobile.service.storj.StorjService;
import mio.storj.mobile.service.utils.ThumbnailProcessor;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.domain.IDatabase;
import mio.storj.mobile.domain.files.File;
import mio.storj.mobile.service.IEventEmitter;

public class DownloadService {
    private final IDatabase mStore;
    private final StorjService mStorj;
    private final IEventEmitter mEventEmitter;

    private final static String EVENT_FILE_DOWNLOAD_START = "EVENT_FILE_DOWNLOAD_START";
    private final static String EVENT_FILE_DOWNLOAD_PROGRESS = "EVENT_FILE_DOWNLOAD_PROGRESS";
    private final static String EVENT_FILE_DOWNLOAD_SUCCESS = "EVENT_FILE_DOWNLOAD_SUCCESS";
    private final static String EVENT_FILE_DOWNLOAD_ERROR = "EVENT_FILE_DOWNLOAD_ERROR";

    public DownloadService(IDatabase db, StorjService storj, IEventEmitter eventEmitter) {
        mStore = db;
        mStorj = storj;
        mEventEmitter = eventEmitter;
    }

    public boolean download(String bucketId, final String fileId, final String localPath)
            throws InterruptedException, Exception {

        java.io.File file = new java.io.File(localPath);

        if(bucketId == null || fileId == null || file.isDirectory()) {
            return false;
        }

        final CountDownLatch downloadLatch = new CountDownLatch(1);

        // TODO: get as external interface dependency in constructor?
        final ThumbnailProcessor tProc = new ThumbnailProcessor();

        final SingleResponse<File> getFileResponse = mStore.files().get(fileId);
        if (!getFileResponse.isSuccess()) {
            return false;
        }

        //final ProgressResolver progressResolver = new ProgressResolver();

        final File fileDbo = getFileResponse.getResult();

        final long fileHandle = mStorj.downloadFile(bucketId, fileId, localPath, new DownloadFileCallback() {
            @Override
            public void onProgress(String fileId, double progress, long downloadedBytes, long totalBytes) {
                synchronized (fileDbo) {
                    if(!fileDbo.isFileHandleSet()) {
                        try {
                            fileDbo.wait();
                        } catch(Exception ignored) {}
                    }
                }

                mEventEmitter.sendEvent(EVENT_FILE_DOWNLOAD_PROGRESS,
                        new LoadFileModel(fileId, fileDbo.getFileHandle(), progress).toJson());
            }

            @Override
            public void onComplete(String fileId, String localPath) {
                fileDbo.setFileHandle(0);
                fileDbo.setDownloadState(DownloadStateEnum.DOWNLOADED.getValue());
                fileDbo.setUri(localPath);

                Response updateFileResponse = mStore.files().update(fileDbo);
                if(updateFileResponse.isSuccess()) {

                    if(fileDbo.getMimeType().contains("image/")) {
                        fileDbo.setThumbnail(tProc.getThumbnail(localPath));
                        mStore.files().update(fileDbo);
                    }

                    mEventEmitter.sendEvent(EVENT_FILE_DOWNLOAD_SUCCESS,
                            new LoadFileModel(fileId, localPath, fileDbo.getThumbnail()).toJson());
                }

                downloadLatch.countDown();
            }

            @Override
            public void onError(String fileId, int code, String message) {
                fileDbo.setFileHandle(0);
                fileDbo.setDownloadState(DownloadStateEnum.DEFAULT.getValue());
                fileDbo.setUri(null);

                Response updateFileResponse = mStore.files().update(fileDbo);
                if(updateFileResponse.isSuccess()) {
                    mEventEmitter.sendEvent(EVENT_FILE_DOWNLOAD_ERROR, new LoadFileModel(fileId, localPath).toJson());
                }

                downloadLatch.countDown();
            }
        });

        fileDbo.setFileHandle(fileHandle);
        fileDbo.setDownloadState(DownloadStateEnum.DOWNLOADING.getValue());
        fileDbo.setUri(null);

        Response updateFileResponse = mStore.files().update(fileDbo);

        if(updateFileResponse.isSuccess()) {
            mEventEmitter.sendEvent(EVENT_FILE_DOWNLOAD_START,
                    new LoadFileModel(fileId, fileHandle, 0).toJson());
        }

        downloadLatch.await();

        return fileDbo.getFileHandle() != 0;
    }
}
