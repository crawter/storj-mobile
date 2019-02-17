package io.storj.mobile.storjlibmodule.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import java.util.List;

import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.dataprovider.Database;
import io.storj.mobile.domain.IDatabase;
import io.storj.mobile.domain.syncqueue.SyncQueueEntry;
import io.storj.mobile.storjlibmodule.enums.SyncStateEnum;
import io.storj.mobile.storjlibmodule.services.eventemitters.BaseEventEmitter;
import io.storj.mobile.storjlibmodule.services.eventemitters.SynchronizationEventEmitter;

public class SyncQueueService extends IntentService {
    public final static String SERVICE_NAME = "SyncQueueService";

    public final static String ACTION_SYNC = "ACTION_SYNC";
    public final static String ACTION_REMOVE_FROM_QUEUE = "ACTION_REMOVE_FROM_QUEUE";
    public final static String ACTION_SYNC_CANCEL = "ACTION_SYNC_CANCEL";

    public final static String EVENT_SYNC_ENTRY_UPDATED = "EVENT_SYNC_ENTRY_UPDATED";
    public final static String EVENT_SYNC_STARTED = "EVENT_SYNC_STARTED"; //TODO: Rename to sync list updated

    private SynchronizationEventEmitter mEventEmitter;
    private final IDatabase mStore = Database.getInstance();

    public SyncQueueService() {
        super(SERVICE_NAME);
        mEventEmitter = new SynchronizationEventEmitter(new BaseEventEmitter(this));
    }

    public SyncQueueService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }

        switch (intent.getAction()) {
            case ACTION_SYNC:
                sync(intent);
                break;
            case ACTION_SYNC_CANCEL:
                cancelSync(intent);
                break;
            case ACTION_REMOVE_FROM_QUEUE:
                removeFileFromQueue(intent);
                break;
        }
    }

    //DELETE ALL UPLOADING FILES
    //SET PROCESSING AND QUEUE ENTRIES TO IDLE STATE
    public static void clean(Context context) {
        IDatabase db = Database.getInstance();
        Response response = db.uploadingFiles().deleteAll();

        ListResponse<SyncQueueEntry> syncEntriesResponse = db.syncQueueEntries().getAll();
        if (!syncEntriesResponse.isSuccess()) {
            return;
        }

        List<SyncQueueEntry> syncEntries = syncEntriesResponse.getResult();

        for (SyncQueueEntry syncEntry : syncEntries) {

            boolean isProcessing = syncEntry.getStatus() == SyncStateEnum.PROCESSING.getValue();
            boolean isQued = syncEntry.getStatus() == SyncStateEnum.QUEUED.getValue();

            if (isProcessing || isQued) {
                syncEntry.setStatus(SyncStateEnum.IDLE.getValue());
                Response updateResponse = db.syncQueueEntries().update(syncEntry);
            }
        }
    }

    private void sync(Intent intent) {
        ListResponse<SyncQueueEntry> syncEntriesResponse = mStore.syncQueueEntries().getAll();
        if (!syncEntriesResponse.isSuccess()) {
            return;
        }

        List<SyncQueueEntry> syncEntries = syncEntriesResponse.getResult();

        for (SyncQueueEntry syncEntry : syncEntries) {
            if (syncEntry.getStatus() == SyncStateEnum.IDLE.getValue()) {
                syncEntry.setStatus(SyncStateEnum.QUEUED.getValue());

                Response response = mStore.syncQueueEntries().update(syncEntry);
                if (response.isSuccess()) {
                    syncFile(syncEntry.getFileName(), syncEntry.getLocalPath(), syncEntry.getBucketId(), syncEntry.getId());
                }
            }
        }

        mEventEmitter.SyncStarted();
    }

    private void cancelSync(Intent intent) {
        Intent cancelSyncIntent = new Intent(this, UploadService.class);
        cancelSyncIntent.setAction(UploadService.ACTION_CANCEL_SYNC);

        this.startService(cancelSyncIntent);


        ListResponse<SyncQueueEntry> syncEntriesResponse = mStore.syncQueueEntries().getAll();
        if (!syncEntriesResponse.isSuccess()) {
            return;
        }

        List<SyncQueueEntry> syncEntries = syncEntriesResponse.getResult();

        for (SyncQueueEntry syncEntry : syncEntries) {
            if (syncEntry.getStatus() == SyncStateEnum.PROCESSING.getValue()) {
                cancelFileSync(syncEntry.getFileHandle());
            }

            if (syncEntry.getStatus() == SyncStateEnum.QUEUED.getValue()) {
                syncEntry.setStatus(SyncStateEnum.IDLE.getValue());

                Response response = mStore.syncQueueEntries().update(syncEntry);
                if (response.isSuccess()) {
                    mEventEmitter.SyncEntryUpdated(syncEntry.getId());
                }
            }
        }

        NotificationService.Clean(this);
    }

    private void syncFile(String fileName, String localPath, String bucketId, int syncEntryId) {
        Intent syncFileIntent = new Intent(this, UploadService.class);
        syncFileIntent.setAction(UploadService.ACTION_SYNC_FILE);
        syncFileIntent.putExtra(UploadService.PARAM_FILE_NAME, fileName);
        syncFileIntent.putExtra(UploadService.PARAM_LOCAL_PATH, localPath);
        syncFileIntent.putExtra(UploadService.PARAM_BUCKET_ID, bucketId);
        syncFileIntent.putExtra(UploadService.PARAM_SYNC_ENTRY_ID, syncEntryId);

        this.startService(syncFileIntent);
    }

    private void cancelFileSync(long fileHandle) {
        if (fileHandle == 0) {
            return;
        }

        Intent syncFileIntent = new Intent(this, UploadService.class);
        syncFileIntent.setAction(UploadService.ACTION_CANCEL_UPLOAD);
        syncFileIntent.putExtra(UploadService.PARAM_FILE_HANDLE, fileHandle);

        this.startService(syncFileIntent);
    }

    private void removeFileFromQueue(Intent intent) {
        int syncEntryId = intent.getIntExtra(UploadService.PARAM_SYNC_ENTRY_ID, -1);

        if (syncEntryId == -1) {
            return;
        }

        SingleResponse<SyncQueueEntry> getSyncEntryResponse = mStore.syncQueueEntries().get(syncEntryId);
        if (!getSyncEntryResponse.isSuccess()) {
            return;
        }

        SyncQueueEntry sqe = getSyncEntryResponse.getResult();
        if(sqe.getStatus() != SyncStateEnum.QUEUED.getValue()) {
            return;
        }

        sqe.setStatus(SyncStateEnum.CANCELLED.getValue());
        Response response = mStore.syncQueueEntries().update(sqe);
        if (response.isSuccess()) {
            Intent removeFromSyncQueueIntent = new Intent(this, UploadService.class);
            removeFromSyncQueueIntent.setAction(UploadService.ACTION_REMOVE_FROM_SYNC_QUEUE);
            removeFromSyncQueueIntent.putExtra(UploadService.PARAM_SYNC_ENTRY_ID, syncEntryId);

            this.startService(removeFromSyncQueueIntent);
            mEventEmitter.SyncEntryUpdated(sqe.getId());
        }

    }
}
