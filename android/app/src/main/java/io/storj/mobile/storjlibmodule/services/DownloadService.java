package io.storj.mobile.storjlibmodule.services;

import android.content.Intent;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

import io.storj.libstorj.DownloadFileCallback;
import io.storj.libstorj.Storj;
import io.storj.libstorj.android.StorjAndroid;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.dataprovider.Database;
import io.storj.mobile.domain.IDatabase;
import io.storj.mobile.domain.files.File;
import io.storj.mobile.dataprovider.files.FileContract;
import io.storj.mobile.storjlibmodule.enums.DownloadStateEnum;
import io.storj.mobile.storjlibmodule.rnmodules.BaseReactService;
import io.storj.mobile.storjlibmodule.utils.ProgressResolver;
import io.storj.mobile.storjlibmodule.utils.ThumbnailProcessor;
import io.storj.mobile.storjlibmodule.utils.UploadSyncObject;

public class DownloadService extends BaseReactService {

    public final static String SERVICE_NAME_SHORT = "DownloadService";
    public final static String SERVICE_NAME = "io.storj.mobile.storjlibmodule.services.DownloadService";

    public final static String ACTION_DOWNLOAD_FILE = "ACTION_DOWNLOAD_FILE";
    public final static String ACTION_COPY_FILE = "ACTION_COPY_FILE";
    public final static String ACTION_DOWNLOAD_FILE_CANCEL = "ACTION_DOWNLOAD_FILE_CANCEL";

    public final static String EVENT_FILE_DOWNLOAD_START = "EVENT_FILE_DOWNLOAD_START";
    public final static String EVENT_FILE_DOWNLOAD_PROGRESS = "EVENT_FILE_DOWNLOAD_PROGRESS";
    public final static String EVENT_FILE_DOWNLOAD_SUCCESS = "EVENT_FILE_DOWNLOAD_SUCCESS";
    public final static String EVENT_FILE_DOWNLOAD_ERROR = "EVENT_FILE_DOWNLOAD_ERROR";

    public final static String PARAMS_BUCKET_ID = "bucketId";
    public final static String PARAMS_TARGET_BUCKET_ID = "targetBucketId";
    public final static String PARAMS_FILE_ID = "fileId";
    public final static String PARAMS_LOCAL_PATH = "localPath";

    private final static String DEBUG_TAG = "DOWNLOAD SERVICE DEBUG";

    private IDatabase mStore;
    private Storj storj;

    public DownloadService() {
        super(SERVICE_NAME_SHORT);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            storj = StorjAndroid.getInstance(this, "https://api.v2.storj.io");
        } catch(Exception e) {
            this.stopSelf();
            return;
        }

        mStore = new Database(this, null);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(DEBUG_TAG, "onHandleIntent: START");
        if(intent == null) {
            return;
        }

        String action = intent.getAction();
        String bucketId = intent.getStringExtra(PARAMS_BUCKET_ID);
        String fileId = intent.getStringExtra(PARAMS_FILE_ID);
        String localPath = intent.getStringExtra(PARAMS_LOCAL_PATH);

