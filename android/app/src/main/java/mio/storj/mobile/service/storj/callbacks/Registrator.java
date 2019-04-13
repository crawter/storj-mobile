package mio.storj.mobile.service.storj.callbacks;

import io.storj.libstorj.RegisterCallback;

// TODO: user response here
public class Registrator implements RegisterCallback {
    private boolean mIsSuccess;

    @Override
    public void onConfirmationPending(String email) {
        mIsSuccess = true;
    }

    @Override
    public void onError(int code, String message) {
        mIsSuccess = false;
    }

    public boolean getResult() {
        return mIsSuccess;
    }
}
