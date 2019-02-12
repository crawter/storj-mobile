package io.storj.mobile.service.storj.callbacks;

import java.util.ArrayList;
import java.util.List;

import io.storj.libstorj.GetBucketsCallback;
import io.storj.mobile.common.Converters;
import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.domain.buckets.Bucket;

public class BucketsReceiver implements GetBucketsCallback {
    private ListResponse<Bucket> mResult;

    @Override
    public void onBucketsReceived(io.storj.libstorj.Bucket[] buckets) {
        int length = buckets.length;
        List<Bucket> bList = new ArrayList<Bucket>();

        for (int i = 0; i < length; i++) {
            bList.add(Converters.toDomain(buckets[i]));
        }

        mResult = new ListResponse<>(bList, true, null);
    }

    @Override
    public void onError(int code, String message) {
        mResult = new ListResponse<Bucket>(null, false, message, code);
    }

    public ListResponse<Bucket> getResult() {

        return mResult;
    }
}