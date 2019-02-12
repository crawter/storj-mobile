package io.storj.mobile.storjlibmodule.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import io.storj.libstorj.Storj;
import io.storj.libstorj.android.StorjAndroid;
import io.storj.mobile.dataprovider.Database;
import io.storj.mobile.service.FetchService;
import io.storj.mobile.service.storj.StorjService;
import io.storj.mobile.storjlibmodule.rnmodules.BaseReactService;

import static io.storj.mobile.storjlibmodule.rnmodules.ServiceModule.GET_BUCKETS;
import static io.storj.mobile.storjlibmodule.rnmodules.ServiceModule.GET_FILES;
import static io.storj.mobile.storjlibmodule.rnmodules.ServiceModule.BUCKET_CREATED;
import static io.storj.mobile.storjlibmodule.rnmodules.ServiceModule.BUCKET_DELETED;
import static io.storj.mobile.storjlibmodule.rnmodules.ServiceModule.FILE_DELETED;

public class FetchIntentService extends BaseReactService {
    public final static String SERVICE_NAME = "io.storj.mobile.storjlibmodule.services.FetchIntentService";

    private final static String EVENT_BUCKETS_UPDATED = "EVENT_BUCKETS_UPDATED";
    private final static String EVENT_FILES_UPDATED = "EVENT_FILES_UPDATED";
    private final static String EVENT_BUCKET_CREATED = "EVENT_BUCKET_CREATED";
    private final static String EVENT_BUCKET_DELETED = "EVENT_BUCKET_DELETED";
    private final static String EVENT_FILE_DELETED = "EVENT_FILE_DELETED";

    private static final String TAG = "FetchIntentService";

    private FetchService mService;

    public FetchIntentService() {
        super("FetchIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Storj storj;
        try {
            storj = StorjAndroid.getInstance(this, "https://api.v2.storj.io");
        } catch(Exception e) {
            // TODO: add proper logging, notifications, send error intent
            // redirect user to error activity and close app
            this.stopSelf();

            // GRACEFULLY CLOSE APP :D
            mService.createBucket("");
            return;
        }

        StorjService service = new StorjService(storj);
        mService = new FetchService(service, new Database(this, null));
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null) {
            Log.d(TAG, "onHandleIntent: Intent is null");
        }

        String action = intent.getAction();
        Log.d(TAG, "onHandleIntent: " + action);

        switch(action) {
            case GET_BUCKETS:
                getBuckets();
                break;
            case GET_FILES:
                getFiles(intent.getStringExtra("bucketId"));
                break;
            case BUCKET_CREATED:
                createBucket(intent.getStringExtra("bucketName"));
                break;
            case BUCKET_DELETED:
                deleteBucket(intent.getStringExtra("bucketId"));
                break;
            case FILE_DELETED:
                deleteFile(intent.getStringExtra("bucketId"), intent.getStringExtra("fileId"));
                break;
        }

        Log.d(TAG, "onHandleIntentEND: " + action);
    }

    private void getBuckets() {
        sendEvent(EVENT_BUCKETS_UPDATED, mService.getBuckets().isSuccess());
    }

    private void getFiles(final String bucketId) {
        sendEvent(EVENT_FILES_UPDATED, mService.getFiles(bucketId).isSuccess());
    }

    private void createBucket(final String bucketName) {
        sendEvent(EVENT_BUCKET_CREATED, mService.createBucket(bucketName).isSuccess());
    }

    private void deleteBucket(final String bucketId) {
        sendEvent(EVENT_BUCKET_DELETED, mService.deleteBucket(bucketId).isSuccess());
    }

    private void deleteFile(final String bucketId, final String fileId) {
        sendEvent(EVENT_FILE_DELETED, mService.deleteFile(bucketId, fileId).isSuccess());
    }
}
