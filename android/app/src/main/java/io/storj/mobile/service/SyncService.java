package io.storj.mobile.service;

import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.domain.IDatabase;
import io.storj.mobile.domain.buckets.Bucket;
import io.storj.mobile.domain.files.File;
import io.storj.mobile.domain.settings.Settings;
import io.storj.mobile.domain.syncqueue.SyncQueueEntry;
import io.storj.mobile.domain.syncqueue.SyncStateEnum;
import io.storj.mobile.domain.uploading.UploadingFile;
import io.storj.mobile.storjlibmodule.enums.DownloadStateEnum;

public class SyncService {
    private final IDatabase mStore;

    public SyncService(IDatabase db) {
        mStore = db;
    }

    public ListResponse<Bucket> listBuckets(final String sortingMode) {
        return sortingMode.equalsIgnoreCase("name")
                ? mStore.buckets().getAll(sortingMode, true)
                : mStore.buckets().getAll();
    }

    public ListResponse<File> listFiles(final String bucketId, final String sortingMode) {
        return sortingMode.equalsIgnoreCase("name")
                ? mStore.files().getAll(bucketId, sortingMode, true)
                : mStore.files().getAll(bucketId);
    }

    public ListResponse<File> listAllFiles(final String sortingMode) {
        return sortingMode.equalsIgnoreCase("name")
                ? mStore.files().getAll(sortingMode, true)
                : mStore.files().getAll();
    }

    public ListResponse<UploadingFile> listUploadingFiles() {
        return mStore.uploadingFiles().getAll();
    }

    public SingleResponse<UploadingFile> getUploadingFile(final String fileHandle) {
        return mStore.uploadingFiles().get(fileHandle);
    }

    public SingleResponse<File> getFile(final String fileId) {
        return mStore.files().get(fileId);
    }

    public Response updateBucketStarred(final String bucketId, final boolean isStarred) {
        SingleResponse<Bucket> getBucketResponse = mStore.buckets().get(bucketId);
        if (!getBucketResponse.isSuccess()) {
            return getBucketResponse;
        }

        Bucket bucketToUpdate = getBucketResponse.getResult();
        bucketToUpdate.setStarred(isStarred);

        return mStore.buckets().update(bucketToUpdate);
    }

    public Response updateFileStarred(final String fileId, final boolean isStarred) {
        SingleResponse<File> getFileResponse = mStore.files().get(fileId);
        if (!getFileResponse.isSuccess()) {
            return getFileResponse;
        }

        File fileToUpdate = getFileResponse.getResult();
        fileToUpdate.setStarred(isStarred);

        return mStore.files().update(fileToUpdate);
    }

    public Response updateFileState(final String fileId, final String localPath, int downState, long fileHandle) {
        SingleResponse<File> fileResponse = mStore.files().get(fileId);
        if (fileResponse.isSuccess()) {
            return fileResponse;
        }

        File file = fileResponse.getResult();
        file.setDownloadState(downState);
        file.setFileHandle(fileHandle);
        file.setUri(localPath);

        return mStore.files().update(file);
    }

    public SingleResponse<Settings> listSettings(final String id) {
        return mStore.settings().get(id);
    }

    // TODO: change settings creation
    public Response insertSyncSetting(final String id) {
        Settings settings = new Settings(id);
        return mStore.settings().insert(settings);
    }

    public SingleResponse<Settings> getSetings(final String id) {
        return mStore.settings().get(id);
    }

    public Response updateSettings(final Settings setting) {
        return mStore.settings().update(setting);
    }

    public SingleResponse<Settings> changeSyncStatus(final String id, final boolean syncStatus) {
        SingleResponse<Settings> settingsResponse = mStore.settings().get(id);
        if (!settingsResponse.isSuccess()) {
            return settingsResponse;
        }

        Settings settings = settingsResponse.getResult();
        settings.setSyncStatus(syncStatus);
        Response updateResponse = mStore.settings().update(settings);
        return new SingleResponse<>(settings, updateResponse.isSuccess(), null);
    }

    public SingleResponse<SyncQueueEntry> updateSyncEntryName(final int id, final String newFileName) {
        SingleResponse<SyncQueueEntry> entryResponse = mStore.syncQueueEntries().get(id);
        if (!entryResponse.isSuccess()) {
            return entryResponse;
        }

        SyncQueueEntry entry = entryResponse.getResult();
        entry.setName(newFileName);
        entry.setStatus(SyncStateEnum.IDLE.getValue());

        Response updateResponse = mStore.syncQueueEntries().update(entry);
        return new SingleResponse<>(entry, updateResponse.isSuccess(), null);
    }

    public SingleResponse<SyncQueueEntry> updateSyncEntryStatus(final int id, final int newStatus) {
        SingleResponse<SyncQueueEntry> entryResponse = mStore.syncQueueEntries().get(id);
        if (!entryResponse.isSuccess()) {
            return entryResponse;
        }

        SyncQueueEntry entry = entryResponse.getResult();
        entry.setStatus(newStatus);

        Response updateResponse = mStore.syncQueueEntries().update(entry);
        return new SingleResponse<>(entry, updateResponse.isSuccess(), null);
    }

    public ListResponse<SyncQueueEntry> getSyncQueue() {
        return mStore.syncQueueEntries().getAll();
    }

    public SingleResponse<SyncQueueEntry> getSyncQueueEntry(final int id) {
        return mStore.syncQueueEntries().get(id);
    }
}
