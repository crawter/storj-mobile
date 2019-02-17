package io.storj.mobile.service.storj.callbacks;

import java.util.concurrent.CountDownLatch;

import io.storj.libstorj.CreateBucketCallback;
import io.storj.mobile.service.storj.Converters;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.domain.buckets.Bucket;

public class BucketCreator implements CreateBucketCallback {
    private SingleResponse<Bucket> mResult;
    private CountDownLatch mCounter;

    public BucketCreator(CountDownLatch counter) {
        mCounter = counter;
    }

    @Override
    public void onBucketCreated(io.storj.libstorj.Bucket bucket) {
        mResult = new SingleResponse<>(Converters.toDomain(bucket), true, null);
        mCounter.countDown();
    }

    @Override
    public void onError(String bucketName, int code, String message) {
        mResult = new SingleResponse<>(null, false, message, code);
        mCounter.countDown();
    }

    public SingleResponse<Bucket> getResult() {
        return mResult;
    }
}
