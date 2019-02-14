package io.storj.mobile.storjlibmodule.rnmodules;

import android.os.Environment;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.File;

import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.service.storj.StorjService;

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

    @ReactMethod
    public void generateMnemonic(final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mService.generateMnemonic());
            }
        }).run();
    }

    @ReactMethod
    public void checkMnemonic(final String mnemonic, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mService.checkMnemonic(mnemonic));
            }
        }).run();
    }

    @ReactMethod
    public void verifyKeys(final String email, final String password, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int errorCode = 1;
                try {
                    errorCode = mService.verifyKeys(email, password);
                } catch (InterruptedException ex) {
                    //TODO: handle InterruptedException
                } finally {
                    promise.resolve(errorCode);
                }
            }
        }).run();
    }

    @ReactMethod
    public void keysExists(final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mService.keysExist());
            }
        }).run();
    }

    @ReactMethod
    public void importKeys(final String email, final String password, final String mnemonic, final String passcode, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mService.importKeys(email, password, mnemonic, passcode));
            }
        }).run();
    }

    @ReactMethod
    public void deleteKeys(final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mService.deleteKeys());
            }
        }).run();
    }

    @ReactMethod
    public void getKeys(final String passcode, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mService.getKeys(passcode).toJson());
            }
        }).run();
    }

    @ReactMethod
    public void register(final String login, final String password, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mService.register(login, password));
            }
        }).run();
    }

    //--- FILES ---//

    @ReactMethod
    public void cancelDownload(final double fileRef, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mService.cancelDownload((long)fileRef));
            }
        }).run();
    }

    @ReactMethod
    public void cancelUpload(final double fileRef, final Promise promise) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                promise.resolve(mService.cancelUpload((long)fileRef));
            }
        }).run();
    }

    @ReactMethod
    public void getDownloadFolderPath(Promise promise) {
        File downloadDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        SingleResponse<String> response = null;

        if(downloadDir == null || !downloadDir.exists() || !downloadDir.isDirectory()) {
            response = new SingleResponse<>(null, false,
                    "Unable to retrieve downloads folder path.");
        } else {
            response = new SingleResponse<>(downloadDir.getAbsolutePath(),true, null);
        }
        
        promise.resolve(response.toJson());
    }
}
