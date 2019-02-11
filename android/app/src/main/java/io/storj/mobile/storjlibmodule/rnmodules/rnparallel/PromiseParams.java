package io.storj.mobile.storjlibmodule.rnmodules.rnparallel;

import com.facebook.react.bridge.Promise;

public class PromiseParams {
    private Promise _promise;

    public PromiseParams(Promise promise) {
        _promise = promise;
    }

    public Promise getPromise() {
        return _promise;
    }
}
