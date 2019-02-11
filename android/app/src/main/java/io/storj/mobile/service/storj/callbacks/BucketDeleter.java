package io.storj.mobile.service.storj.callbacks;

import io.storj.libstorj.DeleteBucketCallback;
import io.storj.mobile.common.responses.Response;

public class BucketDeleter implements DeleteBucketCallback {
    private Response result;

    @Override
    public void onBucketDeleted(String bucketId) {
        result = new Response(true, null);
    }

    @Override
    public void onError(String bucketId, int code, String message) {
        result = new Response(false, null, code);
    }

    public Response getResult() {
        return result;
    }
}
