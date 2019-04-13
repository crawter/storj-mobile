package mio.storj.mobile.domain.uploading;

import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;

public interface IUploadingFilesRepository {
    ListResponse<UploadingFile> getAll();

    SingleResponse<UploadingFile> get(String id);

    Response insert(UploadingFile model);

    Response update(UploadingFile model);

    Response delete(long fileHandle);

    Response deleteAll();

    Response createTable();
}
