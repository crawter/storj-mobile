package io.storj.mobile.storjlibmodule.rnmodules.rnparallel;

import android.os.Process;

import io.storj.mobile.storjlibmodule.responses.Response;

public class RNParallel {
    public static void invokeParallel(final PromiseParams param, final IPromiseCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(Process.getThreadPriority(0) != Process.THREAD_PRIORITY_BACKGROUND) {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                }

                invoke(param, callback);
            }
        }).start();
    }

    private static void invoke(final PromiseParams param, final IPromiseCallback callback) {
        try {
            if(callback == null) return;

            callback.callback(param);
        } catch(Exception error) {
            param.getPromise().resolve(new Response(false, error.getMessage()).toWritableMap());
        }
    }
}
