package mio.storj.mobile.domain.files;

import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;

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
