package mio.storj.mobile.domain.syncqueue;

import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;

public interface ISyncQueueRepository {
    Response insert(SyncQueueEntry model);
    Response update(SyncQueueEntry model);
    ListResponse<SyncQueueEntry> getAll();
    SingleResponse<SyncQueueEntry> get(int id);
    SyncQueueEntry get(String localPath, String bucketId);
    int getActiveCount();

    Response createTable();
}
