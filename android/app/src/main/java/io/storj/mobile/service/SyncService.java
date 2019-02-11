package io.storj.mobile.service;

import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.domain.IDatabase;
import io.storj.mobile.domain.buckets.Bucket;
import io.storj.mobile.domain.files.File;
import io.storj.mobile.domain.settings.Settings;
import io.storj.mobile.domain.uploading.UploadingFile;

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


}
