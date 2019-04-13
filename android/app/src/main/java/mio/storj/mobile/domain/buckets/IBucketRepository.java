package mio.storj.mobile.domain.buckets;

import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;

public interface IBucketRepository {
    ListResponse<Bucket> getAll();
    ListResponse<Bucket> getAll(String orderByColumn, boolean isDesc);

    SingleResponse<Bucket> get(String bucketId);
    SingleResponse<Bucket> get(String columnName, String columnValue);

    Response insert(Bucket model);

    Response delete(String bucketId);
    Response deleteAll();

    Response update(Bucket model);

    Response createTable();
}
