package io.storj.mobile.domain.files;

import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;

public interface IFileRepository {
    ListResponse<File> getAll();
    ListResponse<File> getAll(String orderByColumn, boolean isDesc);
    ListResponse<File> getAll(String bucketId);
    ListResponse<File> getAll(String bucketId, String orderByColumn, boolean isDesc);

    SingleResponse<File> get(String fileId);
    SingleResponse<File> get(String param, String selection, String bucketId);

    Response insert(File model);

    Response delete(String fileId);
    Response deleteAll(String bucketId);

    Response update(File model);

    Response createTable();
}
