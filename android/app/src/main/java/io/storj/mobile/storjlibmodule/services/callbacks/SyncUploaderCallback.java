package io.storj.mobile.storjlibmodule.services.callbacks;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import io.storj.libstorj.File;
import io.storj.mobile.R;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.domain.IDatabase;
import io.storj.mobile.domain.syncqueue.SyncQueueEntry;
import io.storj.mobile.storjlibmodule.enums.SyncStateEnum;
import io.storj.mobile.storjlibmodule.services.NotificationService;
import io.storj.mobile.storjlibmodule.services.SyncQueueService;
import io.storj.mobile.storjlibmodule.services.UploadService;
import io.storj.mobile.storjlibmodule.services.eventemitters.BaseEventEmitter;
import io.storj.mobile.storjlibmodule.services.eventemitters.SynchronizationEventEmitter;
import io.storj.mobile.storjlibmodule.utils.Uploader;

public class SyncUploaderCallback extends WorkerUploaderCallback {
    private int mSyncEntryId;
    private SyncQueueEntry mSyncEntryModel;
    private NotificationService mNotificationService;
    private SynchronizationEventEmitter mSyncEventEmitter;
    private Context mContext;

    public SyncUploaderCallback(Context context, IDatabase db, Uploader.Callback eventEmitter, NotificationService notificationService, int syncEntryId) {
        super(db, eventEmitter, true);
        mContext = context;
        mSyncEntryId = syncEntryId;
        mNotificationService = notificationService;
        mSyncEventEmitter = new SynchronizationEventEmitter((BaseEventEmitter)eventEmitter);
    }

    @Override
    public void onStart(long fileHandle, String bucketId, String fileName, String localPath) {
        super.onStart(fileHandle, bucketId, fileName, localPath);
        SingleResponse<SyncQueueEntry> getEntryResponse = mStore.syncQueueEntries().get(mSyncEntryId);
        if (!getEntryResponse.isSuccess()) {
            return;
        }

        mSyncEntryModel = getEntryResponse.getResult();
        mSyncEntryModel.setStatus(SyncStateEnum.PROCESSING.getValue());
        mSyncEntryModel.setFileHandle(fileHandle);

        Response response = mStore.syncQueueEntries().update(mSyncEntryModel);

        if(response.isSuccess()) {
            mSyncEventEmitter.SyncEntryUpdated(mSyncEntryId);
        }

        mNotificationService.addAction(getNotificationAction("Skip", getCancelUploadIntent()));
        mNotificationService.addAction(getNotificationAction("Cancel", getCancelSyncIntent()));
    }

    @Override
    public boolean onProgress(String localPath, double progress, long uploadedBytes, long totalBytes) {
        if(!super.onProgress(localPath, progress, uploadedBytes, totalBytes)) {
            return false;
        }

        int filesLeftToProcess = mStore.syncQueueEntries().getActiveCount();
        int totalMb = (int) (totalBytes/1024);
        int uploadedMb = (int) (uploadedBytes/1024);
        String state = "Uploading";

        String message = String.format("%s " + mSyncEntryModel.getFileName() + " %s left", state, filesLeftToProcess);
        String title = "Synchronization";

        mNotificationService.notify(1, message, title, uploadedMb, totalMb);
        return true;
    }

    @Override
    public void onComplete(String localPath, File file) {
        super.onComplete(localPath, file);

        mSyncEntryModel.setStatus(SyncStateEnum.PROCESSED.getValue());

        Response response = mStore.syncQueueEntries().update(mSyncEntryModel);
        if(response.isSuccess()) {
            mSyncEventEmitter.SyncEntryUpdated(mSyncEntryId);
        }
    }

    @Override
    public void onError(String localPath, int code, String message) {
        super.onError(localPath, code, message);

        mSyncEntryModel.setStatus(SyncStateEnum.ERROR.getValue());
        mSyncEntryModel.setErrorCode(code);

        Response response = mStore.syncQueueEntries().update(mSyncEntryModel);

        if(response.isSuccess()) {
            mSyncEventEmitter.SyncEntryUpdated(mSyncEntryId);
        }
    }

    private  NotificationCompat.Action getNotificationAction(String title, Intent serviceIntent) {
        return new NotificationCompat.Action(R.mipmap.logo_white, title, getServicePendingIntent(serviceIntent));
    }

    private PendingIntent getServicePendingIntent(Intent serviceIntent) {
        return PendingIntent.getService(mContext, 0, serviceIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private Intent getCancelUploadIntent() {
        Intent cancelUploadIntent = new Intent(mContext, UploadService.class);
        cancelUploadIntent.setAction(UploadService.ACTION_CANCEL_UPLOAD);
        cancelUploadIntent.putExtra(UploadService.PARAM_FILE_HANDLE, mFileHandle);

        return cancelUploadIntent;
    }

    private Intent getCancelSyncIntent() {
        return new Intent(mContext, SyncQueueService.class).setAction(SyncQueueService.ACTION_SYNC_CANCEL);
    }
}