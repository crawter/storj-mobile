package mio.storj.mobile.domain;

import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.domain.buckets.IBucketRepository;
import mio.storj.mobile.domain.files.IFileRepository;
import mio.storj.mobile.domain.settings.ISettingsRepository;
import mio.storj.mobile.domain.syncqueue.ISyncQueueRepository;
import mio.storj.mobile.domain.uploading.IUploadingFilesRepository;

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
