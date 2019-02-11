package io.storj.mobile.storjlibmodule.rnmodules;

import android.os.Environment;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import io.storj.mobile.service.storj.StorjService;
import io.storj.mobile.storjlibmodule.GsonSingle;
import io.storj.mobile.storjlibmodule.responses.SingleResponse;
import io.storj.mobile.storjlibmodule.rnmodules.rnparallel.IPromiseCallback;
import io.storj.mobile.storjlibmodule.rnmodules.rnparallel.PromiseParams;
import io.storj.mobile.storjlibmodule.rnmodules.rnparallel.RNParallel;

import java.io.File;

public class StorjModule extends ReactContextBaseJavaModule {
    private static final String MODULE_NAME = "StorjLibAndroid";

    private final StorjService mService;

    public StorjModule(ReactApplicationContext reactContext, StorjService storj) {
        super(reactContext);
        mService = storj;
    }

    //--- AUTH ---//

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    private String toJson(Object convertible) {
        return GsonSingle.getInstanse().toJson(convertible);
    }

    @ReactMethod
    public void generateMnemonic(Promise promise) {
        RNParallel.invokeParallel(new PromiseParams(promise), new IPromiseCallback() {
            @Override
            public void callback(PromiseParams param) {
                param.getPromise().resolve(mService.generateMnemonic());
            }
        });
    }

    @ReactMethod
    public void checkMnemonic(final String mnemonic, Promise promise) {
        RNParallel.invokeParallel(new PromiseParams(promise), new IPromiseCallback() {
            @Override
            public void callback(PromiseParams param) {
                param.getPromise().resolve(mService.checkMnemonic(mnemonic));
            }
        });
    }

    @ReactMethod
    public void verifyKeys(final String email, final String password, final Promise promise) {
        RNParallel.invokeParallel(new PromiseParams(promise), new IPromiseCallback() {
            @Override
            public void callback(PromiseParams param) throws Exception {
                param.getPromise().resolve(mService.verifyKeys(email, password));
            }
        });
    }

    @ReactMethod
    public void keysExists(Promise promise) {
        RNParallel.invokeParallel(new PromiseParams(promise), new IPromiseCallback() {
            @Override
            public void callback(PromiseParams param) throws Exception {
                param.getPromise().resolve(mService.keysExist());
            }
        });
    }

    @ReactMethod
    public void importKeys(final String email, final String password, final String mnemonic, final String passcode, Promise promise) {
        RNParallel.invokeParallel(new PromiseParams(promise), new IPromiseCallback() {
            @Override
            public void callback(PromiseParams param) throws Exception {
                param.getPromise().resolve(mService.importKeys(email, password, mnemonic, passcode));
            }
        });
    }

    @ReactMethod
    public void deleteKeys(Promise promise) {
        RNParallel.invokeParallel(new PromiseParams(promise), new IPromiseCallback() {
            @Override
            public void callback(PromiseParams param) throws Exception {
                param.getPromise().resolve(mService.deleteKeys());
            }
        });
    }

    @ReactMethod
    public void getKeys(final String passcode, Promise promise) {
        RNParallel.invokeParallel(new PromiseParams(promise), new IPromiseCallback() {
            @Override
            public void callback(PromiseParams param) throws Exception {
                param.getPromise().resolve(toJson(mService.getKeys(passcode)));
            }
        });
    }

    @ReactMethod
    public void register(final String login, final String password, final Promise promise) {
        RNParallel.invokeParallel(new PromiseParams(promise), new IPromiseCallback() {
            @Override
            public void callback(PromiseParams param) {
                param.getPromise().resolve(mService.register(login, password));
            }
        });
    }

    //--- FILES ---//

    @ReactMethod
    public void cancelDownload(final double fileRef, final Promise promise) {
        RNParallel.invokeParallel(new PromiseParams(promise), new IPromiseCallback() {
            @Override
            public void callback(PromiseParams param) {
                promise.resolve(toJson(mService.cancelDownload((long)fileRef)));
            }
        });
    }

    @ReactMethod
    public void cancelUpload(final double fileRef, final Promise promise) {
        RNParallel.invokeParallel(new PromiseParams(promise), new IPromiseCallback() {
            @Override
            public void callback(PromiseParams param) {
                promise.resolve(toJson(mService.cancelUpload((long)fileRef)));
            }
        });
    }

    @ReactMethod
    public void getDownloadFolderPath(Promise promise) {
        File downloadDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        SingleResponse response = null;

        if(downloadDir == null || !downloadDir.exists() || !downloadDir.isDirectory()) {
            response = new SingleResponse(false, null,
                    "Unable to retrieve downloads folder path.");
        } else {
            response = new SingleResponse(true, downloadDir.getAbsolutePath(), null);
        }
        
        promise.resolve(toJson(response));
    }
}
