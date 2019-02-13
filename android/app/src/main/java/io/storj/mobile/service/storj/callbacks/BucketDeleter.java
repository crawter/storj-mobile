package io.storj.mobile.service.storj.callbacks;

import java.util.concurrent.CountDownLatch;

import io.storj.libstorj.DeleteBucketCallback;
import io.storj.mobile.common.responses.Response;

public class BucketDeleter implements DeleteBucketCallback {
    private Response mResult;
    private CountDownLatch mCounter;

    public BucketDeleter(CountDownLatch counter) {
        mCounter = counter;
    }

    @Override
    public void onBucketDeleted(String bucketId) {
        mResult = new Response(true, null);
        mCounter.countDown();
    }

    @Override
    public void onError(String bucketId, int code, String message) {
        mResult = new Response(false, null, code);
        mCounter.countDown();
    }

    public Response getResult() {
        return mResult;
    }
}
