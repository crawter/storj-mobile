package io.storj.mobile.service.storj.callbacks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.storj.libstorj.GetBucketsCallback;
import io.storj.mobile.service.storj.Converters;
import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.domain.buckets.Bucket;

public class BucketsReceiver implements GetBucketsCallback {
    private ListResponse<Bucket> mResult;
    private CountDownLatch mCounter;

    public BucketsReceiver(CountDownLatch counter) {
        mCounter = counter;
    }

    @Override
    public void onBucketsReceived(io.storj.libstorj.Bucket[] buckets) {
        int length = buckets.length;
        List<Bucket> bList = new ArrayList<Bucket>();

        for (int i = 0; i < length; i++) {
            bList.add(Converters.toDomain(buckets[i]));
        }

        mResult = new ListResponse<>(bList, true, null);
        mCounter.countDown();
    }

    @Override
    public void onError(int code, String message) {
        mResult = new ListResponse<Bucket>(null, false, message, code);
        mCounter.countDown();
    }

    public ListResponse<Bucket> getResult() {
        return mResult;
    }
}