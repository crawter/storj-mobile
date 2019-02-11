package io.storj.mobile.domain.buckets;

import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;

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
