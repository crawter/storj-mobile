package io.storj.mobile.domain.uploading;

import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;

public interface IUploadingFilesRepository {
    ListResponse<UploadingFile> getAll();

    SingleResponse<UploadingFile> get(String id);

    Response insert(UploadingFile model);

    Response update(UploadingFile model);

    Response delete(long fileHandle);

    Response deleteAll();

    Response createTable();
}
