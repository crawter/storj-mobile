package io.storj.mobile.storjlibmodule.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import io.storj.libstorj.KeysNotFoundException;
import io.storj.libstorj.Storj;
import io.storj.libstorj.android.StorjAndroid;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.dataprovider.Database;
import io.storj.mobile.service.FetchService;
import io.storj.mobile.service.storj.StorjService;
import io.storj.mobile.storjlibmodule.models.FileDeleteModel;
import io.storj.mobile.storjlibmodule.rnmodules.BaseReactService;

import static io.storj.mobile.storjlibmodule.rnmodules.ServiceModule.BUCKET_CREATED;
import static io.storj.mobile.storjlibmodule.rnmodules.ServiceModule.BUCKET_DELETED;
import static io.storj.mobile.storjlibmodule.rnmodules.ServiceModule.FILE_DELETED;
import static io.storj.mobile.storjlibmodule.rnmodules.ServiceModule.GET_BUCKETS;
import static io.storj.mobile.storjlibmodule.rnmodules.ServiceModule.GET_FILES;

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
            //mService.createBucket("");
            return;
        }

        StorjService service = new StorjService(storj);
        mService = new FetchService(service, Database.getInstance());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null) {
            Log.d(TAG, "onHandleIntent: Intent is null");
        }

        String action = intent.getAction();
        Log.d(TAG, "onHandleIntent: " + action);

        // TODO: move exception handling here
        // as we don't care which method throw
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
        try {
            sendEvent(EVENT_BUCKETS_UPDATED, mService.getBuckets().isSuccess());
        } catch (KeysNotFoundException ex) {
            // TODO: finish handling keys not found exception, send
            // a toast that will ask user to sign out and sign in again
            sendEvent(EVENT_BUCKETS_UPDATED, false);
        } catch (InterruptedException ex) {
            //TODO: handle InterruptedException, no need to send event
        }
    }

    private void getFiles(final String bucketId) {
        try {
            Response getFileResponse = mService.getFiles(bucketId);
            SingleResponse<String> result = new SingleResponse<String>(
                    bucketId,
                    getFileResponse.isSuccess(),
                    getFileResponse.getError().getMessage()
            );

            sendEvent(EVENT_FILES_UPDATED, toJson(result));
        } catch (KeysNotFoundException ex) {
            // TODO: finish handling keys not found exception, send
            // a toast that will ask user to sign out and sign in again
            sendEvent(EVENT_FILES_UPDATED, toJson(new SingleResponse<String>(null, false, "keys not found")));
        } catch (InterruptedException ex) {
            //TODO: handle InterruptedException, no need to send event
        }
    }

    private void createBucket(final String bucketName) {
        try {
            sendEvent(EVENT_BUCKET_CREATED, toJson(mService.createBucket(bucketName)));
        } catch (KeysNotFoundException ex) {
            // TODO: finish handling keys not found exception, send
            // a toast that will ask user to sign out and sign in again
            sendEvent(EVENT_BUCKET_CREATED, toJson(new Response(false, "keys not found")));
        } catch (InterruptedException ex) {
            //TODO: handle InterruptedException, no need to send event
        }
    }

    private void deleteBucket(final String bucketId) {
        try {
            Response deleteBucketResponse = mService.deleteBucket(bucketId);
            if (!deleteBucketResponse.isSuccess()) {
                sendEvent(EVENT_BUCKET_DELETED, toJson(deleteBucketResponse));
            }

            sendEvent(EVENT_BUCKET_DELETED, toJson(new SingleResponse<>(bucketId, true, null)));
        } catch (KeysNotFoundException ex) {
            // TODO: finish handling keys not found exception, send
            // a toast that will ask user to sign out and sign in again
            sendEvent(EVENT_BUCKET_DELETED, toJson(new Response(false, "keys not found")));
        } catch (InterruptedException ex) {
            //TODO: handle InterruptedException, no need to send event
        }
    }

    private void deleteFile(final String bucketId, final String fileId) {
        try {
            Response fileDeleteResponse = mService.deleteFile(bucketId, fileId);
            if (!fileDeleteResponse.isSuccess()) {
                sendEvent(EVENT_FILE_DELETED, toJson(fileDeleteResponse));
            }

            sendEvent(EVENT_FILE_DELETED, toJson(new SingleResponse<>(
                    new FileDeleteModel(bucketId, fileId),
                    true,
                    null
            )));
        } catch (KeysNotFoundException ex) {
            // TODO: finish handling keys not found exception, send
            // a toast that will ask user to sign out and sign in again
            sendEvent(EVENT_BUCKET_DELETED, toJson(new Response(false, "keys not found")));
        } catch (InterruptedException ex) {
            //TODO: handle InterruptedException, no need to send event
        }
    }
}
