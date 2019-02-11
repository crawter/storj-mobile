package io.storj.mobile.storjlibmodule.rnmodules;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import io.storj.mobile.service.FetchService;
import io.storj.mobile.service.storj.StorjService;
import io.storj.mobile.storjlibmodule.models.PromiseHandler;
import io.storj.mobile.storjlibmodule.services.DownloadService;
import io.storj.mobile.storjlibmodule.services.FetchIntentService;
import io.storj.mobile.storjlibmodule.services.SynchronizationService;
import io.storj.mobile.storjlibmodule.services.UploadService;
import io.storj.mobile.storjlibmodule.utils.WritableMapMapper;

public class ServiceModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    public final static String GET_BUCKETS = "GET_BUCKETS";
    public final static String GET_FILES = "GET_FILES";
    public final static String BUCKET_CREATED = "BUCKET_CREATED";
    public final static String BUCKET_DELETED = "BUCKET_DELETED";
    public final static String FILE_DELETED = "FILE_DELETED";

    private FetchIntentService mFetchIntentService;
    private DownloadService mDownloadService;

    private PromiseHandler mPromise;
    private PromiseHandler mDownloadServicePromise;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BaseReactService baseReactService = ((BaseReactService.BaseReactServiceBinder)service).getService();
            baseReactService.setReactContext(getReactApplicationContext());

            String serviceName = name.getClassName();

            // TODO: please, kill me
            switch (serviceName) {
                case FetchIntentService.SERVICE_NAME:
                    // TODO: and me too
                    mFetchIntentService = (FetchIntentService)baseReactService;
                    mPromise.resolveString(serviceName);
                    break;
                case DownloadService.SERVICE_NAME:
                    mDownloadService = (DownloadService)baseReactService;
                    mDownloadServicePromise.resolveString(serviceName);
                    break;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            switch (name.getClassName()) {
                case FetchIntentService.SERVICE_NAME:
                    mFetchIntentService = null;
                    break;
                case DownloadService.SERVICE_NAME:
                    mDownloadService = null;
                    break;
            }
        }
    };

    public ServiceModule(ReactApplicationContext reactContext)
    {
        super(reactContext);
        mPromise = new PromiseHandler();
        mDownloadServicePromise = new PromiseHandler();

        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "ServiceModule";
    }

    @ReactMethod
    public void bindGetBucketsService(Promise promise) {
        bindService(mPromise, FetchIntentService.class, promise);
    }

    @ReactMethod
    public void bindDownloadService(Promise promise) {
        bindService(mDownloadServicePromise, DownloadService.class, promise);
    }

    @ReactMethod
    public void getBuckets() {
        Intent serviceIntent = new Intent(getReactApplicationContext(), FetchIntentService.class);
        serviceIntent.setAction(GET_BUCKETS);

        getReactApplicationContext().startService(serviceIntent);
    }

    @ReactMethod
    public void uploadFile(String bucketId, String localPath, String fileName) {
        if(bucketId == null || localPath == null) {
            return;
        }

        if(fileName == null) {
            int cut = localPath.lastIndexOf('/');
            if (cut != -1) {
                fileName = localPath.substring(cut + 1);
            }
        }

        Intent uploadIntent = new Intent(getReactApplicationContext(), UploadService.class);

        uploadIntent.setAction(UploadService.ACTION_UPLOAD_FILE);
        uploadIntent.putExtra(UploadService.PARAM_BUCKET_ID, bucketId);
        uploadIntent.putExtra(UploadService.PARAM_LOCAL_PATH, localPath);
        uploadIntent.putExtra(UploadService.PARAM_FILE_NAME, fileName);

        getReactApplicationContext().startService(uploadIntent);
    }

    @ReactMethod
    public void downloadFile(String bucketId, String fileId, String localPath) {
        if(bucketId == null || localPath == null || fileId == null) {
            return;
        }

        Intent downloadIntent = new Intent(getReactApplicationContext(), DownloadService.class);
        downloadIntent.setAction(DownloadService.ACTION_DOWNLOAD_FILE);
        downloadIntent.putExtra(DownloadService.PARAMS_BUCKET_ID, bucketId);
        downloadIntent.putExtra(DownloadService.PARAMS_FILE_ID, fileId);
        downloadIntent.putExtra(DownloadService.PARAMS_LOCAL_PATH, localPath);

        getReactApplicationContext().startService(downloadIntent);
    }


    @ReactMethod
    public void copyFile(String bucketId, String fileId, String localPath, String targetBucketId) {
        if(bucketId == null || localPath == null || fileId == null || targetBucketId == null) {
            return;
        }

        Intent downloadIntent = new Intent(getReactApplicationContext(), DownloadService.class);
        downloadIntent.setAction(DownloadService.ACTION_COPY_FILE);
        downloadIntent.putExtra(DownloadService.PARAMS_BUCKET_ID, bucketId);
        downloadIntent.putExtra(DownloadService.PARAMS_TARGET_BUCKET_ID, targetBucketId);
        downloadIntent.putExtra(DownloadService.PARAMS_FILE_ID, fileId);
        downloadIntent.putExtra(DownloadService.PARAMS_LOCAL_PATH, localPath);

        getReactApplicationContext().startService(downloadIntent);
    }

    @ReactMethod
    public void getFiles(String bucketId) {
        Intent serviceIntent = new Intent(getReactApplicationContext(), FetchIntentService.class);
        serviceIntent.setAction(GET_FILES);
        serviceIntent.putExtra("bucketId", bucketId);

        getReactApplicationContext().startService(serviceIntent);
    }

    @ReactMethod
    public void createBucket(final String bucketName) {
        Intent serviceIntent = new Intent(getReactApplicationContext(), FetchIntentService.class);
        serviceIntent.setAction(BUCKET_CREATED);
        serviceIntent.putExtra("bucketName", bucketName);

        getReactApplicationContext().startService(serviceIntent);
    }

    @ReactMethod
    public void cancelSync() {
        Intent cancelSyncIntent = new Intent(getReactApplicationContext(), SynchronizationService.class);
        cancelSyncIntent.setAction(SynchronizationService.ACTION_SYNC_CANCEL);

        getReactApplicationContext().startService(cancelSyncIntent);
    }

    @ReactMethod
    public void deleteBucket(final String bucketId) {
        Intent serviceIntent = new Intent(getReactApplicationContext(), FetchIntentService.class);
        serviceIntent.setAction(BUCKET_DELETED);
        serviceIntent.putExtra("bucketId", bucketId);

        getReactApplicationContext().startService(serviceIntent);
    }

    @ReactMethod
    public void deleteFile(final String bucketId, final String fileId) {
        Intent serviceIntent = new Intent(getReactApplicationContext(), FetchIntentService.class);
        serviceIntent.setAction(FILE_DELETED);
        serviceIntent.putExtra("bucketId", bucketId);
        serviceIntent.putExtra("fileId", fileId);

        getReactApplicationContext().startService(serviceIntent);
    }

    @ReactMethod
    public void removeFileFromSyncQueue(int id) {
        Intent removeFromQueueIntent = new Intent(getReactApplicationContext(), SynchronizationService.class);
        removeFromQueueIntent.setAction(SynchronizationService.ACTION_REMOVE_FROM_QUEUE);
        removeFromQueueIntent.putExtra(UploadService.PARAM_SYNC_ENTRY_ID, id);

        getReactApplicationContext().startService(removeFromQueueIntent);
    }

    @ReactMethod
    public void startSync() {
        Intent startSyncIntent = new Intent(getReactApplicationContext(), SynchronizationService.class);
        startSyncIntent.setAction(SynchronizationService.ACTION_SYNC);

        getReactApplicationContext().startService(startSyncIntent);
    }

    private void bindService(PromiseHandler handler, Class<? extends BaseReactService> serviceClass, Promise promise) {
        handler.setPromise(promise);

        Intent intent = new Intent(getReactApplicationContext(), serviceClass);
        getReactApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onActivityResult(Activity activity, int i, int i1, Intent intent) {

    }

    @Override
    public void onNewIntent(Intent intent) {
        if(intent == null || !intent.getAction().equals("ACTION_EVENT")) {
            return;
        }

        String eventName = intent.getStringExtra("eventName");
        ContentValues data = intent.getParcelableExtra("data");

        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, WritableMapMapper.get(data));
    }
}
