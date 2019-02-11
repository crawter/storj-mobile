package io.storj.mobile.domain;

import io.storj.mobile.common.responses.Response;
import io.storj.mobile.domain.buckets.IBucketRepository;
import io.storj.mobile.domain.files.IFileRepository;
import io.storj.mobile.domain.settings.ISettingsRepository;
import io.storj.mobile.domain.syncqueue.ISyncQueueRepository;
import io.storj.mobile.domain.uploading.IUploadingFilesRepository;

// IDatabase provides access to all database tables
public interface IDatabase {
    IBucketRepository buckets();
    IFileRepository files();
    IUploadingFilesRepository uploadingFiles();
    ISyncQueueRepository syncQueueEntries();
    ISettingsRepository settings();

    void beginTransaction();
    void commitTransaction();
    void rollbackTransaction();

    Response createTables();
    Response dropTables();
}
