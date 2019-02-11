package io.storj.mobile.service.storj.callbacks;

import io.storj.libstorj.DeleteFileCallback;
import io.storj.mobile.common.responses.Response;

public class FileDeleter implements DeleteFileCallback {
    private Response result;

    @Override
    public void onFileDeleted(String fileId) {
        result = new Response(true, null);
    }

    @Override
    public void onError(String fileId, int code, String message) {
        result = new Response(false, null, code);
    }

    public Response getResult() {
        return result;
    }
}
