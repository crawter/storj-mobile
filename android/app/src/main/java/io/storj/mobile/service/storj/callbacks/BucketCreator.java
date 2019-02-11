package io.storj.mobile.service.storj.callbacks;

import io.storj.libstorj.CreateBucketCallback;
import io.storj.mobile.common.Converters;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.domain.buckets.Bucket;

public class BucketCreator implements CreateBucketCallback {
    private SingleResponse<Bucket> mresult;

    @Override
    public void onBucketCreated(io.storj.libstorj.Bucket bucket) {
        mresult = new SingleResponse<>(Converters.toDomain(bucket), true, null);
    }

    @Override
    public void onError(String bucketName, int code, String message) {
        mresult = new SingleResponse<>(null, false, message, code);
    }

    public SingleResponse<Bucket> getResult() {
        return mresult;
    }
}