        switch (action) {
            case ACTION_DOWNLOAD_FILE:
                downloadFile(bucketId, fileId, localPath);
                break;
            case ACTION_COPY_FILE:
                String targetBucketId = intent.getStringExtra(PARAMS_TARGET_BUCKET_ID);

                if(downloadFile(bucketId, fileId, localPath)) {
                    uploadFile(targetBucketId, localPath);
                }

                break;
        }
        Log.d(DEBUG_TAG, "onHandleIntent: END, " + intent.getStringExtra(PARAMS_FILE_ID));
    }

    private void uploadFile(String bucketId, String localPath) {
        Intent uploadIntent = new Intent(this, UploadService.class);
        uploadIntent.setAction(UploadService.ACTION_UPLOAD_FILE);
        uploadIntent.putExtra(UploadService.PARAM_BUCKET_ID, bucketId);
        uploadIntent.putExtra(UploadService.PARAM_LOCAL_PATH, localPath);

        this.startService(uploadIntent);
    }

    private boolean downloadFile(String bucketId, final String fileId, String localPath) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        java.io.File file = new java.io.File(localPath);

        if(bucketId == null || fileId == null || file.isDirectory()) {
            return false;
        }

        final ThumbnailProcessor tProc = new ThumbnailProcessor();

        final SingleResponse<File> getFileResponse = mStore.files().get(fileId);
        if (!getFileResponse.isSuccess()) {
            return false;
        }

        final UploadSyncObject uploadSyncObject = new UploadSyncObject();
        final ProgressResolver progressResolver = new ProgressResolver();

        final File fileDbo = getFileResponse.getResult();

        final long fileHandle = storj.downloadFile(bucketId, fileId, localPath, new DownloadFileCallback() {
            @Override
            public void onProgress(String fileId, double progress, long downloadedBytes, long totalBytes) {
                synchronized (fileDbo) {
                    try {
                        if(!fileDbo.isFileHandleSet())
                            fileDbo.wait();
                    } catch(Exception e) {

                    }
                }

                double _progress;

                synchronized (progressResolver) {
                    progressResolver.setMProgress(progress);
                    _progress = progressResolver.getMProgress();

                    if(_progress != progress) {
                        return;
                    }
                }

                WritableMap map = new WritableNativeMap();
                map.putString("fileId", fileId);
                map.putDouble(FileContract._FILE_HANDLE, fileDbo.getFileHandle());
                map.putDouble("progress", _progress);

                sendEvent(EVENT_FILE_DOWNLOAD_PROGRESS, map);
            }

            @Override
            public void onComplete(String fileId, String localPath) {
                fileDbo.setFileHandle(0);
                fileDbo.setDownloadState(DownloadStateEnum.DOWNLOADED.getValue());
                fileDbo.setUri(localPath);

                Response updateFileResponse = mStore.files().update(fileDbo);
                if(updateFileResponse.isSuccess()) {
                    WritableMap map = new WritableNativeMap();
                    map.putString("fileId", fileId);
                    map.putString("localPath", localPath);

                    if(fileDbo.getMimeType().contains("image/")) {
                        SingleResponse<String> resp = tProc.getThumbnail(localPath);
                        if(resp.isSuccess()) {
                            map.putString(FileContract._FILE_THUMBNAIL, resp.getResult());
                        }

                        fileDbo.setThumbnail(resp.getResult());
                        mStore.files().update(fileDbo);
                    }

                    sendEvent(EVENT_FILE_DOWNLOAD_SUCCESS, map);
                }

                synchronized(uploadSyncObject) {
                    uploadSyncObject.setJobFinishedSuccess();
                }
            }

            @Override
            public void onError(String fileId, int code, String message) {
                fileDbo.setFileHandle(0);
                fileDbo.setDownloadState(DownloadStateEnum.DEFAULT.getValue());
                fileDbo.setUri(null);

                Response updateFileResponse = mStore.files().update(fileDbo);
                if(updateFileResponse.isSuccess()) {
                    WritableMap map = new WritableNativeMap();
                    map.putString("fileId", fileId);
                    map.putString("message", message);
                    map.putInt("code", code);

                    sendEvent(EVENT_FILE_DOWNLOAD_ERROR, map);
                }

                synchronized(uploadSyncObject) {
                    uploadSyncObject.setJobFinished();
                }
            }
        });

        synchronized(fileDbo) {
            fileDbo.setFileHandle(fileHandle);
            fileDbo.setDownloadState(DownloadStateEnum.DOWNLOADING.getValue());
            fileDbo.setUri(null);

            Response updateFileResponse = mStore.files().update(fileDbo);

            if(updateFileResponse.isSuccess()) {
                WritableMap map = new WritableNativeMap();
                map.putString("fileId", fileId);
                map.putDouble(FileContract._FILE_HANDLE, fileHandle);

                sendEvent(EVENT_FILE_DOWNLOAD_START, map);
            }

            fileDbo.notifyAll();
        }

        synchronized(uploadSyncObject) {
            uploadSyncObject.isJobFinished();
            return uploadSyncObject.isSuccess();
        }
    }
}
