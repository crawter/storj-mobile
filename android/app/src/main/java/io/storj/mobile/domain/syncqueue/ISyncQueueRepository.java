package io.storj.mobile.domain.syncqueue;

import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.common.responses.Response;

public interface ISyncQueueRepository {
    Response insert(SyncQueueEntry model);
    Response update(SyncQueueEntry model);
    ListResponse<SyncQueueEntry> getAll();
    SyncQueueEntry get(int id);
    SyncQueueEntry get(String localPath, String bucketId);
    int getActiveCount();

    Response createTable();
}
