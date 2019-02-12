package io.storj.mobile.service.storj.callbacks;

import java.util.concurrent.CountDownLatch;

import io.storj.libstorj.DeleteFileCallback;
import io.storj.mobile.common.responses.Response;

public class FileDeleter implements DeleteFileCallback {
    private Response mResult;
    private CountDownLatch mCounter;

    public FileDeleter(CountDownLatch counter) {
        mCounter = counter;
    }

    @Override
    public void onFileDeleted(String fileId) {
        mResult = new Response(true, null);
        mCounter.countDown();
    }

    @Override
    public void onError(String fileId, int code, String message) {
        mResult = new Response(false, null, code);
        mCounter.countDown();
    }

    public Response getResult() {
        return mResult;
    }
}
