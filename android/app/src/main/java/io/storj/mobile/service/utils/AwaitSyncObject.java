package io.storj.mobile.service.utils;

public class AwaitSyncObject {
    private boolean mIsFinished;
    private boolean mIsSuccess;

    public void finish(boolean isSuccess) {
        mIsFinished = true;
        mIsSuccess = isSuccess;
        notify();
    }

    public synchronized boolean await() {
        try {
            while(!mIsFinished) {
                wait();
            }
        } catch (Exception ignored) {}

        return mIsSuccess;
    }
}
